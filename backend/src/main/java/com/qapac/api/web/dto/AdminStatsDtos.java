package com.qapac.api.web.dto;

import java.math.BigDecimal;
import java.util.List;

public class AdminStatsDtos {
    public static class KpisResponse {
        public BigDecimal ingresos;
        public long tickets;
        public int empresasActivas;
        public int busesActivos;
        public KpisResponse(BigDecimal ingresos, long tickets, int empresasActivas, int busesActivos) {
            this.ingresos = ingresos; this.tickets = tickets; this.empresasActivas = empresasActivas; this.busesActivos = busesActivos;
        }
    }
    public static class SalesPoint { public String date; public BigDecimal total; public SalesPoint(String date, BigDecimal total){this.date=date;this.total=total;} }
    public static class CompanySalesItem { public String empresa; public BigDecimal total; public CompanySalesItem(String empresa, BigDecimal total){this.empresa=empresa;this.total=total;} }
    public static class TopRouteItem { public String ruta; public long vendidos; public TopRouteItem(String ruta, long vendidos){this.ruta=ruta;this.vendidos=vendidos;} }
    public static class SalesDailyResponse { public List<SalesPoint> points; public SalesDailyResponse(List<SalesPoint> points){this.points=points;} }
    public static class SalesByCompanyResponse { public List<CompanySalesItem> items; public SalesByCompanyResponse(List<CompanySalesItem> items){this.items=items;} }
    public static class TopRoutesResponse { public List<TopRouteItem> items; public TopRoutesResponse(List<TopRouteItem> items){this.items=items;} }
}
