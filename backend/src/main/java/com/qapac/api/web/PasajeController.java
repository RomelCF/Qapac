package com.qapac.api.web;

import com.qapac.api.domain.*;
import com.qapac.api.domain.enums.CarritoEstado;
import com.qapac.api.domain.enums.Disponibilidad;
import com.qapac.api.repository.*;
import com.qapac.api.web.dto.PasajeListItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@RestController
@RequestMapping("/pasajes")
public class PasajeController {

    private final CarritoRepository carritoRepository;
    private final AsignacionEmpleadoRepository asignacionEmpleadoRepository;
    private final ChoferRepository choferRepository;
    private final AzafatoRepository azafatoRepository;
    private final TelefonoEmpresaRepository telefonoEmpresaRepository;
    private final AsientoRepository asientoRepository;

    public PasajeController(CarritoRepository carritoRepository,
                            AsignacionEmpleadoRepository asignacionEmpleadoRepository,
                            ChoferRepository choferRepository,
                            AzafatoRepository azafatoRepository,
                            TelefonoEmpresaRepository telefonoEmpresaRepository,
                            AsientoRepository asientoRepository) {
        this.carritoRepository = carritoRepository;
        this.asignacionEmpleadoRepository = asignacionEmpleadoRepository;
        this.choferRepository = choferRepository;
        this.azafatoRepository = azafatoRepository;
        this.telefonoEmpresaRepository = telefonoEmpresaRepository;
        this.asientoRepository = asientoRepository;
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<PasajeListItem>> listPasajesByCliente(@PathVariable("idCliente") Integer idCliente) {
        List<Carrito> tickets = carritoRepository
                .findByCliente_IdClienteAndEstadoOrderByFechaCreacionDesc(idCliente, CarritoEstado.pagado);

        List<PasajeListItem> out = new ArrayList<>();
        for (Carrito it : tickets) {
            AsignacionRuta ar = it.getAsignacionRuta();
            if (ar == null) continue;
            Ruta ruta = ar.getRuta();
            if (ruta == null) continue;
            Sucursal sOri = ruta.getSucursalOrigen();
            Sucursal sDes = ruta.getSucursalDestino();
            Bus bus = ar.getBus();
            Empresa emp = bus != null ? bus.getEmpresa() : null;
            String telefonoEmp = null;
            if (emp != null) {
                TelefonoEmpresa t = telefonoEmpresaRepository
                        .findFirstByEmpresa_IdEmpresaOrderByIdTelefonoEmpresaAsc(emp.getIdEmpresa());
                if (t != null) telefonoEmp = t.getTelefono();
            }

            List<AsignacionEmpleado> emps = asignacionEmpleadoRepository
                    .findByAsignacionRuta_IdAsignacionRuta(ar.getIdAsignacionRuta());
            List<String> choferes = emps.stream()
                    .map(AsignacionEmpleado::getEmpleado)
                    .filter(e -> e != null)
                    .filter(e -> choferRepository.existsByEmpleado_IdEmpleado(e.getIdEmpleado()))
                    .map(e -> ((e.getNombres() != null ? e.getNombres() : "") + " " + (e.getApellidos() != null ? e.getApellidos() : "")).trim())
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            List<String> azafatos = emps.stream()
                    .map(AsignacionEmpleado::getEmpleado)
                    .filter(e -> e != null)
                    .filter(e -> !choferRepository.existsByEmpleado_IdEmpleado(e.getIdEmpleado()))
                    .filter(e -> azafatoRepository.existsByEmpleado_IdEmpleado(e.getIdEmpleado()))
                    .map(e -> ((e.getNombres() != null ? e.getNombres() : "") + " " + (e.getApellidos() != null ? e.getApellidos() : "")).trim())
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            PasajeListItem dto = PasajeListItem.builder()
                    .idPasaje(it.getIdCarrito())
                    .origenProvincia(sOri != null ? sOri.getProvincia() : null)
                    .destinoProvincia(sDes != null ? sDes.getProvincia() : null)
                    .fecha(ar.getFechaPartida() != null ? ar.getFechaPartida().toString() : null)
                    .hora(ar.getHoraPartida() != null ? ar.getHoraPartida().toString() : null)
                    .fechaLlegada(ar.getFechaLlegada() != null ? ar.getFechaLlegada().toString() : null)
                    .horaLlegada(ar.getHoraLlegada() != null ? ar.getHoraLlegada().toString() : null)
                    .empresaNombre(emp != null ? emp.getNombre() : null)
                    .empresaNumero(telefonoEmp)
                    .busMatricula(bus != null ? bus.getMatricula() : null)
                    .seatCode(it.getAsiento() != null ? it.getAsiento().getCodigo() : null)
                    .precio(ruta.getPrecio() != null ? ruta.getPrecio().toString() : null)
                    .choferes(choferes)
                    .azafatos(azafatos)
                    .sucursalOrigen(sOri != null ? new PasajeListItem.SucursalInfo(sOri.getNombre(), sOri.getProvincia(), sOri.getDireccion()) : null)
                    .sucursalDestino(sDes != null ? new PasajeListItem.SucursalInfo(sDes.getNombre(), sDes.getProvincia(), sDes.getDireccion()) : null)
                    .build();
            out.add(dto);
        }

        return ResponseEntity.ok(out);
    }

    /**
     * Calcula el reembolso estimado: un tercio del precio.
     */
    @GetMapping("/{id}/refund")
    public ResponseEntity<?> refundInfo(@PathVariable("id") Integer id) {
        Optional<Carrito> opt = carritoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Carrito c = opt.get();
        if (c.getAsignacionRuta() == null || c.getAsignacionRuta().getRuta() == null) {
            return ResponseEntity.badRequest().body("Pasaje sin datos de ruta");
        }
        Ruta ruta = c.getAsignacionRuta().getRuta();
        BigDecimal precio = ruta.getPrecio() != null ? ruta.getPrecio() : BigDecimal.ZERO;
        // Reembolso es 1/3 del precio
        BigDecimal refund = precio.divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);
        return ResponseEntity.ok(new RefundResponse(precio.toPlainString(), refund.toPlainString(), 33));
    }

    public record RefundResponse(String precio, String refund, int percent) {}

    /**
     * Cancela un pasaje cambiando su estado a 'cancelado'.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancel(@PathVariable("id") Integer id) {
        Optional<Carrito> opt = carritoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Carrito c = opt.get();
        if (c.getEstado() == CarritoEstado.cancelado) {
            return ResponseEntity.ok().build();
        }
        c.setEstado(CarritoEstado.cancelado);
        // Liberar asiento si existe
        if (c.getAsiento() != null) {
            Asiento a = c.getAsiento();
            a.setDisponibilidad(Disponibilidad.disponible);
            asientoRepository.save(a);
        }
        carritoRepository.save(c);
        return ResponseEntity.ok().build();
    }
}
