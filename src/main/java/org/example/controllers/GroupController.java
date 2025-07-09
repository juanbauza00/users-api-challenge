package org.example.controllers;

import org.example.models.Group;
import org.example.services.interfaces.GroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    // CREATE GROUP
    @PostMapping("/owner/{ownerId}")
    public ResponseEntity<?> createGroup(@PathVariable("ownerId") Long ownerId,
                                         @RequestBody Group group) {
        try {
            group.setOwnerId(ownerId);
            Group createdGroup = groupService.createGroup(group);
            return new ResponseEntity<>(createdGroup, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error de validación: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Owner no encontrado: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // READ ONE GROUP
    @GetMapping("/{groupId}")
    public ResponseEntity<?> getGroupById(@PathVariable("groupId") Long groupId) {
        try {
            Group group = groupService.getGroupById(groupId);
            return new ResponseEntity<>(group, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error de validación: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Grupo no encontrado: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // READ ALL GROUPS BY OWNER
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<?> getGroupsByOwner(@PathVariable("ownerId") Long ownerId) {
        try {
            List<Group> groups = groupService.getGroupsByOwnerId(ownerId);
            return new ResponseEntity<>(groups, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error de validación: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Owner no encontrado: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // UPDATE GROUP
    @PutMapping("/owner/{ownerId}/group/{groupId}")
    public ResponseEntity<?> updateGroup(@PathVariable("ownerId") Long ownerId,
                                         @PathVariable("groupId") Long groupId,
                                         @RequestBody Group groupData) {
        try {
            groupData.setOwnerId(ownerId);
            Group updatedGroup = groupService.updateGroup(groupId, groupData);
            return new ResponseEntity<>(updatedGroup, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error de validación: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Grupo no encontrado: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE GROUP
    @DeleteMapping("/owner/{ownerId}/group/{groupId}")
    public ResponseEntity<?> deleteGroup(@PathVariable("ownerId") Long ownerId,
                                         @PathVariable("groupId") Long groupId) {
        try {
            // Verificar que el grupo pertenece al owner antes de eliminar
            if (!groupService.groupBelongsToOwner(groupId, ownerId)) {
                return new ResponseEntity<>("El grupo no pertenece a este owner", HttpStatus.FORBIDDEN);
            }
            groupService.deleteGroup(groupId);
            return ResponseEntity.ok("Grupo eliminado exitosamente");
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error de validación: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Grupo no encontrado: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ADD CLIENT TO GROUP
    @PostMapping("/owner/{ownerId}/group/{groupId}/client/{particularId}")
    public ResponseEntity<?> addClientToGroup(@PathVariable("ownerId") Long ownerId,
                                              @PathVariable("groupId") Long groupId,
                                              @PathVariable("particularId") Integer particularId) {
        try {
            // Verificar que el grupo pertenece al owner
            if (!groupService.groupBelongsToOwner(groupId, ownerId)) {
                return new ResponseEntity<>("El grupo no pertenece a este owner", HttpStatus.FORBIDDEN);
            }
            groupService.addClientToGroup(particularId, groupId);
            return ResponseEntity.ok("Cliente agregado al grupo exitosamente");
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error de validación: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Recurso no encontrado: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // REMOVE CLIENT FROM GROUP
    @DeleteMapping("/owner/{ownerId}/group/{groupId}/client/{particularId}")
    public ResponseEntity<?> removeClientFromGroup(@PathVariable("ownerId") Long ownerId,
                                                   @PathVariable("groupId") Long groupId,
                                                   @PathVariable("particularId") Integer particularId) {
        try {
            // Verificar que el grupo pertenece al owner
            if (!groupService.groupBelongsToOwner(groupId, ownerId)) {
                return new ResponseEntity<>("El grupo no pertenece a este owner", HttpStatus.FORBIDDEN);
            }
            groupService.removeClientFromGroup(particularId, groupId);
            return ResponseEntity.ok("Cliente removido del grupo exitosamente");
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error de validación: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Recurso no encontrado: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET CLIENTS IN GROUP
    @GetMapping("/owner/{ownerId}/group/{groupId}/clients")
    public ResponseEntity<?> getClientsInGroup(@PathVariable("ownerId") Long ownerId,
                                               @PathVariable("groupId") Long groupId) {
        try {
            // Verificar que el grupo pertenece al owner
            if (!groupService.groupBelongsToOwner(groupId, ownerId)) {
                return new ResponseEntity<>("El grupo no pertenece a este owner", HttpStatus.FORBIDDEN);
            }
            List<Integer> clientParticularIds = groupService.getClientParticularIdsByGroup(groupId);
            return new ResponseEntity<>(clientParticularIds, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error de validación: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Grupo no encontrado: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
