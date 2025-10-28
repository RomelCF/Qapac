package com.qapac.api.web;

import com.qapac.api.domain.Usuario;
import com.qapac.api.repository.UsuarioRepository;
import com.qapac.api.web.dto.CreateUsuarioRequest;
import com.qapac.api.web.dto.UpdateUsuarioRequest;
import com.qapac.api.web.dto.UserResponse;
import com.qapac.api.repository.AdministradorRepository;
import com.qapac.api.repository.ClienteRepository;
import com.qapac.api.repository.EmpresaRepository;
import com.qapac.api.domain.Administrador;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final AdministradorRepository administradorRepository;
    private final ClienteRepository clienteRepository;
    private final EmpresaRepository empresaRepository;

    public UsuarioController(UsuarioRepository usuarioRepository, AdministradorRepository administradorRepository,
                             ClienteRepository clienteRepository, EmpresaRepository empresaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.administradorRepository = administradorRepository;
        this.clienteRepository = clienteRepository;
        this.empresaRepository = empresaRepository;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> list() {
        List<Usuario> list = usuarioRepository.findAll();
        List<UserResponse> out = list.stream()
                .map(u -> {
                    boolean isAdmin = administradorRepository.existsByUsuario_IdUsuario(u.getIdUsuario());
                    String tipo;
                    if (isAdmin) tipo = "admin";
                    else if (clienteRepository.existsByUsuario_IdUsuario(u.getIdUsuario())) tipo = "cliente";
                    else if (empresaRepository.existsByUsuario_IdUsuario(u.getIdUsuario())) tipo = "empresa";
                    else tipo = "none";
                    return new UserResponse(u.getIdUsuario(), u.getCorreoElectronico(), isAdmin, tipo);
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(out);
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

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Integer id, @RequestBody UpdateUsuarioRequest req) {
        Optional<Usuario> opt = usuarioRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Usuario u = opt.get();
        if (req.getCorreoElectronico() != null && !req.getCorreoElectronico().isBlank()) {
            u.setCorreoElectronico(req.getCorreoElectronico());
        }
        if (req.getContrasena() != null && !req.getContrasena().isBlank()) {
            u.setContrasena(req.getContrasena());
        }
        usuarioRepository.save(u);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
        if (!usuarioRepository.existsById(id)) return ResponseEntity.notFound().build();
        // Quitar admin si lo fuera
        if (administradorRepository.existsByUsuario_IdUsuario(id)) {
            administradorRepository.deleteByUsuario_IdUsuario(id);
        }
        usuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
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

    @PostMapping("/{id}/admin")
    public ResponseEntity<?> makeAdmin(@PathVariable("id") Integer id) {
        Optional<Usuario> opt = usuarioRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        if (!administradorRepository.existsByUsuario_IdUsuario(id)) {
            Administrador a = Administrador.builder().usuario(opt.get()).build();
            administradorRepository.save(a);
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/admin")
    public ResponseEntity<?> removeAdmin(@PathVariable("id") Integer id) {
        if (!usuarioRepository.existsById(id)) return ResponseEntity.notFound().build();
        if (administradorRepository.existsByUsuario_IdUsuario(id)) {
            administradorRepository.deleteByUsuario_IdUsuario(id);
        }
        return ResponseEntity.noContent().build();
    }
}
