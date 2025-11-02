package com.qapac.api.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "TiempoReembolso")
public class TiempoReembolso {
    @Id
    @Column(name = "id_tiempo_reembolso")
    private Integer idTiempoReembolso; // siempre 1

    @Column(name = "horas", nullable = false)
    private Integer horas;
}
