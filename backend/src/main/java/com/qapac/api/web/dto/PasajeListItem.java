package com.qapac.api.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasajeListItem {
    private Integer idPasaje;
    private String origenProvincia;
    private String destinoProvincia;
    private String fecha;
    private String hora;
    private String empresaNombre;
    private String empresaNumero;
    private String busMatricula;
    private List<String> choferes;
    private List<String> azafatos;
    private String seatCode;
    private SucursalInfo sucursalOrigen;
    private SucursalInfo sucursalDestino;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SucursalInfo {
        private String nombre;
        private String provincia;
        private String direccion;
    }
}
