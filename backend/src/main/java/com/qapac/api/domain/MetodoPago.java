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
@Table(name = "metodopago")
public class MetodoPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_metodo_pago")
    private Integer idMetodoPago;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tipo_pago", referencedColumnName = "id_tipo_pago", nullable = false)
    private TipoPago tipoPago;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private ActivoInactivo estado;

    @Column(name = "comision", precision = 5, scale = 2, nullable = false)
    private BigDecimal comision;
}
