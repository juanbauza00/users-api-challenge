package org.example.services.implementation;

import org.example.models.Client;
import org.example.models.Group;
import org.example.repositories.GroupRepository;
import org.example.services.interfaces.ClientService;
import org.example.services.interfaces.GroupService;
import org.example.services.interfaces.OwnerService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final OwnerService ownerService;
    private final ClientService clientService;

    public GroupServiceImpl(GroupRepository groupRepository, OwnerService ownerService, ClientService clientService) {
        this.groupRepository = groupRepository;
        this.ownerService = ownerService;
        this.clientService = clientService;
    }

    private void validateId(Long groupId) {
        if (groupId == null || groupId <= 0) {
            throw new IllegalArgumentException("El id debe ser mayor a cero");
        }
    }

    private void validateParticularId(Integer particularId) {
        if (particularId == null || particularId <= 0) {
            throw new IllegalArgumentException("El id particular debe ser mayor a cero");
        }
    }

    private void validateGroup(Group group) {
        validateId(group.getOwnerId());
        if (group.getNombre() == null || group.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre del grupo no puede estar vacio");
        }
        if (!ownerService.existsById(group.getOwnerId())) {
            throw new IllegalArgumentException("No existe un owner con el id " + group.getOwnerId());
        }
    }

    @Override
    @Transactional
    public Group createGroup(Group group) {
        validateGroup(group);
        return groupRepository.save(group);
    }

    @Override
    public Group getGroupById(Long id) {
        validateId(id);
        return groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("El id: " + id + " no existe."));
    }

    @Override
    public List<Group> getGroupsByOwnerId(Long ownerId) {
        validateId(ownerId);
        if (!ownerService.existsById(ownerId)) {
            throw new EntityNotFoundException("No existe un owner con el id " + ownerId);
        }
        return groupRepository.findAllByOwnerId(ownerId);
    }

    @Override
    @Transactional
    public Group updateGroup(Long id, Group group) {
        validateId(id);
        validateGroup(group);

        Group dbGroup = getGroupById(id);
        if (!dbGroup.getOwnerId().equals(group.getOwnerId())) {
            throw new EntityNotFoundException("El grupo id: " + id + " no pertenece al owner id: "+ group.getOwnerId());
        }
        group.setId(id);
        return groupRepository.update(group);
    }

    @Override
    @Transactional
    public boolean deleteGroup(Long id) {
        validateId(id);
        if (!existsById(id)) {
            throw new EntityNotFoundException("No existe el grupo con id: " + id);
        }
        return groupRepository.deleteGroupById(id);
    }

    @Override
    public boolean existsById(Long id) {
        validateId(id);
        return groupRepository.findById(id).isPresent();
    }

    @Override
    public boolean groupBelongsToOwner(Long groupId, Long ownerId) {
        validateId(groupId);
        validateId(ownerId);

        Optional<Group> groupOpt = groupRepository.findById(groupId);
        if (groupOpt.isPresent()) {
            return groupOpt.get().getOwnerId().equals(ownerId);
        }
        return false;
    }

    @Override
    @Transactional
    public void addClientToGroup(Integer particularId, Long groupId) {
        validateId(groupId);
        validateParticularId(particularId);

        Group group = getGroupById(groupId);
        Client client = clientService.getClientByOwnerParticularId(particularId, group.getOwnerId());

        try {
            groupRepository.addClientToGroup(client.getId(), groupId);
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException("El cliente " + particularId + " ya pertenece al grupo " + groupId);
        } catch (Exception e) {
            throw new RuntimeException("Error al agregar el cliente " + particularId + " al grupo " + groupId + ": " + e.getMessage());
        }
    }

    @Override
    public List<Integer> getClientParticularIdsByGroup(Long groupId) {
        validateId(groupId);
        if (!existsById(groupId)) {
            throw new EntityNotFoundException("No existe el grupo con id: " + groupId);
        }
        List<Long> clientsIds = groupRepository.getClientIdsByGroup(groupId);
        List<Integer> clientsParticularIdsList = new ArrayList<>();
        for (Long clientId : clientsIds) {
            clientsParticularIdsList.add(clientService.getParticularIdByClientId(clientId));
        }
        return clientsParticularIdsList;
    }

    @Override
    @Transactional
    public boolean removeClientFromGroup(Integer clientParticularId, Long groupId) {
        validateParticularId(clientParticularId);
        validateId(groupId);
        if (!existsById(groupId)) {
            throw new EntityNotFoundException("No existe el grupo con id: " + groupId);
        }
        Group group = getGroupById(groupId);
        Client client = clientService.getClientByOwnerParticularId(clientParticularId, group.getOwnerId());

        try {
            return groupRepository.removeClientFromGroup(client.getId(), groupId);
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException("El cliente " + clientParticularId + " no pertenece al grupo " + groupId);
        }
    }
}
