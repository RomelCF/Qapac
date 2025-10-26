package com.qapac.api.web;

import com.qapac.api.domain.Usuario;
import com.qapac.api.repository.UsuarioRepository;
import com.qapac.api.web.dto.CreateUsuarioRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateUsuarioRequest req) {
        Optional<Usuario> existing = usuarioRepository.findByCorreoElectronico(req.getCorreoElectronico());
        if (existing.isPresent()) {
            return ResponseEntity.badRequest().body("Correo ya registrado");
        }
        Usuario u = Usuario.builder()
                .correoElectronico(req.getCorreoElectronico())
                .contrasena(req.getContrasena())
                .build();
        u = usuarioRepository.save(u);
        return ResponseEntity.created(URI.create("/usuarios/" + u.getIdUsuario())).body(u.getIdUsuario());
    }
}
