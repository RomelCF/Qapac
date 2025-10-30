package com.qapac.api.web;

import com.qapac.api.domain.Bus;
import com.qapac.api.domain.Empresa;
import com.qapac.api.domain.Asiento;
import com.qapac.api.domain.enums.Disponibilidad;
import com.qapac.api.repository.BusRepository;
import com.qapac.api.repository.EmpresaRepository;
import com.qapac.api.repository.AsientoRepository;
import com.qapac.api.web.dto.CreateBusRequest;
import com.qapac.api.web.dto.UpdateBusRequest;
import com.qapac.api.web.dto.BusResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping
public class BusController {

    private final BusRepository busRepository;
    private final EmpresaRepository empresaRepository;
    private final AsientoRepository asientoRepository;

    public BusController(BusRepository busRepository, EmpresaRepository empresaRepository, AsientoRepository asientoRepository) {
        this.busRepository = busRepository;
        this.empresaRepository = empresaRepository;
        this.asientoRepository = asientoRepository;
    }

    @GetMapping("/buses")
    public ResponseEntity<List<BusResponse>> listByEmpresa(@RequestParam("empresaId") Integer empresaId) {
        List<Bus> buses = busRepository.findAllByEmpresa_IdEmpresa(empresaId);
        List<BusResponse> dto = buses.stream()
                .map(b -> new BusResponse(b.getIdBus(), b.getMatricula(), b.getCapacidad(), b.getEstado()))
                .toList();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/buses")
    public ResponseEntity<?> create(@Valid @RequestBody CreateBusRequest req) {
        Optional<Empresa> empOpt = empresaRepository.findById(req.getIdEmpresa());
        if (empOpt.isEmpty()) return ResponseEntity.badRequest().body("Empresa no encontrada");
        Bus b = Bus.builder()
                .matricula(req.getMatricula())
                .capacidad(req.getCapacidad())
                .estado(req.getEstado())
                .empresa(empOpt.get())
                .build();
        b = busRepository.save(b);
        // Registrar asientos si se envían
        if (req.getAsientos() != null && !req.getAsientos().isEmpty()) {
            for (CreateBusRequest.AsientoDto dto : req.getAsientos()) {
                if (dto.getCodigo() == null || dto.getCodigo().isBlank()) continue;
                Asiento as = Asiento.builder()
                        .codigo(dto.getCodigo())
                        .bus(b)
                        .disponibilidad(dto.getDisponibilidad() != null ? Disponibilidad.valueOf(dto.getDisponibilidad()) : Disponibilidad.disponible)
                        .build();
                asientoRepository.save(as);
            }
        }
        return ResponseEntity.created(URI.create("/buses/" + b.getIdBus())).body(b.getIdBus());
    }

    @PutMapping("/buses/{idBus}")
    public ResponseEntity<?> update(@PathVariable Integer idBus, @Valid @RequestBody UpdateBusRequest req) {
        Optional<Bus> opt = busRepository.findById(idBus);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Bus b = opt.get();
        b.setMatricula(req.getMatricula());
        b.setCapacidad(req.getCapacidad());
        b.setEstado(req.getEstado());
        busRepository.save(b);
        // --- Sincronizar asientos ---
        if (req.getAsientos() != null) {
            // Obtener asientos actuales
            List<Asiento> actuales = asientoRepository.findByBus_IdBusOrderByCodigoAsc(idBus);
            // Map de codigo->objeto
            java.util.Map<String, Asiento> mapAct = new java.util.HashMap<>();
            for (Asiento a : actuales) mapAct.put(a.getCodigo(), a);
            // 1. Eliminar los que ya no están
            for (Asiento a : actuales) {
                if (req.getAsientos().stream().noneMatch(nuevo -> nuevo.getCodigo() != null && nuevo.getCodigo().equals(a.getCodigo()))) {
                    asientoRepository.delete(a);
                }
            }
            // 2. Agregar o actualizar
            for (CreateBusRequest.AsientoDto dto : req.getAsientos()) {
                if (dto.getCodigo() == null || dto.getCodigo().isBlank()) continue;
                Asiento yaExiste = mapAct.get(dto.getCodigo());
                if (yaExiste == null) {
                    // Nuevo asiento
                    Asiento as = Asiento.builder()
                            .codigo(dto.getCodigo())
                            .bus(b)
                            .disponibilidad(dto.getDisponibilidad() != null ? com.qapac.api.domain.enums.Disponibilidad.valueOf(dto.getDisponibilidad()) : com.qapac.api.domain.enums.Disponibilidad.disponible)
                            .build();
                    asientoRepository.save(as);
                } else {
                    // Actualizar disponibilidad si cambia
                    com.qapac.api.domain.enums.Disponibilidad nuevaDisp = dto.getDisponibilidad() != null ? com.qapac.api.domain.enums.Disponibilidad.valueOf(dto.getDisponibilidad()) : com.qapac.api.domain.enums.Disponibilidad.disponible;
                    if(!nuevaDisp.equals(yaExiste.getDisponibilidad())) {
                        yaExiste.setDisponibilidad(nuevaDisp);
                        asientoRepository.save(yaExiste);
                    }
                }
            }
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/buses/{idBus}")
    public ResponseEntity<?> delete(@PathVariable Integer idBus) {
        if (!busRepository.existsById(idBus)) return ResponseEntity.notFound().build();
        busRepository.deleteById(idBus);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buses/{idBus}/asientos")
    public ResponseEntity<?> getAsientosByBus(@PathVariable Integer idBus) {
        var list = asientoRepository.findByBus_IdBusOrderByCodigoAsc(idBus);
        var out = list.stream()
            .map(a -> new AsientoItem(a.getIdAsiento(), a.getCodigo(), a.getDisponibilidad() != null ? a.getDisponibilidad().name() : null))
            .toList();
        return ResponseEntity.ok(out);
    }

    public record AsientoItem(Integer idAsiento, String codigo, String disponibilidad) {}
}
