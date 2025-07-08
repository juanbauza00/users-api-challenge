package org.example.services.implementation;

import org.example.models.Owner;
import org.example.repositories.OwnerRepository;
import org.example.services.interfaces.OwnerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;

    public OwnerServiceImpl(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    private void validateOwner(Owner owner) {
        if (owner == null) {
            throw new IllegalArgumentException("Los datos del owner no pueden ser nulos");
        }
        if (owner.getNombre() == null || owner.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del owner es obligatorio");
        }
        if (owner.getNombre().trim().length() > 255) {
            throw new IllegalArgumentException("El nombre del owner no puede exceder 255 caracteres");
        }
    }

    private void validateOwnerId(Long id){
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id debe ser un numero positivo");
        }
    }

    @Override
    @Transactional
    public Owner createOwner(Owner owner) {
        validateOwner(owner);
        return ownerRepository.save(owner);
    }

    @Override
    public Owner getOwnerById(Long id) {
        validateOwnerId(id);
        return ownerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe el owner con id: " + id));
    }

    @Override
    public List<Owner> getAllOwners() {
        return ownerRepository.findAll();
    }

    @Override
    @Transactional
    public Owner updateOwner(Long id, Owner ownerData) {
        validateOwnerId(id);
        validateOwner(ownerData);
        ownerData.setId(id);
        return ownerRepository.update(ownerData);
    }

    @Override
    @Transactional
    public boolean deleteOwner(Long id) {
        validateOwnerId(id);
        return ownerRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        validateOwnerId(id);
        return ownerRepository.existsById(id);
    }
}
