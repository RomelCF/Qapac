package com.qapac.api.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TarjetaItem {
    private Integer idTarjeta;
    private String numeroMasked;
    private String fechaCaducidad;
    private String marca;
    private String metodoPago;
}
