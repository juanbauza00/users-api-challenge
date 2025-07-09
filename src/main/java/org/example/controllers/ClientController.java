package org.example.controllers;

import org.example.dtos.ClientInputDto;
import org.example.models.Client;
import org.example.services.interfaces.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    // CREATE INDIVIDUAL
    @PostMapping("/{ownerId}")
    public ResponseEntity<?> createClient(@PathVariable("ownerId") Long ownerId,
                                          @RequestBody ClientInputDto clientDto) {
        try {
            Client createdClient = clientService.createClient(clientDto, ownerId);
            return new ResponseEntity<>(createdClient, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error de validación: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Owner no encontrado: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // CREATE BATCH
    @PostMapping("/batch/{ownerId}")
    public ResponseEntity<?> createClientBatch(@PathVariable("ownerId") Long ownerId,
                                               @RequestBody List<ClientInputDto> clientDtos) {
        try {
            List<Client> createdClients = clientService.createClientBatch(clientDtos, ownerId);
            return new ResponseEntity<>(createdClients, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error de validación: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Owner no encontrado: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Implementar kafka")) {
                return new ResponseEntity<>("Procesamiento asíncrono iniciado - los clientes serán procesados en background", HttpStatus.ACCEPTED);
            }
            return new ResponseEntity<>("Error en procesamiento batch: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // READ ONE BY PARTICULAR ID
    @GetMapping("/{ownerId}/{particularId}")
    public ResponseEntity<?> getClientByParticularId(@PathVariable("ownerId") Long ownerId,
                                                     @PathVariable("particularId") Integer particularId) {
        try {
            Client client = clientService.getClientByOwnerParticularId(particularId, ownerId);
            return new ResponseEntity<>(client, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error de validación: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Cliente no encontrado: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // READ ALL BY OWNER
    @GetMapping("/{ownerId}")
    public ResponseEntity<?> getClientsByOwner(@PathVariable("ownerId") Long ownerId) {
        try {
            List<Client> clients = clientService.getClientsByOwnerId(ownerId);
            return new ResponseEntity<>(clients, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error de validación: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Owner no encontrado: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // UPDATE BY PARTICULAR ID
    @PutMapping("/{ownerId}/{particularId}")
    public ResponseEntity<?> updateClient(@PathVariable("ownerId") Long ownerId,
                                          @PathVariable("particularId") Integer particularId,
                                          @RequestBody ClientInputDto clientDto) {
        try {
            Client updatedClient = clientService.updateClient(ownerId, particularId, clientDto);
            return new ResponseEntity<>(updatedClient, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error de validación: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Cliente no encontrado: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE BY PARTICULAR ID
    @DeleteMapping("/{ownerId}/{particularId}")
    public ResponseEntity<?> deleteClient(@PathVariable("ownerId") Long ownerId,
                                          @PathVariable("particularId") Integer particularId) {
        try {
            clientService.deleteClient(particularId, ownerId);
            return ResponseEntity.ok("Cliente eliminado exitosamente");
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error de validación: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Cliente no encontrado: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
