package com.qapac.api.web;

import com.qapac.api.domain.Cliente;
import com.qapac.api.domain.Usuario;
import com.qapac.api.repository.ClienteRepository;
import com.qapac.api.repository.UsuarioRepository;
import com.qapac.api.web.dto.CreateClienteRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    public ClienteController(ClienteRepository clienteRepository, UsuarioRepository usuarioRepository) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateClienteRequest req) {
        if (req.getIdUsuario() == null) {
            return ResponseEntity.badRequest().body("idUsuario es requerido");
        }
        Optional<Usuario> userOpt = usuarioRepository.findById(req.getIdUsuario());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuario no encontrado");
        }
        Cliente c = Cliente.builder()
                .nombres(req.getNombres())
                .apellidos(req.getApellidos())
                .domicilio(req.getDomicilio())
                .dni(req.getDni())
                .telefono(req.getTelefono())
                .usuario(userOpt.get())
                .build();
        c = clienteRepository.save(c);
        return ResponseEntity.created(URI.create("/clientes/" + c.getIdCliente())).body(c.getIdCliente());
    }
}
