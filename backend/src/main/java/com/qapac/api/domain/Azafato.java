package com.qapac.api.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Azafato")
public class Azafato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_azafato")
    private Integer idAzafato;

    @OneToOne(optional = false)
    @JoinColumn(name = "id_empleado", nullable = false, unique = true)
    private Empleado empleado;
}
