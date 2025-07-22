package org.example.services.implementation;

import org.example.dtos.ClientInputDto;
import org.example.models.Client;
import org.example.repositories.ClientRepository;
import org.example.services.interfaces.ClientService;
import org.example.services.interfaces.OwnerService;
import org.example.services.kafka.ClientBatchProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

    private static final Logger log = LoggerFactory.getLogger(ClientBatchProducerService.class);
    private final ClientRepository clientRepository;
    private final OwnerService ownerService;
    private final ClientBatchProducerService clientBatchProducerService;

    @Value("${app.batch.threshold:10}") // Toma el valor configurado y sino define por defecto 10
    private int batchThreshold;

    public ClientServiceImpl(ClientRepository clientRepository, OwnerService ownerService,
                             ClientBatchProducerService clientBatchProducerService) {
        this.clientRepository = clientRepository;
        this.ownerService = ownerService;
        this.clientBatchProducerService = clientBatchProducerService;
    }

    private void validateOwnerId(Long ownerId) {
        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("El owner ID debe ser un número positivo");
        }
        if (!ownerService.existsById(ownerId)) {
            throw new EntityNotFoundException("No existe un owner con el id " + ownerId);
        }
    }

    private void validateParticularId(Integer particularId) {
        if (particularId == null || particularId <= 0) {
            throw new IllegalArgumentException("El ID particular debe ser un número positivo");
        }
    }

    private void validateClientInputDto(ClientInputDto clientDto) {
        if (clientDto == null) {
            throw new IllegalArgumentException("Los datos del cliente no pueden ser nulos");
        }
        if (clientDto.getNombre() == null || clientDto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio");
        }
        if (clientDto.getApellido() == null || clientDto.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido del cliente es obligatorio");
        }
        if (clientDto.getFechaNacimiento() == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria");
        }
        if (clientDto.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
        }
    }

    private void validateClientBatch(List<ClientInputDto> clientDtos, Long ownerId) {
        if (clientDtos == null || clientDtos.isEmpty()) {
            throw new IllegalArgumentException("La lista de clientes no puede estar vacía");
        }
        for (ClientInputDto clientDto : clientDtos) {
            validateClientInputDto(clientDto);
        }
    }

    private Client mapClientDtoInputToClient(ClientInputDto clientDto) {
        Client client = new Client();
        client.setNombre(clientDto.getNombre());
        client.setApellido(clientDto.getApellido());
        client.setFechaNacimiento(clientDto.getFechaNacimiento());
        return client;
    }

    // CREATE
    @Override
    @Transactional
    public Client createClient(ClientInputDto clientDto, Long ownerId) {
        validateClientInputDto(clientDto);
        Client client = mapClientDtoInputToClient(clientDto);
        client.setOwnerId(ownerId);
        client.setFechaCreacion(LocalDateTime.now());
        client.setActivo(true);
        return clientRepository.save(client);
    }

    // CREATE BATCH
    @Override
    @Transactional(rollbackFor = Exception.class) // Aplica rollback el generarse una exepcion
    public List<Client> createClientBatch(List<ClientInputDto> clientDtos, Long ownerId) {

        validateClientBatch(clientDtos, ownerId);

        // Toma desicion de proceso asincronico
        if (clientDtos.size() > batchThreshold) {
            log.info("Lote supera la cant. de: {}, enviando a procesamiento asíncrono - Owner: {}, Clientes: {}",
                    batchThreshold, ownerId, clientDtos.size());

            try {
                String batchId = clientBatchProducerService.sendClientBatch(clientDtos, ownerId);
                log.info("Lote enviado a Kafka exitosamente - BatchId: {}, Owner: {}", batchId, ownerId);

                // Retornar lista vacía para indicar procesamiento asíncrono
                // El controlador maneje la respuesta
                return new ArrayList<>();

            } catch (Exception e) {
                log.error("Error enviando lote a Kafka - Owner: {}, Error: {}", ownerId, e.getMessage(), e);
                throw new RuntimeException("Error al procesar lote de clientes de forma asíncrona: " + e.getMessage());
            }
        }

        LocalDateTime batchTime = LocalDateTime.now();
        List<Client> createdClients = new ArrayList<>();
        for (ClientInputDto clientDto : clientDtos) {
            Client client = mapClientDtoInputToClient(clientDto);
            client.setOwnerId(ownerId);
            client.setFechaCreacion(batchTime);
            client.setActivo(true);
            try {
                Client savedClient = clientRepository.save(client);
                createdClients.add(savedClient);
            } catch (Exception e) {
                throw new RuntimeException("Error al guardar cliente: " + client.getNombre() + " " + client.getApellido() + ". " + e.getMessage());
            }
        }
        log.info("Lote procesado sincrónicamente - Owner: {}, Clientes guardados: {}", ownerId, createdClients.size());
        return createdClients;
    }

    // READ ONE (Client)
    @Override
    public Client getClientByOwnerParticularId(Integer particularId, Long ownerId) {
        validateParticularId(particularId);
        validateOwnerId(ownerId);

        return clientRepository.findByOwnerParticularId(ownerId, particularId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se encontró cliente con ID particular " + particularId + " para el owner " + ownerId ));
    }

    @Override
    public Integer getParticularIdByClientId(Long clientId) {
        if (clientId == null || clientId <= 0) {
            throw new IllegalArgumentException("El client ID debe ser un numero positivo");
        }
        try {
            return clientRepository.getParticularIdByClientId(clientId);
        } catch (Exception e) {
            throw new EntityNotFoundException("No se encontró cliente con ID " + clientId);
        }
    }

    @Override
    public List<Client> getClientsByOwnerId(Long ownerId) {
        validateOwnerId(ownerId);
        return clientRepository.findAllByOwnerId(ownerId);
    }

    @Override
    @Transactional
    public Client updateClient(Long ownerId, Integer particularId, ClientInputDto clientDto) {
        validateParticularId(particularId);
        validateOwnerId(ownerId);
        validateClientInputDto(clientDto);


        Client existingClient = getClientByOwnerParticularId(particularId, ownerId);

        if (!existingClient.getOwnerId().equals(ownerId)) {
            throw new EntityNotFoundException(
                    "El owner con ID " + ownerId + " no cuenta con el cliente id " + particularId);
        }
        Client client = mapClientDtoInputToClient(clientDto);
        client.setParticularId(particularId);
        client.setOwnerId(ownerId);
        return clientRepository.update(client);
    }

    @Override
    @Transactional
    public boolean deleteClient(Integer particularId, Long ownerId) {
        validateParticularId(particularId);
        validateOwnerId(ownerId);
        Client client = getClientByOwnerParticularId(particularId, ownerId);
        return clientRepository.deleteById(client.getId());
    }

    @Override
    public boolean existsByParticularIdAndOwnerId(Integer particularId, Long ownerId) {
        validateParticularId(particularId);
        validateOwnerId(ownerId);
        return clientRepository.getClientId(ownerId, particularId).isPresent();
    }

    // Método UNICAMENTE PARA CONSUMER
    @Transactional(rollbackFor = Exception.class)
    public List<Client> createClientBatchDirect(List<ClientInputDto> clientDtos, Long ownerId) {
        validateClientBatch(clientDtos, ownerId);

        LocalDateTime batchTime = LocalDateTime.now();
        List<Client> createdClients = new ArrayList<>();

        for (ClientInputDto clientDto : clientDtos) {
            Client client = mapClientDtoInputToClient(clientDto);
            client.setOwnerId(ownerId);
            client.setFechaCreacion(batchTime);
            client.setActivo(true);

            try {
                Client savedClient = clientRepository.save(client);
                createdClients.add(savedClient);
            } catch (Exception e) {
                throw new RuntimeException("Error al guardar cliente: " + client.getNombre() + " " + client.getApellido() + ". " + e.getMessage());
            }
        }

        log.info("Lote procesado directamente - Owner: {}, Clientes guardados: {}", ownerId, createdClients.size());
        return createdClients;
    }
}
