package com.qapac.api.web.dto;

public class TripDtos {
    public static class TripResponse {
        public Integer idViaje;
        public Integer idRuta;
        public String origen;
        public String destino;
        public Integer idBus;
        public String busMatricula;
        public String fechaPartida; // ISO yyyy-MM-dd
        public String horaPartida;  // HH:mm:ss
        public String fechaLlegada; // ISO yyyy-MM-dd
        public String horaLlegada;  // HH:mm:ss
        public TripResponse(Integer idViaje, Integer idRuta, String origen, String destino, Integer idBus, String busMatricula,
                            String fechaPartida, String horaPartida, String fechaLlegada, String horaLlegada) {
            this.idViaje = idViaje; this.idRuta = idRuta; this.origen = origen; this.destino = destino; this.idBus = idBus; this.busMatricula = busMatricula;
            this.fechaPartida = fechaPartida; this.horaPartida = horaPartida; this.fechaLlegada = fechaLlegada; this.horaLlegada = horaLlegada;
        }
    }
    public static class CreateTripRequest {
        public Integer idRuta;
        public Integer idBus;
        public String fechaPartida; // yyyy-MM-dd
        public String horaPartida;  // HH:mm
        public String fechaLlegada; // yyyy-MM-dd
        public String horaLlegada;  // HH:mm
    }
    public static class UpdateTripRequest extends CreateTripRequest {}
}
