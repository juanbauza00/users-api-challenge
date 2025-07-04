package org.example.services.implementation;

import org.example.models.Group;
import org.example.repositories.GroupRepository;
import org.example.services.interfaces.GroupService;
import org.example.services.interfaces.OwnerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final OwnerService ownerService;

    public GroupServiceImpl(GroupRepository groupRepository, OwnerService ownerService) {
        this.groupRepository = groupRepository;
        this.ownerService = ownerService;
    }

    private void validateId(Long groupId) {
        if (groupId == null || groupId <= 0) {
            throw new IllegalArgumentException("El id debe ser mayor a cero");
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
    public Group createGroup(Group group) {
        validateGroup(group);
        return groupRepository.save(group);
    }

    @Override
    public Group getGroupById(Long id) {
        validateId(id);
        return groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El id: " + id + " no existe."));
    }

    @Override
    public List<Group> getGroupsByOwnerId(Long ownerId) {
        validateId(ownerId);
        if (!ownerService.existsById(ownerId)) {
            throw new IllegalArgumentException("No existe un owner con el id " + ownerId);
        }
        return groupRepository.findAllByOwnerId(ownerId);
    }

    @Override
    public Group updateGroup(Long id, Group group) {
        validateId(id);
        validateGroup(group);

        Group dbGroup = getGroupById(id);
        if (!dbGroup.getOwnerId().equals(group.getOwnerId())) {
            throw new IllegalArgumentException("El grupo id: " + id + " no pertenece al owner id: "+ group.getOwnerId());
        }
        group.setId(id);
        return groupRepository.update(group);
    }

    @Override
    public boolean deleteGroup(Long id) {
        validateId(id);
        if (!existsById(id)) {
            throw new IllegalArgumentException("No existe el grupo con id: " + id);
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
    public void addClientToGroup(Long clientId, Long groupId) {
        validateId(clientId);
        validateId(groupId);

        if (!existsById(groupId)) {
            throw new IllegalArgumentException("No existe el grupo con id: " + groupId);
        }
        try {
            groupRepository.addClientToGroup(clientId, groupId);
        } catch (Exception e) {
            throw new RuntimeException("Error al agregar el cliente " + clientId + " al grupo " + groupId + ": " + e.getMessage());
        }
    }

    @Override
    public List<Long> getClientIdsByGroup(Long groupId) {
        validateId(groupId);
        if (!existsById(groupId)) {
            throw new IllegalArgumentException("No existe el grupo con id: " + groupId);
        }
        return groupRepository.getClientIdsByGroup(groupId);
    }

    @Override
    public boolean removeClientFromGroup(Long clientId, Long groupId) {
        validateId(clientId);
        validateId(groupId);
        if (!existsById(groupId)) {
            throw new IllegalArgumentException("No existe el grupo con id: " + groupId);
        }
        return groupRepository.removeClientFromGroup(clientId, groupId);
    }
}
