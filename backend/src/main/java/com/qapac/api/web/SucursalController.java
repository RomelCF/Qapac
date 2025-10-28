package com.qapac.api.web;

import com.qapac.api.domain.Sucursal;
import com.qapac.api.repository.SucursalRepository;
import com.qapac.api.web.dto.SucursalResponse;
import com.qapac.api.web.dto.SucursalDtos.CreateSucursalRequest;
import com.qapac.api.web.dto.SucursalDtos.UpdateSucursalRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class SucursalController {

    private final SucursalRepository sucursalRepository;

    public SucursalController(SucursalRepository sucursalRepository) { this.sucursalRepository = sucursalRepository; }

    @GetMapping("/sucursales")
    public ResponseEntity<List<SucursalResponse>> listAll() {
        List<Sucursal> list = sucursalRepository.findAll();
        List<SucursalResponse> out = list.stream()
                .map(s -> new SucursalResponse(s.getIdSucursal(), s.getNombre(), s.getDepartamento(), s.getProvincia(), s.getDireccion()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    @PostMapping("/sucursales")
    public ResponseEntity<?> create(@RequestBody CreateSucursalRequest req) {
        if (req == null || isBlank(req.nombre) || isBlank(req.departamento) || isBlank(req.provincia) || isBlank(req.direccion)) {
            return ResponseEntity.badRequest().body("Parámetros inválidos");
        }
        Sucursal s = Sucursal.builder()
                .nombre(req.nombre)
                .departamento(req.departamento)
                .provincia(req.provincia)
                .direccion(req.direccion)
                .build();
        s = sucursalRepository.save(s);
        return ResponseEntity.ok(s.getIdSucursal());
    }

    @PutMapping("/sucursales/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Integer id, @RequestBody UpdateSucursalRequest req) {
        var opt = sucursalRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Sucursal s = opt.get();
        if (!isBlank(req.nombre)) s.setNombre(req.nombre);
        if (!isBlank(req.departamento)) s.setDepartamento(req.departamento);
        if (!isBlank(req.provincia)) s.setProvincia(req.provincia);
        if (!isBlank(req.direccion)) s.setDireccion(req.direccion);
        sucursalRepository.save(s);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/sucursales/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
        if (!sucursalRepository.existsById(id)) return ResponseEntity.notFound().build();
        sucursalRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}
