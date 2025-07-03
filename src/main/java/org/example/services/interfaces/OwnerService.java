package org.example.services.interfaces;

import org.example.models.Owner;

import java.util.List;

public interface OwnerService {
    // CREATE
    Owner createOwner(Owner owner);

    // READ ONE
    Owner getOwnerById(Long id);

    // READ ALL
    List<Owner> getAllOwners();

    // UPDATE
    Owner updateOwner(Long id, Owner ownerData);

    // DELETE
    boolean deleteOwner(Long id);

    // EXIST
    boolean existsById(Long id);
}
