package com.qapac.api.web;

import com.qapac.api.domain.*;
import com.qapac.api.repository.*;
import com.qapac.api.web.dto.AdminStatsDtos.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin-stats")
public class AdminStatsController {

    private final DetalleVentaRepository detalleVentaRepository;
    private final EmpresaRepository empresaRepository;
    private final BusRepository busRepository;

    public AdminStatsController(DetalleVentaRepository detalleVentaRepository,
                                EmpresaRepository empresaRepository,
                                BusRepository busRepository) {
        this.detalleVentaRepository = detalleVentaRepository;
        this.empresaRepository = empresaRepository;
        this.busRepository = busRepository;
    }

    @GetMapping("/kpis")
    public ResponseEntity<KpisResponse> kpis(@RequestParam(required = false) String from,
                                             @RequestParam(required = false) String to) {
        DateRange dr = resolveRange(from, to);
        List<DetalleVenta> detalles = detalleVentaRepository.findByVenta_FechaBetween(dr.from, dr.to);
        BigDecimal ingresos = detalles.stream()
                .map(d -> Optional.ofNullable(d.getPasaje())
                        .map(Carrito::getAsignacionRuta)
                        .map(AsignacionRuta::getRuta)
                        .map(Ruta::getPrecio).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long tickets = detalles.size();
        int empresasActivas = (int)empresaRepository.count();
        int busesActivos;
        try {
            busesActivos = (int)busRepository.count();
        } catch (Exception ignored) {
            busesActivos = 0;
        }
        return ResponseEntity.ok(new KpisResponse(ingresos, tickets, empresasActivas, busesActivos));
    }

    @GetMapping("/sales-daily")
    public ResponseEntity<SalesDailyResponse> salesDaily(@RequestParam(required = false) String from,
                                                         @RequestParam(required = false) String to) {
        DateRange dr = resolveRange(from, to);
        List<DetalleVenta> detalles = detalleVentaRepository.findByVenta_FechaBetween(dr.from, dr.to);
        Map<LocalDate, BigDecimal> byDate = new TreeMap<>();
        for (DetalleVenta d : detalles) {
            Venta v = d.getVenta();
            if (v == null || v.getFecha() == null) continue;
            BigDecimal precio = Optional.ofNullable(d.getPasaje())
                    .map(Carrito::getAsignacionRuta)
                    .map(AsignacionRuta::getRuta)
                    .map(Ruta::getPrecio)
                    .orElse(BigDecimal.ZERO);
            byDate.merge(v.getFecha(), precio, BigDecimal::add);
        }
        List<SalesPoint> points = byDate.entrySet().stream()
                .map(e -> new SalesPoint(e.getKey().toString(), e.getValue()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new SalesDailyResponse(points));
    }

    @GetMapping("/sales-by-company")
    public ResponseEntity<SalesByCompanyResponse> salesByCompany(@RequestParam(required = false) String from,
                                                                 @RequestParam(required = false) String to) {
        DateRange dr = resolveRange(from, to);
        List<DetalleVenta> detalles = detalleVentaRepository.findByVenta_FechaBetween(dr.from, dr.to);
        Map<String, BigDecimal> byCompany = new LinkedHashMap<>();
        for (DetalleVenta d : detalles) {
            String empName = Optional.ofNullable(d.getPasaje())
                    .map(Carrito::getAsignacionRuta)
                    .map(AsignacionRuta::getRuta)
                    .map(Ruta::getEmpresa)
                    .map(Empresa::getNombre)
                    .orElse("Desconocida");
            BigDecimal precio = Optional.ofNullable(d.getPasaje())
                    .map(Carrito::getAsignacionRuta)
                    .map(AsignacionRuta::getRuta)
                    .map(Ruta::getPrecio)
                    .orElse(BigDecimal.ZERO);
            byCompany.merge(empName, precio, BigDecimal::add);
        }
        List<CompanySalesItem> items = byCompany.entrySet().stream()
                .map(e -> new CompanySalesItem(e.getKey(), e.getValue()))
                .sorted((a,b) -> b.total.compareTo(a.total))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new SalesByCompanyResponse(items));
    }

    @GetMapping("/top-routes")
    public ResponseEntity<TopRoutesResponse> topRoutes(@RequestParam(required = false) String from,
                                                       @RequestParam(required = false) String to) {
        DateRange dr = resolveRange(from, to);
        List<DetalleVenta> detalles = detalleVentaRepository.findByVenta_FechaBetween(dr.from, dr.to);
        Map<String, Long> byRoute = new LinkedHashMap<>();
        for (DetalleVenta d : detalles) {
            String rutaLabel = Optional.ofNullable(d.getPasaje())
                    .map(Carrito::getAsignacionRuta)
                    .map(AsignacionRuta::getRuta)
                    .map(r -> {
                        String ori = (r.getSucursalOrigen()!=null && r.getSucursalOrigen().getProvincia()!=null) ? r.getSucursalOrigen().getProvincia() : "";
                        String des = (r.getSucursalDestino()!=null && r.getSucursalDestino().getProvincia()!=null) ? r.getSucursalDestino().getProvincia() : "";
                        return (ori + " - " + des).trim();
                    })
                    .orElse("Ruta desconocida");
            byRoute.merge(rutaLabel, 1L, Long::sum);
        }
        List<TopRouteItem> items = byRoute.entrySet().stream()
                .map(e -> new TopRouteItem(e.getKey(), e.getValue()))
                .sorted((a,b) -> Long.compare(b.vendidos, a.vendidos))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new TopRoutesResponse(items));
    }

    private static class DateRange { LocalDate from; LocalDate to; DateRange(LocalDate f, LocalDate t){from=f;to=t;} }
    private DateRange resolveRange(String from, String to) {
        LocalDate now = LocalDate.now();
        if (from != null && to != null) {
            return new DateRange(LocalDate.parse(from), LocalDate.parse(to));
        }
        LocalDate start = now.minusDays(29);
        LocalDate end = now;
        return new DateRange(start, end);
    }
}
