package com.qapac.api.web;

import com.qapac.api.domain.MetodoPago;
import com.qapac.api.domain.Carrito;
import com.qapac.api.domain.Venta;
import com.qapac.api.domain.Asiento;
import com.qapac.api.repository.AsientoRepository;
import com.qapac.api.domain.Tarjeta;
import com.qapac.api.domain.DetalleVenta;
import com.qapac.api.domain.enums.CarritoEstado;
import com.qapac.api.repository.MetodoPagoRepository;
import com.qapac.api.repository.CarritoRepository;
import com.qapac.api.repository.VentaRepository;
import com.qapac.api.repository.TarjetaRepository;
import com.qapac.api.repository.DetalleVentaRepository;
import com.qapac.api.web.dto.PaymentMethodItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/metodos-pago")
public class PaymentController {

    private final MetodoPagoRepository metodoPagoRepository;
    private final CarritoRepository carritoRepository;
    private final VentaRepository ventaRepository;
    private final TarjetaRepository tarjetaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final AsientoRepository asientoRepository;

    public PaymentController(MetodoPagoRepository metodoPagoRepository,
                             CarritoRepository carritoRepository,
                             VentaRepository ventaRepository,
                             TarjetaRepository tarjetaRepository,
                             DetalleVentaRepository detalleVentaRepository,
                             AsientoRepository asientoRepository) {
        this.metodoPagoRepository = metodoPagoRepository;
        this.carritoRepository = carritoRepository;
        this.ventaRepository = ventaRepository;
        this.tarjetaRepository = tarjetaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.asientoRepository = asientoRepository;
    }

    @GetMapping
    public ResponseEntity<List<PaymentMethodItem>> list() {
        List<MetodoPago> all = metodoPagoRepository.findAll();
        List<PaymentMethodItem> out = all.stream().map(mp -> PaymentMethodItem.builder()
                .idMetodoPago(mp.getIdMetodoPago())
                .nombre(mp.getNombre())
                .tipo(mp.getTipoPago() != null ? mp.getTipoPago().getNombre() : null)
                .descripcion(mp.getDescripcion())
                .estado(mp.getEstado() != null ? mp.getEstado().name() : null)
                .comision(mp.getComision())
                .build()).collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    public record ConfirmPaymentRequest(Integer idCliente, Integer idMetodoPago, Integer idTarjeta) {}

    @PostMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestBody ConfirmPaymentRequest req) {
        if (req == null || req.idCliente() == null || req.idMetodoPago() == null) {
            return ResponseEntity.badRequest().body("Parámetros inválidos");
        }
        MetodoPago mp = metodoPagoRepository.findById(req.idMetodoPago()).orElse(null);
        if (mp == null) return ResponseEntity.badRequest().body("Método de pago no existe");
        Tarjeta tarjeta = null;
        if (req.idTarjeta() != null) {
            tarjeta = tarjetaRepository.findById(req.idTarjeta()).orElse(null);
        }
        List<Carrito> carritos = carritoRepository.findByCliente_IdCliente(req.idCliente());
        int created = 0;
        java.time.LocalDate hoy = java.time.LocalDate.now();
        java.time.LocalTime ahora = java.time.LocalTime.now();
        for (Carrito c : carritos) {
            if (c.getAsignacionRuta() == null) continue;
            // saltar si ya vendido o estado no pendiente
            boolean vendido = detalleVentaRepository.existsByPasaje_IdCarrito(c.getIdCarrito());
            if (vendido) continue;
            if (c.getEstado() != null && c.getEstado() != CarritoEstado.pendiente) continue;
            // crear venta
            Venta v = Venta.builder()
                    .metodoPago(mp)
                    .tarjeta(tarjeta)
                    .fecha(hoy)
                    .hora(ahora)
                    .build();
            v = ventaRepository.save(v);
            // crear detalle
            DetalleVenta dv = DetalleVenta.builder()
                    .venta(v)
                    .pasaje(c)
                    .build();
            detalleVentaRepository.save(dv);
            // Actualizar el estado del pasaje a 'pagado'
            c.setEstado(CarritoEstado.pagado);
            carritoRepository.save(c);
            
            // Marcar el asiento como ocupado
            if (c.getAsiento() != null) {
                c.getAsiento().setDisponibilidad(com.qapac.api.domain.enums.Disponibilidad.ocupado);
                asientoRepository.save(c.getAsiento());
            }
            created++;
        }
        return ResponseEntity.ok(created);
    }
}
