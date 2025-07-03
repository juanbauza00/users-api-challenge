package org.example.repositories;

import com.sun.istack.internal.NotNull;
import org.example.models.Client;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class ClientRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ClientRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbcTemplate = jdbc;
    }

    private final RowMapper<Client> clientRowMapper = (rs, rowNum) ->
            new Client(
                    rs.getLong("id"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getDate("fecha_nacimiento").toLocalDate(),
                    rs.getTimestamp("fecha_creacion").toLocalDateTime(),
                    rs.getLong("owner_id")
            );

    // CREATE
    public Client save(Client client) {
        String sql = "INSERT INTO clients (nombre, apellido, fecha_nacimiento, owner_id)" +
                     "VALUES (:nombre, :apellido, :fecha_nacimiento, :owner_id) RETURNING id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nombre", client.getNombre());
        params.addValue("apellido", client.getApellido());
        params.addValue("fecha_nacimiento", client.getFechaNacimiento());
        params.addValue("owner_id", client.getOwnerId());

        Long generatedId = jdbcTemplate.queryForObject(sql, params, Long.class);
        client.setId(generatedId);
        return client;
    }

    // READ ONE
    public Optional<Client> findById(Long id) {
        String sql = "SELECT * FROM clients WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        try{
            Client client = jdbcTemplate.queryForObject(sql, params, clientRowMapper);
            return Optional.of(client);
        } catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    // READ ALL BY OWNER
    public List<Client> findAllByOwnerId(Long ownerId) {
        String sql = "SELECT * FROM clients WHERE owner_id = :ownerId";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ownerId", ownerId);

        return jdbcTemplate.query(sql, Collections.emptyMap(), clientRowMapper);
    }


    // UPDATE
    public Client update(Client client) {
        String sql = "UPDATE clients SET nombre = :nombre, apellido = :apellido, fecha_nacimiento = :fecha_nacimiento " +
                                  "WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nombre", client.getNombre());
        params.addValue("apellido", client.getApellido());
        params.addValue("fecha_nacimiento", client.getFechaNacimiento());
        params.addValue("id", client.getId());

        int rowsUpdated = jdbcTemplate.update(sql, params);
        if (rowsUpdated == 0) {
            throw new RuntimeException("El cliente con id: " + client.getId() + " no fue encontrado");
        }
        return client;
    }

    // DELETE
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM clients WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        int rowsUpdated = jdbcTemplate.update(sql, params);
        if (rowsUpdated == 0) {
            throw new RuntimeException("El cliente con id: " + id + " no fue encontrado");
        }
        return true;
    }

    // EXIST
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM clients WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        Integer count = jdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }
}
