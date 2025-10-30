package com.qapac.api.web;

import com.qapac.api.domain.Empleado;
import com.qapac.api.repository.EmpleadoRepository;
import com.qapac.api.repository.ChoferRepository;
import com.qapac.api.repository.AzafatoRepository;
import com.qapac.api.web.dto.EmpleadoResponse;
import com.qapac.api.web.dto.BreveteDto;
import com.qapac.api.web.dto.EmpleadoDtos.CreateEmpleadoRequest;
import com.qapac.api.web.dto.EmpleadoDtos.UpdateEmpleadoRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class EmpleadoController {

    private final EmpleadoRepository empleadoRepository;
    private final ChoferRepository choferRepository;
    private final AzafatoRepository azafatoRepository;

    public EmpleadoController(EmpleadoRepository empleadoRepository, ChoferRepository choferRepository, AzafatoRepository azafatoRepository) { 
        this.empleadoRepository = empleadoRepository;
        this.choferRepository = choferRepository;
        this.azafatoRepository = azafatoRepository;
    }

    @GetMapping("/empleados")
    public ResponseEntity<List<EmpleadoResponse>> listAll() {
        List<Empleado> list = empleadoRepository.findAll();
        List<EmpleadoResponse> out = list.stream()
                .map(e -> {
                    String tipo = calcularTipo(e.getIdEmpleado());
                    BreveteDto breveteDto = null;
                    if ("chofer".equals(tipo)) {
                        breveteDto = obtenerBrevete(e.getIdEmpleado());
                    }
                    return new EmpleadoResponse(
                        e.getIdEmpleado(), 
                        e.getDni(), 
                        e.getTelefono(), 
                        e.getDomicilio(), 
                        e.getNombres(), 
                        e.getApellidos(), 
                        e.getAniosExperiencia(), 
                        e.getFechaNacimiento(),
                        e.getDisponible(),
                        tipo,
                        breveteDto
                    );
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    private String calcularTipo(Integer idEmpleado) {
        if (choferRepository.existsByEmpleado_IdEmpleado(idEmpleado)) {
            return "chofer";
        } else if (azafatoRepository.existsByEmpleado_IdEmpleado(idEmpleado)) {
            return "azafato";
        }
        return "ninguno";
    }

    private BreveteDto obtenerBrevete(Integer idEmpleado) {
        var choferOpt = choferRepository.findByEmpleado_IdEmpleado(idEmpleado);
        if (choferOpt.isPresent() && choferOpt.get().getBrevete() != null) {
            var b = choferOpt.get().getBrevete();
            return new BreveteDto(
                b.getIdBrevete(),
                b.getNumero(),
                b.getFechaEmision() != null ? b.getFechaEmision().toString() : null,
                b.getFechaVencimiento() != null ? b.getFechaVencimiento().toString() : null
            );
        }
        return null;
    }

    @PostMapping("/empleados")
    public ResponseEntity<?> create(@RequestBody CreateEmpleadoRequest req) {
        if (req == null || isBlank(req.dni) || isBlank(req.nombres) || isBlank(req.apellidos) || req.fechaNacimiento == null) {
            return ResponseEntity.badRequest().body("DNI, nombres, apellidos y fecha de nacimiento son obligatorios");
        }
        if (req.dni.length() != 8) {
            return ResponseEntity.badRequest().body("El DNI debe tener 8 dígitos");
        }
        Empleado e = Empleado.builder()
                .dni(req.dni)
                .telefono(req.telefono)
                .domicilio(req.domicilio)
                .nombres(req.nombres)
                .apellidos(req.apellidos)
                .aniosExperiencia(req.aniosExperiencia)
                .fechaNacimiento(req.fechaNacimiento)
                .disponible(req.disponible != null ? req.disponible : true)
                .build();
        e = empleadoRepository.save(e);
        return ResponseEntity.ok(e.getIdEmpleado());
    }

    @PutMapping("/empleados/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Integer id, @RequestBody UpdateEmpleadoRequest req) {
        var opt = empleadoRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Empleado e = opt.get();
        if (!isBlank(req.dni)) {
            if (req.dni.length() != 8) {
                return ResponseEntity.badRequest().body("El DNI debe tener 8 dígitos");
            }
            e.setDni(req.dni);
        }
        if (req.telefono != null) e.setTelefono(req.telefono);
        if (req.domicilio != null) e.setDomicilio(req.domicilio);
        if (!isBlank(req.nombres)) e.setNombres(req.nombres);
        if (!isBlank(req.apellidos)) e.setApellidos(req.apellidos);
        if (req.aniosExperiencia != null) e.setAniosExperiencia(req.aniosExperiencia);
        if (req.fechaNacimiento != null) e.setFechaNacimiento(req.fechaNacimiento);
        if (req.disponible != null) e.setDisponible(req.disponible);
        empleadoRepository.save(e);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/empleados/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
        if (!empleadoRepository.existsById(id)) return ResponseEntity.notFound().build();
        empleadoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private boolean isBlank(String s) { return s == null || s.isBlank(); }
}
