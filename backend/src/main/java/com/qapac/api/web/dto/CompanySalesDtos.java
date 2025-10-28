package com.qapac.api.web.dto;

import java.math.BigDecimal;
import java.util.List;

public class CompanySalesDtos {
    public static class SalesItem {
        public Integer idVenta;
        public Integer idDetalleVenta;
        public String fecha; // YYYY-MM-DD
        public String hora;  // HH:mm:ss
        public String ruta;  // Origen - Destino
        public String busMatricula;
        public String asiento;
        public BigDecimal precio;
        public String cliente;
        public String metodoPago;
        public SalesItem(Integer idVenta, Integer idDetalleVenta, String fecha, String hora, String ruta,
                         String busMatricula, String asiento, BigDecimal precio, String cliente, String metodoPago) {
            this.idVenta = idVenta; this.idDetalleVenta = idDetalleVenta; this.fecha = fecha; this.hora = hora; this.ruta = ruta;
            this.busMatricula = busMatricula; this.asiento = asiento; this.precio = precio; this.cliente = cliente; this.metodoPago = metodoPago;
        }
    }
    public static class SalesListResponse { public List<SalesItem> items; public SalesListResponse(List<SalesItem> items){this.items=items;} }

    public static class SaleDetailItem {
        public String asiento;
        public BigDecimal precio;
        public SaleDetailItem(String asiento, BigDecimal precio){ this.asiento = asiento; this.precio = precio; }
    }
    public static class SaleDetailResponse {
        public Integer idVenta;
        public String fecha;
        public String hora;
        public String ruta;
        public String busMatricula;
        public String metodoPago;
        public String cliente;
        public BigDecimal total;
        public List<SaleDetailItem> items;
        public SaleDetailResponse(Integer idVenta, String fecha, String hora, String ruta, String busMatricula, String metodoPago, String cliente, BigDecimal total, List<SaleDetailItem> items) {
            this.idVenta = idVenta; this.fecha = fecha; this.hora = hora; this.ruta = ruta; this.busMatricula = busMatricula; this.metodoPago = metodoPago; this.cliente = cliente; this.total = total; this.items = items;
        }
    }
}
