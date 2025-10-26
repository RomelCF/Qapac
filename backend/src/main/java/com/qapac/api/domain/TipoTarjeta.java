package com.qapac.api.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "TipoTarjeta")
public class TipoTarjeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_tarjeta")
    private Integer idTipoTarjeta;

    @Column(name = "nombre", length = 50, nullable = false, unique = true)
    private String nombre;
}
