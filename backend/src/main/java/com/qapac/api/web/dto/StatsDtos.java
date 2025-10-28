package com.qapac.api.web.dto;

import java.math.BigDecimal;
import java.util.List;

public class StatsDtos {
    public static class KpisResponse {
        public BigDecimal ingresos;
        public long tickets;
        public int ocupacionPromedio; // porcentaje 0-100
        public int busesActivos;
        public KpisResponse(BigDecimal ingresos, long tickets, int ocupacionPromedio, int busesActivos) {
            this.ingresos = ingresos; this.tickets = tickets; this.ocupacionPromedio = ocupacionPromedio; this.busesActivos = busesActivos;
        }
    }
    public static class SalesPoint { public String date; public BigDecimal total; public SalesPoint(String date, BigDecimal total){this.date=date;this.total=total;} }
    public static class BusStateItem { public String label; public long value; public BusStateItem(String label, long value){this.label=label;this.value=value;} }
    public static class OccupancyItem { public String route; public int percent; public OccupancyItem(String route, int percent){this.route=route;this.percent=percent;} }
    public static class SalesDailyResponse { public List<SalesPoint> points; public SalesDailyResponse(List<SalesPoint> points){this.points=points;} }
    public static class BusStatesResponse { public List<BusStateItem> states; public BusStatesResponse(List<BusStateItem> states){this.states=states;} }
    public static class OccupancyResponse { public List<OccupancyItem> items; public OccupancyResponse(List<OccupancyItem> items){this.items=items;} }
}
