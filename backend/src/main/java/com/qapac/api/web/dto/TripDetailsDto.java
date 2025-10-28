package com.qapac.api.web.dto;

import java.util.List;

public class TripDetailsDto {
    public List<String> choferes;
    public List<String> azafatos;
    public List<SeatItem> asientos;
    public int totalAsientos;
    public int disponibles;
    public long vendidos;

    public TripDetailsDto(List<String> choferes, List<String> azafatos, List<SeatItem> asientos, int totalAsientos, int disponibles, long vendidos) {
        this.choferes = choferes;
        this.azafatos = azafatos;
        this.asientos = asientos;
        this.totalAsientos = totalAsientos;
        this.disponibles = disponibles;
        this.vendidos = vendidos;
    }

    public static class SeatItem {
        public Integer idAsiento;
        public String codigo;
        public String disponibilidad;
        public SeatItem(Integer idAsiento, String codigo, String disponibilidad) {
            this.idAsiento = idAsiento;
            this.codigo = codigo;
            this.disponibilidad = disponibilidad;
        }
    }
}
