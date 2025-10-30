package com.qapac.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Empleado")
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleado")
    private Integer idEmpleado;

    @Column(name = "dni", columnDefinition = "CHAR(8)", nullable = false, unique = true)
    private String dni;

    @Column(name = "telefono", length = 15)
    private String telefono;

    @Column(name = "domicilio", length = 255)
    private String domicilio;

    @Column(name = "nombres", length = 100, nullable = false)
    private String nombres;

    @Column(name = "apellidos", length = 100, nullable = false)
    private String apellidos;

    @Column(name = "anios_experiencia")
    private Integer aniosExperiencia;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "disponible", columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean disponible;
}
