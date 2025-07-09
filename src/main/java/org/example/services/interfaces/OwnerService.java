package org.example.services.interfaces;

import org.example.dtos.OwnerInputDto;
import org.example.models.Owner;

import java.util.List;

public interface OwnerService {
    // CREATE
    Owner createOwner(OwnerInputDto ownerDto);

    // READ ONE
    Owner getOwnerById(Long id);

    // READ ALL
    List<Owner> getAllOwners();

    // UPDATE
    Owner updateOwner(Long id, OwnerInputDto ownerDto);

    // DELETE
    boolean deleteOwner(Long id);

    // EXIST
    boolean existsById(Long id);
}
