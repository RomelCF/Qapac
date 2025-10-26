package com.qapac.api.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Chofer")
public class Chofer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_chofer")
    private Integer idChofer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_brevete", nullable = false)
    private Brevete brevete;

    @OneToOne(optional = false)
    @JoinColumn(name = "id_empleado", nullable = false, unique = true)
    private Empleado empleado;
}
