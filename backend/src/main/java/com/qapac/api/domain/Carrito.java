package com.qapac.api.domain;

import com.qapac.api.domain.enums.CarritoEstado;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Carrito")
public class Carrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_carrito")
    private Integer idCarrito;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_asignacion_ruta", nullable = false)
    private AsignacionRuta asignacionRuta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_asiento", nullable = false)
    private Asiento asiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private CarritoEstado estado;
}
