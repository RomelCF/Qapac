package com.qapac.api.web;

import com.qapac.api.domain.Ruta;
import com.qapac.api.domain.Empresa;
import com.qapac.api.domain.Sucursal;
import com.qapac.api.repository.RutaRepository;
import com.qapac.api.repository.EmpresaRepository;
import com.qapac.api.repository.SucursalRepository;
import com.qapac.api.web.dto.RutaResponse;
import com.qapac.api.web.dto.RutaDtos.CreateRutaRequest;
import com.qapac.api.web.dto.RutaDtos.UpdateRutaRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class RutaController {

    private final RutaRepository rutaRepository;
    private final EmpresaRepository empresaRepository;
    private final SucursalRepository sucursalRepository;

    public RutaController(RutaRepository rutaRepository, EmpresaRepository empresaRepository, SucursalRepository sucursalRepository) {
        this.rutaRepository = rutaRepository;
        this.empresaRepository = empresaRepository;
        this.sucursalRepository = sucursalRepository;
    }

    @GetMapping("/rutas")
    public ResponseEntity<List<RutaResponse>> list(@RequestParam("empresaId") Integer empresaId) {
        List<Ruta> rutas = rutaRepository.findByEmpresa_IdEmpresa(empresaId);
        List<RutaResponse> out = rutas.stream().map(r -> new RutaResponse(
                r.getIdRuta(),
                r.getSucursalOrigen()!=null? r.getSucursalOrigen().getProvincia(): null,
                r.getSucursalDestino()!=null? r.getSucursalDestino().getProvincia(): null,
                r.getPrecio()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    @PostMapping("/rutas")
    public ResponseEntity<?> create(@RequestBody CreateRutaRequest req) {
        if (req == null || req.idEmpresa == null || req.idSucursalOrigen == null || req.idSucursalDestino == null || req.precio == null) {
            return ResponseEntity.badRequest().body("Parámetros inválidos");
        }
        var empOpt = empresaRepository.findById(req.idEmpresa);
        var oriOpt = sucursalRepository.findById(req.idSucursalOrigen);
        var desOpt = sucursalRepository.findById(req.idSucursalDestino);
        if (empOpt.isEmpty() || oriOpt.isEmpty() || desOpt.isEmpty()) return ResponseEntity.badRequest().body("Empresa u origen/destino no encontrados");
        Ruta r = Ruta.builder()
                .empresa(empOpt.get())
                .sucursalOrigen(oriOpt.get())
                .sucursalDestino(desOpt.get())
                .precio(req.precio)
                .build();
        r = rutaRepository.save(r);
        return ResponseEntity.created(URI.create("/rutas/"+r.getIdRuta())).body(r.getIdRuta());
    }

    @PutMapping("/rutas/{idRuta}")
    public ResponseEntity<?> update(@PathVariable Integer idRuta, @RequestBody UpdateRutaRequest req) {
        var opt = rutaRepository.findById(idRuta);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Ruta r = opt.get();
        if (req.idEmpresa != null) {
            empresaRepository.findById(req.idEmpresa).ifPresent(r::setEmpresa);
        }
        if (req.idSucursalOrigen != null) {
            sucursalRepository.findById(req.idSucursalOrigen).ifPresent(r::setSucursalOrigen);
        }
        if (req.idSucursalDestino != null) {
            sucursalRepository.findById(req.idSucursalDestino).ifPresent(r::setSucursalDestino);
        }
        if (req.precio != null) {
            r.setPrecio(req.precio);
        }
        rutaRepository.save(r);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/rutas/{idRuta}")
    public ResponseEntity<?> delete(@PathVariable Integer idRuta) {
        if (!rutaRepository.existsById(idRuta)) return ResponseEntity.notFound().build();
        rutaRepository.deleteById(idRuta);
        return ResponseEntity.noContent().build();
    }
}
