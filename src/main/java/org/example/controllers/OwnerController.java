package org.example.controllers;

import org.example.models.Owner;
import org.example.services.interfaces.OwnerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/owners")
public class OwnerController {

    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<?> createOwner(@RequestBody Owner owner) {
        try{
            Owner createdOwner = ownerService.createOwner(owner);
            return new ResponseEntity<>(createdOwner, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Error de validación: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno: "+ e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<?> getOwnerById(@PathVariable("id") Long id) {
        try {
            Owner owner = ownerService.getOwnerById(id);
            return new ResponseEntity<>(owner, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("No se encontró owner con id: " + id, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error interno: "+ e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // READ ALL
    @GetMapping
    public ResponseEntity<?> getAllOwners() {
        try {
            return new ResponseEntity<>(ownerService.getAllOwners(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>("Error interno: "+ e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOwner(@PathVariable("id") Long id, @RequestBody Owner owner) {
        try {
            Owner updatedOwner = ownerService.updateOwner(id, owner);
            return new ResponseEntity<>(updatedOwner, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error de validación: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Owner no encontrado: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor: " + e.getMessage());
        }
    }


}
