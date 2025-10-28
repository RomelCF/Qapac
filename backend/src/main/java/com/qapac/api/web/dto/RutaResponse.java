package com.qapac.api.web.dto;

public class RutaResponse {
    public Integer idRuta;
    public String origen;
    public String destino;
    public java.math.BigDecimal precio;
    public RutaResponse(Integer idRuta, String origen, String destino, java.math.BigDecimal precio){
        this.idRuta = idRuta; this.origen = origen; this.destino = destino; this.precio = precio;
    }
}
