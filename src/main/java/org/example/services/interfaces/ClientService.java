package org.example.services.interfaces;


import org.example.models.Client;

import java.util.List;

public interface ClientService {
    // CREATE
    Client createClient(Client client);

    // CREATE (BATCH)
    List<Client> createClientBatch(List<Client> clients, Long ownerId);

    // GET ONE
    Client getClientById(Long id);

    // GET ALL BY OWNER ID
    List<Client> getClientsByOwnerId(Long ownerId);

    Client updateClient(Client client);

    Boolean deleteClientById(Long id);

    Boolean existById(Long id);

    Boolean clientBelongsToOwner(Client client, Long ownerId);




}
