package com.qapac.api.domain;

import com.qapac.api.domain.enums.ActivoInactivo;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "MetodoPago")
public class MetodoPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_metodo_pago")
    private Integer idMetodoPago;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_tipo_pago", nullable = false)
    private TipoPago tipoPago;

    @Lob
    @Column(name = "descripcion")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private ActivoInactivo estado;

    @Column(name = "comision", precision = 5, scale = 2)
    private BigDecimal comision;
}
