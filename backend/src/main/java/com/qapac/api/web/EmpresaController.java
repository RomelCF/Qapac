package com.qapac.api.web;

import com.qapac.api.domain.Empresa;
import com.qapac.api.domain.Usuario;
import com.qapac.api.repository.EmpresaRepository;
import com.qapac.api.repository.UsuarioRepository;
import com.qapac.api.web.dto.CreateEmpresaRequest;
import com.qapac.api.web.dto.UpdateEmpresaRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;

    public EmpresaController(EmpresaRepository empresaRepository, UsuarioRepository usuarioRepository) {
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateEmpresaRequest req) {
        if (req.getIdUsuario() == null) {
            return ResponseEntity.badRequest().body("idUsuario es requerido");
        }
        Optional<Usuario> userOpt = usuarioRepository.findById(req.getIdUsuario());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario no encontrado");
        }
        Empresa e = Empresa.builder()
                .nombre(req.getNombre())
                .ruc(req.getRuc())
                .razonSocial(req.getRazonSocial())
                .usuario(userOpt.get())
                .build();
        e = empresaRepository.save(e);
        return ResponseEntity.created(URI.create("/empresas/" + e.getIdEmpresa())).body(e.getIdEmpresa());
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<?> getByUser(@PathVariable Integer userId) {
        var opt = empresaRepository.findByUsuario_IdUsuario(userId);
        return opt.<ResponseEntity<?>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{idEmpresa}")
    public ResponseEntity<?> getById(@PathVariable Integer idEmpresa) {
        Optional<Empresa> opt = empresaRepository.findById(idEmpresa);
        return opt.<ResponseEntity<?>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{idEmpresa}")
    public ResponseEntity<?> update(@PathVariable Integer idEmpresa, @Valid @RequestBody UpdateEmpresaRequest req) {
        Optional<Empresa> opt = empresaRepository.findById(idEmpresa);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Empresa e = opt.get();
        e.setNombre(req.getNombre());
        e.setRuc(req.getRuc());
        e.setRazonSocial(req.getRazonSocial());
        empresaRepository.save(e);
        return ResponseEntity.noContent().build();
    }
}
