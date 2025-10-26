package com.qapac.api.domain;

import com.qapac.api.domain.enums.Disponibilidad;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Asiento", uniqueConstraints = {
        @UniqueConstraint(name = "unique_asiento_bus", columnNames = {"codigo", "id_bus"})
})
public class Asiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asiento")
    private Integer idAsiento;

    @Column(name = "codigo", length = 10, nullable = false)
    private String codigo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_bus", nullable = false)
    private Bus bus;

    @Enumerated(EnumType.STRING)
    @Column(name = "disponibilidad")
    private Disponibilidad disponibilidad;
}
