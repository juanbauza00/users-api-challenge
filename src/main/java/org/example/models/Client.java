package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    private Long id;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private LocalDateTime fechaCreacion;
    private Long ownerId;
}
