package com.qapac.api.web;

import com.qapac.api.domain.*;
import com.qapac.api.domain.enums.CarritoEstado;
import com.qapac.api.repository.*;
import com.qapac.api.web.dto.PasajeListItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pasajes")
public class PasajeController {

    private final VentaRepository ventaRepository;
    private final AsignacionEmpleadoRepository asignacionEmpleadoRepository;
    private final ChoferRepository choferRepository;
    private final AzafatoRepository azafatoRepository;
    private final TelefonoEmpresaRepository telefonoEmpresaRepository;
    private final CarritoRepository carritoRepository;
    private final AsientoRepository asientoRepository;

    public PasajeController(VentaRepository ventaRepository,
                            AsignacionEmpleadoRepository asignacionEmpleadoRepository,
                            TelefonoEmpresaRepository telefonoEmpresaRepository,
                            CarritoRepository carritoRepository,
                            AsientoRepository asientoRepository,
                            ChoferRepository choferRepository,
                            AzafatoRepository azafatoRepository) {
        this.ventaRepository = ventaRepository;
        this.asignacionEmpleadoRepository = asignacionEmpleadoRepository;
        this.telefonoEmpresaRepository = telefonoEmpresaRepository;
        this.carritoRepository = carritoRepository;
        this.asientoRepository = asientoRepository;
        this.choferRepository = choferRepository;
        this.azafatoRepository = azafatoRepository;
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<PasajeListItem>> listByCliente(@PathVariable("idCliente") Integer idCliente) {
        List<Venta> ventas = ventaRepository.findByCarrito_Cliente_IdCliente(idCliente);
        List<PasajeListItem> out = new ArrayList<>();
        for (Venta v : ventas) {
            Carrito carrito = v.getCarrito();
            if (carrito == null) continue;
            // Mostrar solo pasajes pagados (no pendientes, no cancelados, no completados)
            if (carrito.getEstado() != CarritoEstado.pagado) continue;
            if (carrito == null || carrito.getAsignacionRuta() == null) continue;
            AsignacionRuta ar = carrito.getAsignacionRuta();
            Ruta ruta = ar.getRuta();
            if (ruta == null) continue;
            Sucursal sOri = ruta.getSucursalOrigen();
            Sucursal sDes = ruta.getSucursalDestino();
            Bus bus = ar.getBus();
            Empresa emp = bus != null ? bus.getEmpresa() : null;

            String telefonoEmp = null;
            if (emp != null) {
                TelefonoEmpresa t = telefonoEmpresaRepository.findFirstByEmpresa_IdEmpresaOrderByIdTelefonoEmpresaAsc(emp.getIdEmpresa());
                if (t != null) telefonoEmp = t.getTelefono();
            }

            List<AsignacionEmpleado> asignaciones = asignacionEmpleadoRepository.findByAsignacionRuta_IdAsignacionRuta(ar.getIdAsignacionRuta());
            List<String> choferes = asignaciones.stream()
                    .map(AsignacionEmpleado::getEmpleado)
                    .filter(e -> e != null)
                    .filter(e -> choferRepository.existsByEmpleado_IdEmpleado(e.getIdEmpleado()))
                    .map(e -> ((e.getNombres() != null ? e.getNombres() : "") + " " + (e.getApellidos() != null ? e.getApellidos() : "")).trim())
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            List<String> azafatos = asignaciones.stream()
                    .map(AsignacionEmpleado::getEmpleado)
                    .filter(e -> e != null)
                    .filter(e -> !choferRepository.existsByEmpleado_IdEmpleado(e.getIdEmpleado()))
                    .filter(e -> azafatoRepository.existsByEmpleado_IdEmpleado(e.getIdEmpleado()))
                    .map(e -> ((e.getNombres() != null ? e.getNombres() : "") + " " + (e.getApellidos() != null ? e.getApellidos() : "")).trim())
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            PasajeListItem.SucursalInfo oriInfo = sOri != null ? new PasajeListItem.SucursalInfo(sOri.getNombre(), sOri.getProvincia(), sOri.getDireccion()) : null;
            PasajeListItem.SucursalInfo desInfo = sDes != null ? new PasajeListItem.SucursalInfo(sDes.getNombre(), sDes.getProvincia(), sDes.getDireccion()) : null;

            PasajeListItem item = PasajeListItem.builder()
                    .idPasaje(v.getIdVenta())
                    .origenProvincia(sOri != null ? sOri.getProvincia() : null)
                    .destinoProvincia(sDes != null ? sDes.getProvincia() : null)
                    .fecha(ar.getFechaPartida() != null ? ar.getFechaPartida().toString() : null)
                    .hora(ar.getHoraPartida() != null ? ar.getHoraPartida().toString() : null)
                    .fechaLlegada(ar.getFechaLlegada() != null ? ar.getFechaLlegada().toString() : null)
                    .horaLlegada(ar.getHoraLlegada() != null ? ar.getHoraLlegada().toString() : null)
                    .empresaNombre(emp != null ? emp.getNombre() : null)
                    .empresaNumero(telefonoEmp)
                    .busMatricula(bus != null ? bus.getMatricula() : null)
                    .seatCode(carrito.getAsiento() != null ? carrito.getAsiento().getCodigo() : null)
                    .choferes(choferes)
                    .azafatos(azafatos)
                    .sucursalOrigen(oriInfo)
                    .sucursalDestino(desInfo)
                    .build();
            out.add(item);
        }
        return ResponseEntity.ok(out);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelar(@PathVariable("id") Integer id) {
        return ventaRepository.findById(id)
                .map(v -> {
                    Carrito c = v.getCarrito();
                    if (c != null) {
                        c.setEstado(CarritoEstado.cancelado);
                        // liberar asiento
                        Asiento a = c.getAsiento();
                        if (a != null) {
                            a.setDisponibilidad(com.qapac.api.domain.enums.Disponibilidad.disponible);
                            asientoRepository.save(a);
                        }
                        carritoRepository.save(c);
                    }
                    // mantener la venta para historial de movimientos; Mis pasajes filtra por estado pagado
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
