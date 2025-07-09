package org.example.services.implementation;

import org.example.dtos.OwnerInputDto;
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

    private void validateOwnerId(Long id){
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id debe ser un numero positivo");
        }
    }

    private void validateOwnerInputDto(OwnerInputDto ownerDto){
        if (ownerDto == null || ownerDto.getName() == null || ownerDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
    }

    private Owner mapOwnerInputDtoToOwner(OwnerInputDto ownerDto){
        Owner owner = new Owner();
        owner.setNombre(ownerDto.getName());
        return owner;
    }

    @Override
    @Transactional
    public Owner createOwner(OwnerInputDto ownerDto) {
        validateOwnerInputDto(ownerDto);
        return ownerRepository.save(mapOwnerInputDtoToOwner(ownerDto));
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
    public Owner updateOwner(Long id, OwnerInputDto ownerDto) {
        validateOwnerId(id);
        validateOwnerInputDto(ownerDto);
        return ownerRepository.update(mapOwnerInputDtoToOwner(ownerDto));
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
