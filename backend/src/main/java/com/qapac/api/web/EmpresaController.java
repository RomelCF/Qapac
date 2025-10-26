package com.qapac.api.web;

import com.qapac.api.domain.Empresa;
import com.qapac.api.domain.Usuario;
import com.qapac.api.repository.EmpresaRepository;
import com.qapac.api.repository.UsuarioRepository;
import com.qapac.api.web.dto.CreateEmpresaRequest;
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
}
