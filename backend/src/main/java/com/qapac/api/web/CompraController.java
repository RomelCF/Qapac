package com.qapac.api.web;

import com.qapac.api.domain.*;
import com.qapac.api.domain.enums.CarritoEstado;
import com.qapac.api.domain.enums.Disponibilidad;
import com.qapac.api.repository.*;
import com.qapac.api.web.dto.CompraOptionItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/compras")
public class CompraController {

    private final AsignacionRutaRepository asignacionRutaRepository;
    private final AsignacionEmpleadoRepository asignacionEmpleadoRepository;
    private final ChoferRepository choferRepository;
    private final AzafatoRepository azafatoRepository;
    private final TelefonoEmpresaRepository telefonoEmpresaRepository;
    private final CarritoRepository carritoRepository;
    private final AsientoRepository asientoRepository;
    private final ClienteRepository clienteRepository;

    public CompraController(AsignacionRutaRepository asignacionRutaRepository,
                            AsignacionEmpleadoRepository asignacionEmpleadoRepository,
                            ChoferRepository choferRepository,
                            AzafatoRepository azafatoRepository,
                            TelefonoEmpresaRepository telefonoEmpresaRepository,
                            CarritoRepository carritoRepository,
                            AsientoRepository asientoRepository,
                            ClienteRepository clienteRepository) {
        this.asignacionRutaRepository = asignacionRutaRepository;
        this.asignacionEmpleadoRepository = asignacionEmpleadoRepository;
        this.choferRepository = choferRepository;
        this.azafatoRepository = azafatoRepository;
        this.telefonoEmpresaRepository = telefonoEmpresaRepository;
        this.carritoRepository = carritoRepository;
        this.asientoRepository = asientoRepository;
        this.clienteRepository = clienteRepository;
    }

    @GetMapping("/opciones")
    public ResponseEntity<List<CompraOptionItem>> listOpciones() {
        List<AsignacionRuta> asignaciones = asignacionRutaRepository.findAll();
        List<CompraOptionItem> out = new ArrayList<>();
        for (AsignacionRuta ar : asignaciones) {
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

            List<AsignacionEmpleado> emps = asignacionEmpleadoRepository.findByAsignacionRuta_IdAsignacionRuta(ar.getIdAsignacionRuta());
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

            CompraOptionItem item = CompraOptionItem.builder()
                    .idAsignacionRuta(ar.getIdAsignacionRuta())
                    .origenProvincia(sOri != null ? sOri.getProvincia() : null)
                    .destinoProvincia(sDes != null ? sDes.getProvincia() : null)
                    .fecha(ar.getFechaPartida() != null ? ar.getFechaPartida().toString() : null)
                    .hora(ar.getHoraPartida() != null ? ar.getHoraPartida().toString() : null)
                    .empresaNombre(emp != null ? emp.getNombre() : null)
                    .empresaNumero(telefonoEmp)
                    .busMatricula(bus != null ? bus.getMatricula() : null)
                    .precio(ruta.getPrecio())
                    .choferes(choferes)
                    .azafatos(azafatos)
                    .sucursalOrigen(sOri != null ? new com.qapac.api.web.dto.PasajeListItem.SucursalInfo(sOri.getNombre(), sOri.getProvincia(), sOri.getDireccion()) : null)
                    .sucursalDestino(sDes != null ? new com.qapac.api.web.dto.PasajeListItem.SucursalInfo(sDes.getNombre(), sDes.getProvincia(), sDes.getDireccion()) : null)
                    .build();
            out.add(item);
        }
        return ResponseEntity.ok(out);
    }

    public record AddCarritoRequest(Integer idCliente, Integer idAsignacionRuta, Integer idAsiento) {}

    public record AsientoDto(Integer idAsiento, String codigo, String disponibilidad) {}

    @GetMapping("/asientos/{idAsignacionRuta}")
    public ResponseEntity<?> listAsientos(@PathVariable("idAsignacionRuta") Integer idAsignacionRuta) {
        var arOpt = asignacionRutaRepository.findById(idAsignacionRuta);
        if (arOpt.isEmpty()) return ResponseEntity.notFound().build();
        Bus bus = arOpt.get().getBus();
        if (bus == null) return ResponseEntity.badRequest().body("Asignación sin bus");
        List<Asiento> seats = asientoRepository.findByBus_IdBusOrderByCodigoAsc(bus.getIdBus());
        List<AsientoDto> out = seats.stream()
                .map(a -> new AsientoDto(a.getIdAsiento(), a.getCodigo(), a.getDisponibilidad() != null ? a.getDisponibilidad().name() : null))
                .toList();
        return ResponseEntity.ok(out);
    }

    @PostMapping("/carrito")
    public ResponseEntity<?> addCarrito(@RequestBody AddCarritoRequest req) {
        if (req == null || req.idCliente() == null || req.idAsignacionRuta() == null) {
            return ResponseEntity.badRequest().body("Parámetros inválidos");
        }
        var cliOpt = clienteRepository.findById(req.idCliente());
        if (cliOpt.isEmpty()) return ResponseEntity.notFound().build();
        var arOpt = asignacionRutaRepository.findById(req.idAsignacionRuta());
        if (arOpt.isEmpty()) return ResponseEntity.notFound().build();
        AsignacionRuta ar = arOpt.get();
        Bus bus = ar.getBus();
        if (bus == null) return ResponseEntity.badRequest().body("Asignación sin bus");
        Asiento asientoSeleccionado;
        if (req.idAsiento() != null) {
            var seatOpt = asientoRepository.findById(req.idAsiento());
            if (seatOpt.isEmpty()) return ResponseEntity.badRequest().body("Asiento no existe");
            Asiento a = seatOpt.get();
            if (a.getBus() == null || !a.getBus().getIdBus().equals(bus.getIdBus())) {
                return ResponseEntity.badRequest().body("Asiento no pertenece a este bus");
            }
            if (a.getDisponibilidad() != Disponibilidad.disponible) {
                return ResponseEntity.badRequest().body("Asiento no disponible");
            }
            asientoSeleccionado = a;
        } else {
            Asiento libre = asientoRepository.findFirstByBus_IdBusAndDisponibilidadOrderByCodigoAsc(bus.getIdBus(), Disponibilidad.disponible);
            if (libre == null) return ResponseEntity.badRequest().body("Sin asientos disponibles");
            asientoSeleccionado = libre;
        }

        asientoSeleccionado.setDisponibilidad(Disponibilidad.ocupado);
        asientoRepository.save(asientoSeleccionado);

        Carrito c = Carrito.builder()
                .cliente(cliOpt.get())
                .asignacionRuta(ar)
                .asiento(asientoSeleccionado)
                .estado(CarritoEstado.pendiente)
                .build();
        c = carritoRepository.save(c);
        return ResponseEntity.ok(c.getIdCarrito());
    }
}
