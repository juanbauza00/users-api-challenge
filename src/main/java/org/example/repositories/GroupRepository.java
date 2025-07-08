package org.example.repositories;

import org.example.models.Group;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GroupRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public GroupRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Group> groupRowMapper = (rs, rowNum) ->
            new Group(
                    rs.getLong("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getLong("owner_id")
            );

    // CREATE
    public Group save(Group group) {
        String sql = "INSERT INTO groups (nombre, descripcion, owner_id) " +
                     "VALUES (:nombre, :descripcion, :owner_id) RETURNING id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nombre", group.getNombre());
        params.addValue("descripcion", group.getDescripcion());
        params.addValue("owner_id", group.getOwnerId());

        Long generatedId = jdbcTemplate.queryForObject(sql, params, Long.class);
        group.setId(generatedId);
        return group;
    }

    // READ ONE
    public Optional<Group> findById(Long id) {
        String sql = "SELECT id, nombre, descripcion, owner_id FROM groups WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        try {
            Group group = jdbcTemplate.queryForObject(sql, params, groupRowMapper);
            return Optional.of(group);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // READ ALL BY OWNER
    public List<Group> findAllByOwnerId(Long ownerId) {
        String sql = "SELECT id, nombre, descripcion, owner_id FROM groups WHERE owner_id = :ownerId ORDER BY id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ownerId", ownerId);

        return jdbcTemplate.query(sql, params, groupRowMapper);
    }

    // UPDATE
    public Group update(Group group) {
        String sql = "UPDATE groups SET nombre = :nombre, descripcion = :descripcion " +
                "WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nombre", group.getNombre());
        params.addValue("descripcion", group.getDescripcion());
        params.addValue("id", group.getId());

        int rowsAffected = jdbcTemplate.update(sql, params);

        if (rowsAffected == 0) {
            throw new RuntimeException("El grupo con id: " + group.getId() + " no fue encontrado");
        }

        return group;
    }

    // DELETE  ( clients_groups tiene ON CASCADE en group_id)
    public boolean deleteGroupById(Long groupId) {
        String sql = "DELETE FROM groups WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", groupId);

        int rowsAffected = jdbcTemplate.update(sql, params);
        if (rowsAffected == 0) {
            throw new RuntimeException("El grupo con id: " + groupId + " no fue encontrado");
        }
        return true;
    }

    //----------------  OPERACIONES CON client_groups ----------------------------------

    // Asignar cliente a grupo
    public void addClientToGroup(Long clientId, Long groupId) {
        if (isClientInGroup(clientId, groupId)) {
            throw new IllegalStateException("El cliente ya pertenece a este grupo");
        }
        String sql = "INSERT INTO clients_groups (client_id, group_id) VALUES (:client_id, :group_id)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("client_id", clientId);
        params.addValue("group_id", groupId);

        jdbcTemplate.update(sql, params);
    }

    // Obtener todos los clientes de un grupo
    public List<Long> getClientIdsByGroup(Long groupId) {
        String sql = "SELECT client_id FROM clients_groups WHERE group_id = :group_id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("group_id", groupId);

        return jdbcTemplate.queryForList(sql, params, Long.class);
    }

    // Quitar cliente de grupo
    public boolean removeClientFromGroup(Long clientId, Long groupId) {
        if (!isClientInGroup(clientId, groupId)) {
            throw new IllegalStateException("El cliente no pertenece a este grupo");
        }

        String sql = "DELETE FROM clients_groups WHERE client_id = :client_id AND group_id = :group_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("client_id", clientId);
        params.addValue("group_id", groupId);

        int rowsAffected = jdbcTemplate.update(sql, params);
        return rowsAffected > 0;
    }

    // Valida que el cliente esté en el grupo
    public boolean isClientInGroup(Long clientId, Long groupId) {
        String sql = "SELECT COUNT(*) FROM clients_groups WHERE client_id = :client_id AND group_id = :group_id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("client_id", clientId);
        params.addValue("group_id", groupId);

        Integer count = jdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }
}
