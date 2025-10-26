package com.qapac.api.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "AsignacionEmpleado", uniqueConstraints = {
        @UniqueConstraint(name = "unique_empleado_ruta", columnNames = {"id_asignacion_ruta", "id_empleado"})
})
public class AsignacionEmpleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asignacion_bus_azafato")
    private Integer idAsignacionBusAzafato;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_asignacion_ruta", nullable = false)
    private AsignacionRuta asignacionRuta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;
}
