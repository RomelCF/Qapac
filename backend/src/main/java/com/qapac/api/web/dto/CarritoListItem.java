package com.qapac.api.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoListItem {
    private Integer idCarrito;
    private String origenProvincia;
    private String destinoProvincia;
    private String fecha;
    private String hora;
    private String empresaNombre;
    private String empresaNumero;
    private String busMatricula;
    private String seatCode;
    private BigDecimal precio;
    private List<String> choferes;
    private List<String> azafatos;
    private PasajeListItem.SucursalInfo sucursalOrigen;
    private PasajeListItem.SucursalInfo sucursalDestino;
}
