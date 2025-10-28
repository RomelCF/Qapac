package com.qapac.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Viaje")
public class AsignacionRuta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_viaje")
    private Integer idAsignacionRuta;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_ruta", nullable = false)
    private Ruta ruta;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_bus", nullable = false)
    private Bus bus;

    @Column(name = "fecha_partida", nullable = false)
    private LocalDate fechaPartida;

    @Column(name = "fecha_llegada", nullable = false)
    private LocalDate fechaLlegada;

    @Column(name = "hora_partida", nullable = false)
    private LocalTime horaPartida;

    @Column(name = "hora_llegada", nullable = false)
    private LocalTime horaLlegada;
}
