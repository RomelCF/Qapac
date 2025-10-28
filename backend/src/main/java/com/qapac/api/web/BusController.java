package com.qapac.api.web;

import com.qapac.api.domain.Bus;
import com.qapac.api.domain.Empresa;
import com.qapac.api.repository.BusRepository;
import com.qapac.api.repository.EmpresaRepository;
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

    public BusController(BusRepository busRepository, EmpresaRepository empresaRepository) {
        this.busRepository = busRepository;
        this.empresaRepository = empresaRepository;
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
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/buses/{idBus}")
    public ResponseEntity<?> delete(@PathVariable Integer idBus) {
        if (!busRepository.existsById(idBus)) return ResponseEntity.notFound().build();
        busRepository.deleteById(idBus);
        return ResponseEntity.noContent().build();
    }
}
