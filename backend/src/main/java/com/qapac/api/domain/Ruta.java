package com.qapac.api.domain;

import com.qapac.api.domain.enums.RutaEstado;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Ruta")
public class Ruta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ruta")
    private Integer idRuta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_sucursal_origen", nullable = false)
    private Sucursal sucursalOrigen;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_sucursal_destino", nullable = false)
    private Sucursal sucursalDestino;

    @Column(name = "precio", precision = 10, scale = 2, nullable = false)
    private BigDecimal precio;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private RutaEstado estado;
}
