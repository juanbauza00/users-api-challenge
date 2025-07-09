package org.example.services.interfaces;


import org.example.dtos.ClientInputDto;
import org.example.models.Client;

import java.util.List;

public interface ClientService {
    // CREATE
    Client createClient(ClientInputDto clientDto, Long ownerId);

    // CREATE (BATCH)
    List<Client> createClientBatch(List<ClientInputDto> clientDtos, Long ownerId);

    // GET ONE
    Client getClientByOwnerParticularId(Integer particularId, Long ownerId);

    // GET PARTICULAR ID
    Integer getParticularIdByClientId(Long clientId);

    // GET ALL BY OWNER ID
    List<Client> getClientsByOwnerId(Long ownerId);

    // UPDATE
    Client updateClient(Long ownerId, Integer particularId, ClientInputDto clientDto);

    // DELETE
    boolean deleteClient(Integer particularId, Long ownerId);

    // EXIST
    boolean existsByParticularIdAndOwnerId(Integer particularId, Long ownerId);
}
