package com.qapac.api.domain;

import com.qapac.api.domain.enums.CarritoEstado;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Representa un ítem en el carrito de compras.
 * Se mapea a la tabla 'Pasaje' en la base de datos.
 * El estado 'pendiente' indica que está en el carrito pero no se ha pagado.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Pasaje")
public class Carrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pasaje")
    private Integer idCarrito;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_viaje", nullable = false)
    private AsignacionRuta asignacionRuta;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "id_asiento", nullable = false)
    private Asiento asiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private CarritoEstado estado;
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now(ZoneId.of("America/Lima"));
        if (this.estado == null) {
            this.estado = CarritoEstado.pendiente;
        }
    }
}
