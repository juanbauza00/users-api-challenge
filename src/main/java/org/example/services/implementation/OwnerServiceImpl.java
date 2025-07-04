package org.example.services.implementation;

import org.example.models.Owner;
import org.example.repositories.OwnerRepository;
import org.example.services.interfaces.OwnerService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;

    public OwnerServiceImpl(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Override
    public Owner createOwner(Owner owner) {
        return null;
    }

    @Override
    public Owner getOwnerById(Long id) {
        return null;
    }

    @Override
    public List<Owner> getAllOwners() {
        return Collections.emptyList();
    }

    @Override
    public Owner updateOwner(Long id, Owner ownerData) {
        return null;
    }

    @Override
    public boolean deleteOwner(Long id) {
        return false;
    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }
}
