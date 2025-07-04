package org.example.services.interfaces;


import org.example.models.Client;

import java.util.List;

public interface ClientService {
    // CREATE
    Client createClient(Client client);

    // CREATE (BATCH)
    List<Client> createClientBatch(List<Client> clients, Long ownerId);

    // GET ONE
    Client getClientByOwnerParticularId(Integer particularId, Long ownerId);

    // GET ALL BY OWNER ID
    List<Client> getClientsByOwnerId(Long ownerId);

    // UPDATE
    Client updateClient(Integer particularId, Long ownerId, Client clientData);

    // DELETE
    boolean deleteClient(Integer particularId, Long ownerId);

    // EXIST
    boolean existsByParticularIdAndOwnerId(Integer particularId, Long ownerId);
}
