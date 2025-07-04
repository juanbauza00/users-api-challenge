package org.example.services.implementation;

import org.example.models.Client;
import org.example.repositories.ClientRepository;
import org.example.services.interfaces.ClientService;
import org.example.services.interfaces.OwnerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final OwnerService ownerService;

    @Value("${app.batch.threshold:10}") // Toma el valor configurado y sino define por defecto 10
    private int batchThreshold;

    public ClientServiceImpl(ClientRepository clientRepository, OwnerService ownerService) {
        this.clientRepository = clientRepository;
        this.ownerService = ownerService;
    }

    private void validateOwnerId(Long ownerId) {
        if (ownerId == null || ownerId <= 0) {
            throw new IllegalArgumentException("El owner ID debe ser un número positivo");
        }
        if (!ownerService.existsById(ownerId)) {
            throw new IllegalArgumentException("No existe un owner con el id " + ownerId);
        }
    }

    private void validateParticularId(Integer particularId) {
        if (particularId == null || particularId <= 0) {
            throw new IllegalArgumentException("El ID particular debe ser un número positivo");
        }
    }

    private void validateClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Los datos del cliente no pueden ser nulos");
        }
        if (client.getNombre() == null || client.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio");
        }
        if (client.getApellido() == null || client.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido del cliente es obligatorio");
        }
        if (client.getFechaNacimiento() == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria");
        }
        if (client.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
        }
        validateOwnerId(client.getOwnerId());
    }

    private void validateClientBatch(List<Client> clients, Long ownerId) {
        if (clients == null || clients.isEmpty()) {
            throw new IllegalArgumentException("La lista de clientes no puede estar vacía");
        }
        validateOwnerId(ownerId);

        for (Client client : clients) {
            try {
                validateClient(client);
                if (!client.getOwnerId().equals(ownerId)) {
                    throw new IllegalArgumentException("El cliente: " + client.getNombre() + " " +client.getApellido() + " no pertenece al owner especificado");
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Error en cliente " + client.getNombre() + " " +client.getApellido() + ": " + e.getMessage());
            }
        }
    }


    @Override
    public Client createClient(Client client) {
        validateClient(client);
        client.setFechaCreacion(LocalDateTime.now());
        client.setActivo(true);
        return clientRepository.save(client);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // Aplica rollback el generarse una exepcion
    public List<Client> createClientBatch(List<Client> clients, Long ownerId) {
        validateClientBatch(clients, ownerId);
        // Toma desicion de proceso asincronico
        if (clients.size() > batchThreshold) {
            throw new RuntimeException("Implementar kafka");
            // todo: Implementar kafka
        }

        List<Client> createdClients = new ArrayList<>();
        for (Client client : clients) {
            client.setFechaCreacion(LocalDateTime.now());
            client.setActivo(true);
            try {
                Client savedClient = clientRepository.save(client);
                createdClients.add(savedClient);
            } catch (Exception e) {
                throw new RuntimeException("Error al guardar cliente: " + client.getNombre() + " " + client.getApellido() + ". " + e.getMessage());
            }
        }
        return createdClients;
    }

    @Override
    public Client getClientByOwnerParticularId(Integer particularId, Long ownerId) {
        validateParticularId(particularId);
        validateOwnerId(ownerId);

        return clientRepository.findByOwnerParticularId(ownerId, particularId)
                .orElseThrow(() -> new IllegalArgumentException(
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
            throw new IllegalArgumentException("No se encontró cliente con ID " + clientId);
        }
    }

    @Override
    public List<Client> getClientsByOwnerId(Long ownerId) {
        validateOwnerId(ownerId);
        return clientRepository.findAllByOwnerId(ownerId);
    }

    @Override
    public Client updateClient(Long ownerId, Client clientData) {
        validateParticularId(clientData.getParticularId());
        validateOwnerId(ownerId);
        validateClient(clientData);

        Client existingClient = getClientByOwnerParticularId(clientData.getParticularId(), ownerId);

        if (!existingClient.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException(
                    "El owner con ID " + ownerId + " no cuenta con el cliente id " + clientData.getParticularId());
        }

        // Se actualizan camps modificables
        existingClient.setNombre(clientData.getNombre());
        existingClient.setApellido(clientData.getApellido());
        existingClient.setFechaNacimiento(clientData.getFechaNacimiento());
        return clientRepository.update(existingClient);
    }

    @Override
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
}
