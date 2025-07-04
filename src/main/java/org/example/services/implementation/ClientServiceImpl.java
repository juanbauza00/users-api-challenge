package org.example.services.implementation;

import org.example.models.Client;
import org.example.repositories.ClientRepository;
import org.example.services.interfaces.ClientService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Client createClient(Client client) {
        return null;
    }

    @Override
    public List<Client> createClientBatch(List<Client> clients, Long ownerId) {
        return Collections.emptyList();
    }

    @Override
    public Client getClientByOwnerParticularId(Integer particularId, Long ownerId) {
        return null;
    }

    @Override
    public List<Client> getClientsByOwnerId(Long ownerId) {
        return Collections.emptyList();
    }

    @Override
    public Client updateClient(Integer particularId, Long ownerId, Client clientData) {
        return null;
    }

    @Override
    public boolean deleteClient(Integer particularId, Long ownerId) {
        return false;
    }

    @Override
    public boolean existsByParticularIdAndOwnerId(Integer particularId, Long ownerId) {
        return false;
    }
}
