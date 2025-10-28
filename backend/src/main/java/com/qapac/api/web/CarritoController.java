package com.qapac.api.web;

import com.qapac.api.domain.*;
import com.qapac.api.domain.enums.CarritoEstado;
import com.qapac.api.repository.*;
import com.qapac.api.web.dto.CarritoListItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/carritos")
public class CarritoController {

    private final CarritoRepository carritoRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final AsignacionEmpleadoRepository asignacionEmpleadoRepository;
    private final TelefonoEmpresaRepository telefonoEmpresaRepository;
    private final ChoferRepository choferRepository;
    private final AzafatoRepository azafatoRepository;
    private final AsientoRepository asientoRepository;

    public CarritoController(CarritoRepository carritoRepository,
                             DetalleVentaRepository detalleVentaRepository,
                             AsignacionEmpleadoRepository asignacionEmpleadoRepository,
                             TelefonoEmpresaRepository telefonoEmpresaRepository,
                             ChoferRepository choferRepository,
                             AzafatoRepository azafatoRepository,
                             AsientoRepository asientoRepository) {
        this.carritoRepository = carritoRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.asignacionEmpleadoRepository = asignacionEmpleadoRepository;
        this.telefonoEmpresaRepository = telefonoEmpresaRepository;
        this.choferRepository = choferRepository;
        this.azafatoRepository = azafatoRepository;
        this.asientoRepository = asientoRepository;
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<CarritoListItem>> listPendientes(@PathVariable("idCliente") Integer idCliente) {
        System.out.println("Buscando carritos para el cliente: " + idCliente);
        List<Carrito> carritos = carritoRepository.findByCliente_IdCliente(idCliente);
        System.out.println("Carritos encontrados: " + carritos.size());
        List<CarritoListItem> out = new ArrayList<>();
        for (Carrito c : carritos) {
            System.out.println("Procesando carrito ID: " + c.getIdCarrito() + ", Estado: " + c.getEstado());
            if (c.getAsignacionRuta() == null) {
                System.out.println("Carrito " + c.getIdCarrito() + " sin asignación de ruta, omitiendo...");
                continue;
            }
            // Mostrar todos los carritos, no solo los pendientes
            // if (c.getEstado() != CarritoEstado.pendiente) continue;
            AsignacionRuta ar = c.getAsignacionRuta();
            LocalDate f = ar.getFechaPartida();
            LocalTime h = ar.getHoraPartida();
            boolean vendido = detalleVentaRepository.existsByPasaje_IdCarrito(c.getIdCarrito());
            if (vendido) continue;

            Ruta ruta = ar.getRuta();
            if (ruta == null) continue;
            Sucursal sOri = ruta.getSucursalOrigen();
            Sucursal sDes = ruta.getSucursalDestino();
            Bus bus = ar.getBus();
            Empresa emp = bus != null ? bus.getEmpresa() : null;
            // si ya inició, no mostrar el carrito (pero no eliminar)
            boolean yaInicio = false;
            if (f != null && h != null) {
                var salida = java.time.LocalDateTime.of(f, h);
                yaInicio = java.time.LocalDateTime.now().isAfter(salida) || java.time.LocalDateTime.now().isEqual(salida);
            }
            if (yaInicio) {
                if (c.getEstado() != null && c.getEstado() != CarritoEstado.pendiente) {
                    continue;
                }
            }
            // No ocultar por capacidad alcanzada: el carrito del usuario debe mostrar sus ítems
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

            com.qapac.api.web.dto.PasajeListItem.SucursalInfo oriInfo = sOri != null ? new com.qapac.api.web.dto.PasajeListItem.SucursalInfo(sOri.getNombre(), sOri.getProvincia(), sOri.getDireccion()) : null;
            com.qapac.api.web.dto.PasajeListItem.SucursalInfo desInfo = sDes != null ? new com.qapac.api.web.dto.PasajeListItem.SucursalInfo(sDes.getNombre(), sDes.getProvincia(), sDes.getDireccion()) : null;

            CarritoListItem item = CarritoListItem.builder()
                    .idCarrito(c.getIdCarrito())
                    .origenProvincia(sOri != null ? sOri.getProvincia() : null)
                    .destinoProvincia(sDes != null ? sDes.getProvincia() : null)
                    .fecha(f != null ? f.toString() : null)
                    .hora(h != null ? h.toString() : null)
                    .empresaNombre(emp != null ? emp.getNombre() : null)
                    .empresaNumero(telefonoEmp)
                    .busMatricula(bus != null ? bus.getMatricula() : null)
                    .seatCode(c.getAsiento() != null ? c.getAsiento().getCodigo() : null)
                    .precio(ruta != null ? ruta.getPrecio() : null)
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
    public ResponseEntity<?> eliminar(@PathVariable("id") Integer id) {
        return carritoRepository.findById(id)
                .map(c -> {
                    boolean vendido = detalleVentaRepository.existsByPasaje_IdCarrito(c.getIdCarrito());
                    if (vendido) {
                        return ResponseEntity.status(409).body("El carrito ya fue vendido");
                    }
                    // liberar asiento
                    Asiento a = c.getAsiento();
                    if (a != null) {
                        a.setDisponibilidad(com.qapac.api.domain.enums.Disponibilidad.disponible);
                        asientoRepository.save(a);
                    }
                    carritoRepository.delete(c);
                    return ResponseEntity.noContent().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
