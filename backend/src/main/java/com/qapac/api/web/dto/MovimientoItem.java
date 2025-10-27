package com.qapac.api.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoItem {
    private String tipo; // COMPRA o CANCELACION
    private String fecha; // yyyy-MM-dd
    private String hora;  // HH:mm:ss
    private String origen;
    private String destino;
    private String fechaSalida;
    private String horaSalida;
    private String empresa;
    private String busMatricula;
    private String metodoPago;
    private String tarjetaMasked;
    private Double bruto;
    private Double comisionPct;
    private Double comisionMonto;
    private Double neto;
    private String estadoCarrito;
    private Double originalNeto; // total pagado original (para cancelaciones)
    private Double originalBruto; // precio base original (para cancelaciones)
}
