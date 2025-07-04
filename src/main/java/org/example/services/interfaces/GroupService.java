package org.example.services.interfaces;

import org.example.models.Group;

import java.util.List;

public interface GroupService {
    // CREATE
    Group createGroup(Group group);

    // READ ONE
    Group getGroupById(Long id);

    // READ ALL BY OWNER
    List<Group> getGroupsByOwnerId(Long ownerId);

    //UPDATE
    Group updateGroup(Long id, Group group);

    // DELETE (on cascade - elimina las relaciones de la tabla clients_groups)
    boolean deleteGroup(Long id);

    // VALIDATIONS
    boolean existsById(Long id);
    boolean groupBelongsToOwner(Long groupId, Long ownerId);

    // CLIENT-GROUP OPERATIONS
    void addClientToGroup(Integer particularId, Long groupId);
    List<Integer> getClientParticularIdsByGroup(Long groupId);
    boolean removeClientFromGroup(Integer clientParticularId, Long groupId);
}
