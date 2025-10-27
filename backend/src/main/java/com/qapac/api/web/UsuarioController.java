package com.qapac.api.web;

import com.qapac.api.domain.Usuario;
import com.qapac.api.repository.UsuarioRepository;
import com.qapac.api.web.dto.CreateUsuarioRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/{id}/logo")
    public ResponseEntity<byte[]> getLogo(@PathVariable("id") Integer id) {
        Optional<Usuario> opt = usuarioRepository.findById(id);
        if (opt.isEmpty() || opt.get().getLogo() == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] bytes = opt.get().getLogo();
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }

    @PutMapping(path = "/{id}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> putLogo(@PathVariable("id") Integer id,
                                     @RequestPart("file") MultipartFile file) {
        try {
            Optional<Usuario> opt = usuarioRepository.findById(id);
            if (opt.isEmpty()) return ResponseEntity.notFound().build();
            Usuario u = opt.get();
            u.setLogo(file != null ? file.getBytes() : null);
            usuarioRepository.save(u);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("No se pudo guardar el logo");
        }
    }
}
