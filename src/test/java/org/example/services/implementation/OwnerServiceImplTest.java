package org.example.services.implementation;

import org.example.dtos.OwnerInputDto;
import org.example.models.Owner;
import org.example.repositories.OwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerServiceImplTest {

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerServiceImpl ownerService;

    private Owner owner;
    private OwnerInputDto ownerInputDto;

    @BeforeEach
    void setUp() {
        owner = new Owner(1L, "Juan Pérez");
        ownerInputDto = new OwnerInputDto("Juan Pérez");
    }

    // ---------------------- CREATE OWNER -----------------------------

    @Test
    void createOwner_WithValidData_ShouldReturnOwner() {
        when(ownerRepository.save(any(Owner.class))).thenReturn(owner);
        Owner result = ownerService.createOwner(ownerInputDto);

        assertNotNull(result);
        assertEquals(owner.getId(), result.getId());
        assertEquals(owner.getNombre(), result.getNombre());
        verify(ownerRepository, times(1)).save(any(Owner.class));
    }

    @Test
    void createOwner_WithNullDto_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ownerService.createOwner(null)
        );

        assertEquals("El nombre no puede estar vacío", exception.getMessage());
        verify(ownerRepository, never()).save(any(Owner.class));
    }

    @Test
    void createOwner_WithEmptyName_ShouldThrowException() {
        OwnerInputDto emptyDto = new OwnerInputDto("");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ownerService.createOwner(emptyDto)
        );

        assertEquals("El nombre no puede estar vacío", exception.getMessage());
        verify(ownerRepository, never()).save(any(Owner.class));
    }

    @Test
    void createOwner_WithBlankName_ShouldThrowException() {
        OwnerInputDto blankDto = new OwnerInputDto("   ");
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ownerService.createOwner(blankDto)
        );
        assertEquals("El nombre no puede estar vacío", exception.getMessage());
        verify(ownerRepository, never()).save(any(Owner.class));
    }

    // ------------------- GET OWNER BY ID -------------------------

    @Test
    void getOwnerById_WithValidId_ShouldReturnOwner() {
        when(ownerRepository.findById(1L)).thenReturn(Optional.of(owner));
        Owner result = ownerService.getOwnerById(1L);

        assertNotNull(result);
        assertEquals(owner.getId(), result.getId());
        assertEquals(owner.getNombre(), result.getNombre());
        verify(ownerRepository, times(1)).findById(1L);
    }

    @Test
    void getOwnerById_WithNonExistentId_ShouldThrowException() {
        when(ownerRepository.findById(999L)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> ownerService.getOwnerById(999L)
        );

        assertEquals("No existe el owner con id: 999", exception.getMessage());
        verify(ownerRepository, times(1)).findById(999L);
    }

    @Test
    void getOwnerById_WithNullId_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ownerService.getOwnerById(null)
        );

        assertEquals("El id debe ser un numero positivo", exception.getMessage());
        verify(ownerRepository, never()).findById(anyLong());
    }

    @Test
    void getOwnerById_WithNegativeId_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ownerService.getOwnerById(-1L)
        );

        assertEquals("El id debe ser un numero positivo", exception.getMessage());
        verify(ownerRepository, never()).findById(anyLong());
    }

    @Test
    void getOwnerById_WithZeroId_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ownerService.getOwnerById(0L)
        );

        assertEquals("El id debe ser un numero positivo", exception.getMessage());
        verify(ownerRepository, never()).findById(anyLong());
    }

    //---------------------- GET ALL OWNERS ---------------------------------

    @Test
    void getAllOwners_ShouldReturnListOfOwners() {
        List<Owner> owners = Arrays.asList(
                new Owner(1L, "Owner 1"),
                new Owner(2L, "Owner 2")
        );

        when(ownerRepository.findAll()).thenReturn(owners);
        List<Owner> result = ownerService.getAllOwners();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Owner 1", result.get(0).getNombre());
        assertEquals("Owner 2", result.get(1).getNombre());
        verify(ownerRepository, times(1)).findAll();
    }

    @Test
    void getAllOwners_WithEmptyList_ShouldReturnEmptyList() {
        when(ownerRepository.findAll()).thenReturn(Arrays.asList());
        List<Owner> result = ownerService.getAllOwners();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(ownerRepository, times(1)).findAll();
    }

    // ---------------------- UPDATE OWNER -----------------------

    @Test
    void updateOwner_WithValidData_ShouldReturnUpdatedOwner() {
        Owner updatedOwner = new Owner(1L, "Juan Pérez Actualizado");
        when(ownerRepository.update(any(Owner.class))).thenReturn(updatedOwner);

        Owner result = ownerService.updateOwner(1L, ownerInputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Juan Pérez Actualizado", result.getNombre());
        verify(ownerRepository, times(1)).update(any(Owner.class));
    }

    @Test
    void updateOwner_WithNullId_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ownerService.updateOwner(null, ownerInputDto)
        );

        assertEquals("El id debe ser un numero positivo", exception.getMessage());
        verify(ownerRepository, never()).update(any(Owner.class));
    }

    @Test
    void updateOwner_WithNullDto_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ownerService.updateOwner(1L, null)
        );

        assertEquals("El nombre no puede estar vacío", exception.getMessage());
        verify(ownerRepository, never()).update(any(Owner.class));
    }

    // ------------------- DELETE OWNER ----------------

    @Test
    void deleteOwner_WithValidId_ShouldReturnTrue() {
        when(ownerRepository.deleteById(1L)).thenReturn(true);
        boolean result = ownerService.deleteOwner(1L);

        assertTrue(result);
        verify(ownerRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteOwner_WithNullId_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ownerService.deleteOwner(null)
        );

        assertEquals("El id debe ser un numero positivo", exception.getMessage());
        verify(ownerRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteOwner_WithNegativeId_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ownerService.deleteOwner(-5L)
        );

        assertEquals("El id debe ser un numero positivo", exception.getMessage());
        verify(ownerRepository, never()).deleteById(anyLong());
    }

    // ----------------- EXISTS BY ID -------------------------

    @Test
    void existsById_WithExistingId_ShouldReturnTrue() {
        when(ownerRepository.existsById(1L)).thenReturn(true);
        boolean result = ownerService.existsById(1L);

        assertTrue(result);
        verify(ownerRepository, times(1)).existsById(1L);
    }

    @Test
    void existsById_WithNonExistingId_ShouldReturnFalse() {
        when(ownerRepository.existsById(999L)).thenReturn(false);
        boolean result = ownerService.existsById(999L);

        assertFalse(result);
        verify(ownerRepository, times(1)).existsById(999L);
    }

    @Test
    void existsById_WithNullId_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ownerService.existsById(null)
        );

        assertEquals("El id debe ser un numero positivo", exception.getMessage());
        verify(ownerRepository, never()).existsById(anyLong());
    }
}