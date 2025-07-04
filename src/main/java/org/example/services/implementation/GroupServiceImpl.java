package org.example.services.implementation;

import org.example.models.Group;
import org.example.repositories.GroupRepository;
import org.example.services.interfaces.GroupService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    public GroupServiceImpl(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public Group createGroup(Group group) {
        return null;
    }

    @Override
    public Group getGroupById(Long id) {
        return null;
    }

    @Override
    public List<Group> getGroupsByOwnerId(Long ownerId) {
        return Collections.emptyList();
    }

    @Override
    public Group updateGroup(Long id, Group group) {
        return null;
    }

    @Override
    public boolean deleteGroup(Long id) {
        return false;
    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public boolean groupBelongsToOwner(Long groupId, Long ownerId) {
        return false;
    }

    @Override
    public void addClientToGroup(Long clientId, Long groupId) {

    }

    @Override
    public List<Long> getClientIdsByGroup(Long groupId) {
        return Collections.emptyList();
    }

    @Override
    public boolean removeClientFromGroup(Long clientId, Long groupId) {
        return false;
    }
}
