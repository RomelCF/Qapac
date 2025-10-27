package com.qapac.api.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodItem {
    private Integer idMetodoPago;
    private String nombre;
    private String tipo;
    private String descripcion;
    private String estado;
    private BigDecimal comision;
}
