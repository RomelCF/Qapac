package com.qapac.api.web;

import com.qapac.api.domain.Usuario;
import com.qapac.api.web.dto.LoginRequest;
import com.qapac.api.web.dto.LoginResponse;
import com.qapac.api.repository.UsuarioRepository;
import com.qapac.api.repository.ClienteRepository;
import com.qapac.api.repository.EmpresaRepository;
import com.qapac.api.web.dto.ProfileResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final EmpresaRepository empresaRepository;

    public AuthController(UsuarioRepository usuarioRepository,
                          ClienteRepository clienteRepository,
                          EmpresaRepository empresaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.empresaRepository = empresaRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Optional<Usuario> userOpt = usuarioRepository.findByCorreoElectronico(request.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "Credenciales inválidas", null));
        }
        Usuario u = userOpt.get();
        if (!u.getContrasena().equals(request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "Credenciales inválidas", null));
        }
        return ResponseEntity.ok(new LoginResponse(true, "Autenticación exitosa", u.getIdUsuario()));
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> profile(@RequestParam("userId") Integer userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        var clienteOpt = clienteRepository.findByUsuario_IdUsuario(userId);
        if (clienteOpt.isPresent()) {
            return ResponseEntity.ok(new ProfileResponse("cliente", clienteOpt.get().getIdCliente(), null));
        }
        var empresaOpt = empresaRepository.findByUsuario_IdUsuario(userId);
        if (empresaOpt.isPresent()) {
            return ResponseEntity.ok(new ProfileResponse("empresa", null, empresaOpt.get().getIdEmpresa()));
        }
        return ResponseEntity.ok(new ProfileResponse("none", null, null));
    }
}
