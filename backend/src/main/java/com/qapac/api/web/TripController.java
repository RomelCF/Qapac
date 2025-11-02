package com.qapac.api.web;

import com.qapac.api.domain.AsignacionRuta;
import com.qapac.api.domain.Bus;
import com.qapac.api.domain.Ruta;
import com.qapac.api.domain.Empleado;
import com.qapac.api.domain.AsignacionEmpleado;
import com.qapac.api.repository.AsignacionRutaRepository;
import com.qapac.api.repository.BusRepository;
import com.qapac.api.repository.RutaRepository;
import com.qapac.api.repository.AsignacionEmpleadoRepository;
import com.qapac.api.repository.ChoferRepository;
import com.qapac.api.repository.AzafatoRepository;
import com.qapac.api.repository.AsientoRepository;
import com.qapac.api.repository.DetalleVentaRepository;
import com.qapac.api.repository.EmpleadoRepository;
import com.qapac.api.web.dto.TripDtos.CreateTripRequest;
import com.qapac.api.web.dto.TripDtos.UpdateTripRequest;
import com.qapac.api.web.dto.TripDtos.TripResponse;
import com.qapac.api.web.dto.TripDetailsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class TripController {

    private final AsignacionRutaRepository asignacionRutaRepository;
    private final RutaRepository rutaRepository;
    private final BusRepository busRepository;
    private final AsignacionEmpleadoRepository asignacionEmpleadoRepository;
    private final ChoferRepository choferRepository;
    private final AzafatoRepository azafatoRepository;
    private final AsientoRepository asientoRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final EmpleadoRepository empleadoRepository;

    public TripController(AsignacionRutaRepository asignacionRutaRepository, RutaRepository rutaRepository, BusRepository busRepository,
                          AsignacionEmpleadoRepository asignacionEmpleadoRepository,
                          ChoferRepository choferRepository,
                          AzafatoRepository azafatoRepository,
                          AsientoRepository asientoRepository,
                          DetalleVentaRepository detalleVentaRepository,
                          EmpleadoRepository empleadoRepository) {
        this.asignacionRutaRepository = asignacionRutaRepository;
        this.rutaRepository = rutaRepository;
        this.busRepository = busRepository;
        this.asignacionEmpleadoRepository = asignacionEmpleadoRepository;
        this.choferRepository = choferRepository;
        this.azafatoRepository = azafatoRepository;
        this.asientoRepository = asientoRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.empleadoRepository = empleadoRepository;
    }

    @GetMapping("/viajes")
    public ResponseEntity<List<TripResponse>> list(@RequestParam("empresaId") Integer empresaId) {
        List<AsignacionRuta> list = asignacionRutaRepository.findByRuta_Empresa_IdEmpresa(empresaId);
        List<TripResponse> out = list.stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    @PostMapping("/viajes")
    public ResponseEntity<?> create(@RequestBody CreateTripRequest req) {
        if (req == null || req.idRuta == null || req.idBus == null) return ResponseEntity.badRequest().body("Parámetros inválidos");
        Optional<Ruta> rOpt = rutaRepository.findById(req.idRuta);
        Optional<Bus> bOpt = busRepository.findById(req.idBus);
        if (rOpt.isEmpty() || bOpt.isEmpty()) return ResponseEntity.badRequest().body("Ruta o Bus no encontrado");
        AsignacionRuta v = AsignacionRuta.builder()
                .ruta(rOpt.get())
                .bus(bOpt.get())
                .fechaPartida(LocalDate.parse(req.fechaPartida))
                .horaPartida(LocalTime.parse(req.horaPartida.length()==5? req.horaPartida+":00" : req.horaPartida))
                .fechaLlegada(LocalDate.parse(req.fechaLlegada))
                .horaLlegada(LocalTime.parse(req.horaLlegada.length()==5? req.horaLlegada+":00" : req.horaLlegada))
                .build();
        v = asignacionRutaRepository.save(v);
        // Asignar chofer y azafatos si se envían
        if (req.idChofer != null) {
            var chOpt = empleadoRepository.findById(req.idChofer);
            if (chOpt.isEmpty()) return ResponseEntity.badRequest().body("Chofer no existe");
            if (asignacionEmpleadoRepository.existsByEmpleado_IdEmpleado(req.idChofer)) return ResponseEntity.badRequest().body("Chofer ya asignado a otro viaje");
            AsignacionEmpleado ae = AsignacionEmpleado.builder()
                    .asignacionRuta(v)
                    .empleado(chOpt.get())
                    .build();
            asignacionEmpleadoRepository.save(ae);
        }
        if (req.idAzafatos != null) {
            for (Integer idAz : req.idAzafatos) {
                if (idAz == null) continue;
                var azOpt = empleadoRepository.findById(idAz);
                if (azOpt.isEmpty()) return ResponseEntity.badRequest().body("Azafato no existe");
                if (asignacionEmpleadoRepository.existsByEmpleado_IdEmpleado(idAz)) return ResponseEntity.badRequest().body("Azafato ya asignado a otro viaje");
                AsignacionEmpleado ae = AsignacionEmpleado.builder()
                        .asignacionRuta(v)
                        .empleado(azOpt.get())
                        .build();
                asignacionEmpleadoRepository.save(ae);
            }
        }
        return ResponseEntity.created(URI.create("/viajes/"+v.getIdAsignacionRuta())).body(v.getIdAsignacionRuta());
    }

    @PutMapping("/viajes/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Integer id, @RequestBody UpdateTripRequest req) {
        Optional<AsignacionRuta> opt = asignacionRutaRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        AsignacionRuta v = opt.get();
        if (req.idRuta != null) {
            rutaRepository.findById(req.idRuta).ifPresent(v::setRuta);
        }
        if (req.idBus != null) {
            busRepository.findById(req.idBus).ifPresent(v::setBus);
        }
        if (req.fechaPartida != null) v.setFechaPartida(LocalDate.parse(req.fechaPartida));
        if (req.horaPartida != null) v.setHoraPartida(LocalTime.parse(req.horaPartida.length()==5? req.horaPartida+":00" : req.horaPartida));
        if (req.fechaLlegada != null) v.setFechaLlegada(LocalDate.parse(req.fechaLlegada));
        if (req.horaLlegada != null) v.setHoraLlegada(LocalTime.parse(req.horaLlegada.length()==5? req.horaLlegada+":00" : req.horaLlegada));
        asignacionRutaRepository.save(v);

        // Si el request incluye campos de empleados, resincornizar asignaciones
        boolean tocarAsignaciones = (req.idChofer != null) || (req.idAzafatos != null);
        if (tocarAsignaciones) {
            // Eliminar todas las asignaciones actuales de este viaje
            var actuales = asignacionEmpleadoRepository.findByAsignacionRuta_IdAsignacionRuta(v.getIdAsignacionRuta());
            for (var ae : actuales) asignacionEmpleadoRepository.delete(ae);
            // Añadir chofer si se envía
            if (req.idChofer != null) {
                var chOpt = empleadoRepository.findById(req.idChofer);
                if (chOpt.isEmpty()) return ResponseEntity.badRequest().body("Chofer no existe");
                if (asignacionEmpleadoRepository.existsByEmpleado_IdEmpleado(req.idChofer)) return ResponseEntity.badRequest().body("Chofer ya asignado a otro viaje");
                AsignacionEmpleado ae = AsignacionEmpleado.builder().asignacionRuta(v).empleado(chOpt.get()).build();
                asignacionEmpleadoRepository.save(ae);
            }
            if (req.idAzafatos != null) {
                for (Integer idAz : req.idAzafatos) {
                    if (idAz == null) continue;
                    var azOpt = empleadoRepository.findById(idAz);
                    if (azOpt.isEmpty()) return ResponseEntity.badRequest().body("Azafato no existe");
                    if (asignacionEmpleadoRepository.existsByEmpleado_IdEmpleado(idAz)) return ResponseEntity.badRequest().body("Azafato ya asignado a otro viaje");
                    AsignacionEmpleado ae = AsignacionEmpleado.builder().asignacionRuta(v).empleado(azOpt.get()).build();
                    asignacionEmpleadoRepository.save(ae);
                }
            }
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/viajes/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
        if (!asignacionRutaRepository.existsById(id)) return ResponseEntity.notFound().build();
        asignacionRutaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/viajes/{id}/detalles")
    public ResponseEntity<?> detalles(@PathVariable("id") Integer id) {
        Optional<AsignacionRuta> opt = asignacionRutaRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        AsignacionRuta v = opt.get();
        // choferes y azafatos asignados
        var emps = asignacionEmpleadoRepository.findByAsignacionRuta_IdAsignacionRuta(v.getIdAsignacionRuta());
        List<String> choferes = emps.stream()
                .map(a -> a.getEmpleado())
                .filter(e -> e != null)
                .filter(e -> choferRepository.existsByEmpleado_IdEmpleado(e.getIdEmpleado()))
                .map(e -> ((e.getNombres() != null ? e.getNombres() : "") + " " + (e.getApellidos() != null ? e.getApellidos() : "")).trim())
                .filter(s -> !s.isEmpty())
                .toList();
        List<String> azafatos = emps.stream()
                .map(a -> a.getEmpleado())
                .filter(e -> e != null)
                .filter(e -> !choferRepository.existsByEmpleado_IdEmpleado(e.getIdEmpleado()))
                .filter(e -> azafatoRepository.existsByEmpleado_IdEmpleado(e.getIdEmpleado()))
                .map(e -> ((e.getNombres() != null ? e.getNombres() : "") + " " + (e.getApellidos() != null ? e.getApellidos() : "")).trim())
                .filter(s -> !s.isEmpty())
                .toList();

        Bus bus = v.getBus();
        List<TripDetailsDto.SeatItem> seats = java.util.Collections.emptyList();
        int total = 0; int disp = 0;
        if (bus != null) {
            var list = asientoRepository.findByBus_IdBusOrderByCodigoAsc(bus.getIdBus());
            total = list.size();
            seats = list.stream()
                    .map(a -> new TripDetailsDto.SeatItem(a.getIdAsiento(), a.getCodigo(), a.getDisponibilidad()!=null? a.getDisponibilidad().name(): null))
                    .toList();
            long disponibles = list.stream().filter(a -> a.getDisponibilidad()!=null && a.getDisponibilidad().name().equals("disponible")).count();
            disp = (int)disponibles;
        }
        long vendidos = detalleVentaRepository.countByPasaje_AsignacionRuta_IdAsignacionRuta(v.getIdAsignacionRuta());
        return ResponseEntity.ok(new TripDetailsDto(choferes, azafatos, seats, total, disp, vendidos));
    }

    private TripResponse toDto(AsignacionRuta v) {
        Ruta r = v.getRuta();
        Bus b = v.getBus();
        String ori = r != null && r.getSucursalOrigen()!=null ? r.getSucursalOrigen().getProvincia() : null;
        String des = r != null && r.getSucursalDestino()!=null ? r.getSucursalDestino().getProvincia() : null;
        String mat = b != null ? b.getMatricula() : null;
        return new TripResponse(
                v.getIdAsignacionRuta(),
                r != null ? r.getIdRuta() : null,
                ori,
                des,
                b != null ? b.getIdBus() : null,
                mat,
                v.getFechaPartida()!=null? v.getFechaPartida().toString():null,
                v.getHoraPartida()!=null? v.getHoraPartida().toString():null,
                v.getFechaLlegada()!=null? v.getFechaLlegada().toString():null,
                v.getHoraLlegada()!=null? v.getHoraLlegada().toString():null
        );
    }

    // --- Disponibilidad ---
    public record IdName(Integer id, String name) {}

    @GetMapping("/viajes/buses-disponibles")
    public ResponseEntity<?> busesDisponibles(@RequestParam("empresaId") Integer empresaId,
                                              @RequestParam(value = "viajeId", required = false) Integer viajeId) {
        List<Bus> all = busRepository.findAllByEmpresa_IdEmpresa(empresaId);
        Integer currentBusId = null;
        if (viajeId != null) {
            var vOpt = asignacionRutaRepository.findById(viajeId);
            if (vOpt.isPresent() && vOpt.get().getBus()!=null) currentBusId = vOpt.get().getBus().getIdBus();
        }
        final Integer currId = currentBusId;
        List<Bus> libres = all.stream()
                .filter(b -> (currId != null && b.getIdBus().equals(currId)) || asignacionRutaRepository.findByBus_IdBus(b.getIdBus()).isEmpty())
                .toList();
        var out = libres.stream().map(b -> new IdName(b.getIdBus(), b.getMatricula())).toList();
        return ResponseEntity.ok(out);
    }

    @GetMapping("/viajes/empleados-disponibles")
    public ResponseEntity<?> empleadosDisponibles(@RequestParam(value = "viajeId", required = false) Integer viajeId) {
        // Sin relación directa con empresa en Empleado; listamos globalmente los no asignados actualmente
        List<Empleado> all = empleadoRepository.findAll();
        java.util.Set<Integer> currentAssigned = new java.util.HashSet<>();
        if (viajeId != null) {
            var cur = asignacionEmpleadoRepository.findByAsignacionRuta_IdAsignacionRuta(viajeId);
            for (var ae : cur) if (ae.getEmpleado()!=null) currentAssigned.add(ae.getEmpleado().getIdEmpleado());
        }
        var choferes = all.stream()
                .filter(e -> choferRepository.existsByEmpleado_IdEmpleado(e.getIdEmpleado()))
                .filter(e -> currentAssigned.contains(e.getIdEmpleado()) || !asignacionEmpleadoRepository.existsByEmpleado_IdEmpleado(e.getIdEmpleado()))
                .map(e -> new IdName(e.getIdEmpleado(), ((e.getNombres()!=null?e.getNombres():"")+" "+(e.getApellidos()!=null?e.getApellidos():"")).trim()))
                .toList();
        var azafatos = all.stream()
                .filter(e -> azafatoRepository.existsByEmpleado_IdEmpleado(e.getIdEmpleado()))
                .filter(e -> currentAssigned.contains(e.getIdEmpleado()) || !asignacionEmpleadoRepository.existsByEmpleado_IdEmpleado(e.getIdEmpleado()))
                .map(e -> new IdName(e.getIdEmpleado(), ((e.getNombres()!=null?e.getNombres():"")+" "+(e.getApellidos()!=null?e.getApellidos():"")).trim()))
                .toList();
        return ResponseEntity.ok(java.util.Map.of("choferes", choferes, "azafatos", azafatos));
    }

    @GetMapping("/viajes/{id}/empleados-asignados")
    public ResponseEntity<?> empleadosAsignados(@PathVariable("id") Integer id) {
        var list = asignacionEmpleadoRepository.findByAsignacionRuta_IdAsignacionRuta(id);
        java.util.List<Integer> choferIds = new java.util.ArrayList<>();
        java.util.List<Integer> azafatoIds = new java.util.ArrayList<>();
        for (var ae : list) {
            var e = ae.getEmpleado();
            if (e == null) continue;
            if (choferRepository.existsByEmpleado_IdEmpleado(e.getIdEmpleado())) choferIds.add(e.getIdEmpleado());
            else if (azafatoRepository.existsByEmpleado_IdEmpleado(e.getIdEmpleado())) azafatoIds.add(e.getIdEmpleado());
        }
        Integer choferId = choferIds.isEmpty() ? null : choferIds.get(0);
        return ResponseEntity.ok(java.util.Map.of("idChofer", choferId, "idAzafatos", azafatoIds));
    }
}
