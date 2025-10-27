package com.qapac.api.web;

import com.qapac.api.domain.*;
import com.qapac.api.domain.enums.CarritoEstado;
import com.qapac.api.repository.*;
import com.qapac.api.web.dto.MovimientoItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/movimientos")
public class MovimientoController {

    private final VentaRepository ventaRepository;
    private final TelefonoEmpresaRepository telefonoEmpresaRepository;
    private final CarritoRepository carritoRepository;

    public MovimientoController(VentaRepository ventaRepository,
                                TelefonoEmpresaRepository telefonoEmpresaRepository,
                                CarritoRepository carritoRepository) {
        this.ventaRepository = ventaRepository;
        this.telefonoEmpresaRepository = telefonoEmpresaRepository;
        this.carritoRepository = carritoRepository;
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<MovimientoItem>> list(@PathVariable("idCliente") Integer idCliente) {
        List<Venta> ventas = ventaRepository.findByCarrito_Cliente_IdCliente(idCliente);
        List<MovimientoItem> out = new ArrayList<>();
        Set<Integer> handledCarritos = new HashSet<>();
        for (Venta v : ventas) {
            Carrito c = v.getCarrito();
            if (c == null || c.getAsignacionRuta() == null) continue;
            AsignacionRuta ar = c.getAsignacionRuta();
            Ruta ruta = ar.getRuta();
            if (ruta == null) continue;
            Sucursal sOri = ruta.getSucursalOrigen();
            Sucursal sDes = ruta.getSucursalDestino();
            Bus bus = ar.getBus();
            Empresa emp = bus != null ? bus.getEmpresa() : null;
            MetodoPago mp = v.getMetodoPago();
            Tarjeta tj = v.getTarjeta();

            double precio = ruta.getPrecio() != null ? ruta.getPrecio().doubleValue() : 0.0;
            double comisionPct = (mp != null && mp.getComision() != null) ? mp.getComision().doubleValue() : 0.0;
            double comisionMonto = precio * comisionPct / 100.0;
            double neto = precio + comisionMonto;

            String tarjetaMasked = mask(tj != null ? tj.getNumero() : null);

            String tipo;
            double outBruto;
            double outComisionMonto;
            double outNeto;
            double originalBruto = precio;
            double originalNeto = neto;
            if (c.getEstado() == CarritoEstado.cancelado) {
                // Reembolso: 1/3 del dinero pagado (neto), representado como negativo
                tipo = "CANCELACION";
                double refund = neto / 3.0;
                outBruto = precio / 3.0;
                outComisionMonto = comisionMonto / 3.0;
                outNeto = refund; // positivo
            } else {
                // Compra (pagado o completado a futuro)
                if (c.getEstado() != CarritoEstado.pagado && c.getEstado() != CarritoEstado.completado) {
                    // ignorar pendientes en movimientos
                    handledCarritos.add(c.getIdCarrito());
                    continue;
                }
                tipo = "COMPRA";
                outBruto = precio;
                outComisionMonto = comisionMonto;
                outNeto = neto;
            }

            MovimientoItem item = MovimientoItem.builder()
                    .tipo(tipo)
                    .fecha(v.getFecha() != null ? v.getFecha().toString() : (LocalDate.now().toString()))
                    .hora(v.getHora() != null ? v.getHora().toString() : (LocalTime.now().toString()))
                    .origen(sOri != null ? sOri.getProvincia() : null)
                    .destino(sDes != null ? sDes.getProvincia() : null)
                    .fechaSalida(ar.getFechaPartida() != null ? ar.getFechaPartida().toString() : null)
                    .horaSalida(ar.getHoraPartida() != null ? ar.getHoraPartida().toString() : null)
                    .empresa(emp != null ? emp.getNombre() : null)
                    .busMatricula(bus != null ? bus.getMatricula() : null)
                    .metodoPago(mp != null ? mp.getNombre() : null)
                    .tarjetaMasked(tarjetaMasked)
                    .bruto(round2(outBruto))
                    .comisionPct(round2(comisionPct))
                    .comisionMonto(round2(outComisionMonto))
                    .neto(round2(outNeto))
                    .estadoCarrito(c.getEstado() != null ? c.getEstado().name() : null)
                    .originalBruto(round2(originalBruto))
                    .originalNeto(round2(originalNeto))
                    .build();
            out.add(item);
            handledCarritos.add(c.getIdCarrito());
        }

        // Agregar cancelaciones sin Venta (por casos previos donde se eliminó la venta)
        List<Carrito> carritosCliente = carritoRepository.findByCliente_IdCliente(idCliente);
        for (Carrito c : carritosCliente) {
            if (c.getEstado() != CarritoEstado.cancelado) continue;
            if (handledCarritos.contains(c.getIdCarrito())) continue;
            if (c.getAsignacionRuta() == null) continue;
            AsignacionRuta ar = c.getAsignacionRuta();
            Ruta ruta = ar.getRuta();
            if (ruta == null) continue;
            Sucursal sOri = ruta.getSucursalOrigen();
            Sucursal sDes = ruta.getSucursalDestino();
            Bus bus = ar.getBus();
            Empresa emp = bus != null ? bus.getEmpresa() : null;

            double precio = ruta.getPrecio() != null ? ruta.getPrecio().doubleValue() : 0.0;
            double comisionPct = 0.0; // desconocido sin venta/metodo; asumimos 0
            double comisionMonto = 0.0;
            double neto = precio + comisionMonto;

            double refund = neto / 3.0;
            double outBruto = precio / 3.0;
            double outComisionMonto = comisionMonto / 3.0;
            double outNeto = refund; // positivo

            MovimientoItem item = MovimientoItem.builder()
                    .tipo("CANCELACION")
                    .fecha(LocalDate.now().toString())
                    .hora(LocalTime.now().toString())
                    .origen(sOri != null ? sOri.getProvincia() : null)
                    .destino(sDes != null ? sDes.getProvincia() : null)
                    .fechaSalida(ar.getFechaPartida() != null ? ar.getFechaPartida().toString() : null)
                    .horaSalida(ar.getHoraPartida() != null ? ar.getHoraPartida().toString() : null)
                    .empresa(emp != null ? emp.getNombre() : null)
                    .busMatricula(bus != null ? bus.getMatricula() : null)
                    .metodoPago(null)
                    .tarjetaMasked(null)
                    .bruto(round2(outBruto))
                    .comisionPct(round2(comisionPct))
                    .comisionMonto(round2(outComisionMonto))
                    .neto(round2(outNeto))
                    .estadoCarrito(c.getEstado().name())
                    .originalBruto(round2(precio))
                    .originalNeto(round2(neto))
                    .build();
            out.add(item);
        }
        return ResponseEntity.ok(out);
    }

    private static String mask(String numero) {
        if (numero == null || numero.isEmpty()) return null;
        String digits = numero.replaceAll("\\s+", "");
        if (digits.length() <= 4) return digits;
        String last4 = digits.substring(digits.length() - 4);
        return "**** **** **** " + last4;
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
