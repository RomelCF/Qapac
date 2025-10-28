package com.qapac.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Sucursal")
public class Sucursal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sucursal")
    private Integer idSucursal;

    @Column(name = "nombre", length = 150, nullable = false)
    private String nombre;

    @Column(name = "departamento", length = 100, nullable = false)
    private String departamento;

    @Column(name = "provincia", length = 100, nullable = false)
    private String provincia;

    @Column(name = "direccion", length = 255, nullable = false)
    private String direccion;
}
