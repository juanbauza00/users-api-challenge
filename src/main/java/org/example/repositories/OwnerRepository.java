package org.example.repositories;

import org.example.models.Owner;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class OwnerRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public OwnerRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbcTemplate = jdbc;
    }

    private final RowMapper<Owner> ownerRowMapper = (rs, rowNum) ->
            new Owner(
                    rs.getLong("id"),
                    rs.getString("nombre")
            );

    // CREATE
    public Owner save(Owner owner) {
        String sql = "INSERT INTO owners (nombre) VALUES (:nombre) RETURNING id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nombre", owner.getNombre());

        Long generatedId = jdbcTemplate.queryForObject(sql, params, Long.class);
        owner.setId(generatedId);
        return owner;
    }

    // READ ONE
    public Optional<Owner> findById(Long id) {
        String sql = "SELECT id, nombre FROM owners WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        try {
            Owner owner = jdbcTemplate.queryForObject(sql, params, ownerRowMapper);
            return Optional.of(owner);
        } catch (EmptyResultDataAccessException e) { //EmptyResultDataAccessException captura únicamente los errores "not found", evitando que otros errores se muestren como no encontrado
            return Optional.empty();
        }
    }

    // READ ALL
    public List<Owner> findAll() {
        String sql = "SELECT id, nombre FROM owners ORDER BY id ASC";
        return jdbcTemplate.query(sql, Collections.emptyMap(), ownerRowMapper);
    }

    // UPDATE
    public Owner update(Owner owner) {
        String sql = "UPDATE owners SET nombre = :nombre WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", owner.getId());
        params.addValue("nombre", owner.getNombre());

        int rowsUpdated = jdbcTemplate.update(sql, params);
        if (rowsUpdated == 0) {
            throw new RuntimeException("El owner con id: " + owner.getId() + " no fue encontrado");
        }
        return owner;
    }

    //DELETE
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM owners WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        int rowsUpdated = jdbcTemplate.update(sql, params);
        if (rowsUpdated == 0) {
            throw new RuntimeException("El owner con id: " + id + " no fue encontrado");
        }
        return true;
    }

    // EXIST
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM owners WHERE id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        Integer count = jdbcTemplate.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }
}
