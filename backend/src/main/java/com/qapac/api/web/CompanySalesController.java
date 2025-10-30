package com.qapac.api.web;

import com.qapac.api.domain.*;
import com.qapac.api.repository.DetalleVentaRepository;
import com.qapac.api.web.dto.CompanySalesDtos.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/company-sales")
public class CompanySalesController {

    private final DetalleVentaRepository detalleVentaRepository;

    public CompanySalesController(DetalleVentaRepository detalleVentaRepository) {
        this.detalleVentaRepository = detalleVentaRepository;
    }

    @GetMapping("/list")
    public ResponseEntity<SalesListResponse> list(@RequestParam Integer empresaId,
                                                  @RequestParam(required = false) String from,
                                                  @RequestParam(required = false) String to) {
        DateRange dr = resolveRange(from, to);
        List<DetalleVenta> detalles = detalleVentaRepository
                .findByVenta_FechaBetweenAndPasaje_AsignacionRuta_Ruta_Empresa_IdEmpresa(dr.from, dr.to, empresaId);
        List<SalesItem> items = new ArrayList<>();
        for (DetalleVenta d : detalles) {
            Venta v = d.getVenta();
            Carrito p = d.getPasaje();
            if (v == null || p == null) continue;
            AsignacionRuta ar = p.getAsignacionRuta();
            if (ar == null || ar.getRuta() == null) continue;
            Ruta r = ar.getRuta();
            String ruta = r.getSucursalOrigen()!=null && r.getSucursalDestino()!=null
                ? ( (r.getSucursalOrigen().getProvincia() != null ? r.getSucursalOrigen().getProvincia() : "") + " - " + (r.getSucursalDestino().getProvincia() != null ? r.getSucursalDestino().getProvincia() : "") )
                : "";
            String busMatricula = ar.getBus()!=null ? ar.getBus().getMatricula() : null;
            String asiento = p.getAsiento() != null ? p.getAsiento().getCodigo() : null;
            BigDecimal precio = r.getPrecio() != null ? r.getPrecio() : BigDecimal.ZERO;
            String cliente = (p.getCliente() != null && (p.getCliente().getNombres() != null || p.getCliente().getApellidos() != null))
                ? ((Optional.ofNullable(p.getCliente().getNombres()).orElse("") + " " + Optional.ofNullable(p.getCliente().getApellidos()).orElse("")).trim())
                : "";
            String metodo = v.getMetodoPago() != null ? v.getMetodoPago().getNombre() : (v.getTarjeta() != null ? "Tarjeta" : "");
            items.add(new SalesItem(v.getIdVenta(), d.getIdDetalleVenta(),
                    v.getFecha() != null ? v.getFecha().toString() : null,
                    v.getHora() != null ? v.getHora().toString() : null,
                    ruta, busMatricula, asiento, precio, cliente, metodo));
        }
        // ordenar por fecha desc, hora desc
        items.sort((a, b) -> {
            int cmp = Objects.compare(b.fecha, a.fecha, Comparator.nullsLast(String::compareTo));
            if (cmp != 0) return cmp;
            return Objects.compare(b.hora, a.hora, Comparator.nullsLast(String::compareTo));
        });
        return ResponseEntity.ok(new SalesListResponse(items));
    }

    @GetMapping("/detail/{idVenta}")
    public ResponseEntity<SaleDetailResponse> detail(@PathVariable("idVenta") Integer idVenta) {
        List<DetalleVenta> list = detalleVentaRepository.findByVenta_IdVenta(idVenta);
        if (list.isEmpty()) return ResponseEntity.notFound().build();
        Venta v = list.get(0).getVenta();
        Carrito p0 = list.get(0).getPasaje();
        AsignacionRuta ar = p0!=null? p0.getAsignacionRuta() : null;
        Ruta r = ar!=null? ar.getRuta() : null;
        String ruta = r != null ? ( (r.getSucursalOrigen()!=null? r.getSucursalOrigen().getProvincia() : "") + " - " + (r.getSucursalDestino()!=null? r.getSucursalDestino().getProvincia() : "") ) : "";
        String busMatricula = ar!=null && ar.getBus()!=null? ar.getBus().getMatricula() : null;
        String metodo = v.getMetodoPago()!=null? v.getMetodoPago().getNombre() : (v.getTarjeta()!=null? "Tarjeta" : "");
        String cliente = p0!=null && p0.getCliente()!=null? ((Optional.ofNullable(p0.getCliente().getNombres()).orElse("") + " " + Optional.ofNullable(p0.getCliente().getApellidos()).orElse("")).trim()) : "";
        List<SaleDetailItem> items = list.stream()
                .map(d -> new SaleDetailItem(d.getPasaje()!=null && d.getPasaje().getAsiento()!=null? d.getPasaje().getAsiento().getCodigo() : null,
                        r!=null? r.getPrecio() : BigDecimal.ZERO))
                .collect(Collectors.toList());
        BigDecimal total = items.stream().map(i->i.precio).reduce(BigDecimal.ZERO, BigDecimal::add);
        return ResponseEntity.ok(new SaleDetailResponse(
                v.getIdVenta(), v.getFecha()!=null? v.getFecha().toString():null, v.getHora()!=null? v.getHora().toString():null,
                ruta, busMatricula, metodo, cliente, total, items
        ));
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
