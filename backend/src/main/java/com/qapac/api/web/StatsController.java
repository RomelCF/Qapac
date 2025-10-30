package com.qapac.api.web;

import com.qapac.api.domain.*;
import com.qapac.api.domain.enums.BusEstado;
import com.qapac.api.repository.*;
import com.qapac.api.web.dto.StatsDtos.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stats")
public class StatsController {

    private final DetalleVentaRepository detalleVentaRepository;
    private final AsignacionRutaRepository asignacionRutaRepository;
    private final AsientoRepository asientoRepository;
    private final BusRepository busRepository;

    public StatsController(DetalleVentaRepository detalleVentaRepository,
                           AsignacionRutaRepository asignacionRutaRepository,
                           AsientoRepository asientoRepository,
                           BusRepository busRepository) {
        this.detalleVentaRepository = detalleVentaRepository;
        this.asignacionRutaRepository = asignacionRutaRepository;
        this.asientoRepository = asientoRepository;
        this.busRepository = busRepository;
    }

    @GetMapping("/kpis")
    public ResponseEntity<KpisResponse> kpis(@RequestParam Integer empresaId,
                                             @RequestParam(required = false) String from,
                                             @RequestParam(required = false) String to) {
        DateRange dr = resolveRange(from, to);
        List<DetalleVenta> detalles = detalleVentaRepository
                .findByVenta_FechaBetweenAndPasaje_AsignacionRuta_Ruta_Empresa_IdEmpresa(dr.from, dr.to, empresaId);

        BigDecimal ingresos = detalles.stream()
                .map(DetalleVenta::getPasaje)
                .filter(Objects::nonNull)
                .map(Carrito::getAsignacionRuta)
                .filter(Objects::nonNull)
                .map(AsignacionRuta::getRuta)
                .filter(Objects::nonNull)
                .map(r->r.getPrecio()!=null?r.getPrecio():BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long tickets = detalles.size();

        // Ocupación promedio por viaje en rango
        List<AsignacionRuta> viajes = asignacionRutaRepository
                .findByRuta_Empresa_IdEmpresaAndFechaPartidaBetween(empresaId, dr.from, dr.to);
        int ocupacionPromedio = 0;
        if (!viajes.isEmpty()) {
            long totalPorcentaje = 0;
            int count = 0;
            for (AsignacionRuta v : viajes) {
                Bus bus = v.getBus();
                if (bus == null) continue;
                long total = asientoRepository.countByBus_IdBus(bus.getIdBus());
                if (total == 0) continue;
                long vendidos = detalleVentaRepository.countByPasaje_AsignacionRuta_IdAsignacionRuta(v.getIdAsignacionRuta());
                int pct = (int)Math.round((vendidos * 100.0) / total);
                totalPorcentaje += pct;
                count++;
            }
            if (count > 0) ocupacionPromedio = (int)(totalPorcentaje / count);
        }

        // Buses activos: no inactivos
        int busesActivos = (int)(
                busRepository.countByEstadoAndEmpresa_IdEmpresa(BusEstado.disponible, empresaId) +
                busRepository.countByEstadoAndEmpresa_IdEmpresa(BusEstado.en_ruta, empresaId) +
                busRepository.countByEstadoAndEmpresa_IdEmpresa(BusEstado.mantenimiento, empresaId)
        );

        return ResponseEntity.ok(new KpisResponse(ingresos, tickets, ocupacionPromedio, busesActivos));
    }

    @GetMapping("/sales-daily")
    public ResponseEntity<SalesDailyResponse> salesDaily(@RequestParam Integer empresaId,
                                                         @RequestParam(required = false) String from,
                                                         @RequestParam(required = false) String to) {
        DateRange dr = resolveRange(from, to);
        List<DetalleVenta> detalles = detalleVentaRepository
                .findByVenta_FechaBetweenAndPasaje_AsignacionRuta_Ruta_Empresa_IdEmpresa(dr.from, dr.to, empresaId);
        Map<LocalDate, BigDecimal> byDate = new TreeMap<>();
        for (DetalleVenta d : detalles) {
            Venta v = d.getVenta();
            if (v == null || v.getFecha() == null) continue;
            BigDecimal precio = Optional.ofNullable(d.getPasaje())
                    .map(Carrito::getAsignacionRuta)
                    .map(AsignacionRuta::getRuta)
                    .map(r->r.getPrecio()!=null?r.getPrecio():BigDecimal.ZERO)
                    .orElse(BigDecimal.ZERO);
            byDate.merge(v.getFecha(), precio, BigDecimal::add);
        }
        List<SalesPoint> points = byDate.entrySet().stream()
                .map(e -> new SalesPoint(e.getKey().toString(), e.getValue()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new SalesDailyResponse(points));
    }

    @GetMapping("/bus-states")
    public ResponseEntity<BusStatesResponse> busStates(@RequestParam Integer empresaId) {
        List<BusStateItem> states = new ArrayList<>();
        states.add(new BusStateItem("Disponible", busRepository.countByEstadoAndEmpresa_IdEmpresa(BusEstado.disponible, empresaId)));
        states.add(new BusStateItem("En ruta", busRepository.countByEstadoAndEmpresa_IdEmpresa(BusEstado.en_ruta, empresaId)));
        states.add(new BusStateItem("Mantenimiento", busRepository.countByEstadoAndEmpresa_IdEmpresa(BusEstado.mantenimiento, empresaId)));
        long inact = busRepository.countByEstadoAndEmpresa_IdEmpresa(BusEstado.inactivo, empresaId);
        states.add(new BusStateItem("Inactivo", inact));
        return ResponseEntity.ok(new BusStatesResponse(states));
    }

    @GetMapping("/occupancy")
    public ResponseEntity<OccupancyResponse> occupancy(@RequestParam Integer empresaId,
                                                       @RequestParam(required = false) String from,
                                                       @RequestParam(required = false) String to) {
        DateRange dr = resolveRange(from, to);
        List<AsignacionRuta> viajes = asignacionRutaRepository
                .findByRuta_Empresa_IdEmpresaAndFechaPartidaBetween(empresaId, dr.from, dr.to);
        Map<String, List<Integer>> byRoute = new LinkedHashMap<>();
        for (AsignacionRuta v : viajes) {
            Bus bus = v.getBus();
            if (bus == null) continue;
            long total = asientoRepository.countByBus_IdBus(bus.getIdBus());
            if (total == 0) continue;
            long vendidos = detalleVentaRepository.countByPasaje_AsignacionRuta_IdAsignacionRuta(v.getIdAsignacionRuta());
            int pct = (int)Math.round((vendidos * 100.0) / total);
            Ruta r = v.getRuta();
            String label = r!=null && r.getSucursalOrigen()!=null && r.getSucursalOrigen().getProvincia()!=null && r.getSucursalDestino()!=null && r.getSucursalDestino().getProvincia()!=null
                    ? (r.getSucursalOrigen().getProvincia() + " - " + r.getSucursalDestino().getProvincia()) : "Ruta " + v.getIdAsignacionRuta();
            byRoute.computeIfAbsent(label, k -> new ArrayList<>()).add(pct);
        }
        List<OccupancyItem> items = byRoute.entrySet().stream()
                .map(e -> new OccupancyItem(e.getKey(), (int)Math.round(e.getValue().stream().mapToInt(i->i).average().orElse(0))))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new OccupancyResponse(items));
    }

    private static class DateRange { LocalDate from; LocalDate to; DateRange(LocalDate f, LocalDate t){from=f;to=t;} }
    private DateRange resolveRange(String from, String to) {
        LocalDate now = LocalDate.now();
        if (from != null && to != null) {
            return new DateRange(LocalDate.parse(from), LocalDate.parse(to));
        }
        // por defecto últimos 30 días del mes actual
        LocalDate start = now.minusDays(29);
        LocalDate end = now;
        return new DateRange(start, end);
    }
}
