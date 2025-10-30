package com.qapac.api.web;

import com.qapac.api.domain.Brevete;
import com.qapac.api.domain.Chofer;
import com.qapac.api.repository.BreveteRepository;
import com.qapac.api.repository.ChoferRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping
public class BreveteController {

    private final BreveteRepository breveteRepository;
    private final ChoferRepository choferRepository;

    public BreveteController(BreveteRepository breveteRepository, ChoferRepository choferRepository) {
        this.breveteRepository = breveteRepository;
        this.choferRepository = choferRepository;
    }

    @PostMapping("/empleados/{idEmpleado}/brevete")
    public ResponseEntity<?> asignarBrevete(@PathVariable Integer idEmpleado, @RequestBody BreveteRequest req) {
        if (req == null || isBlank(req.numero) || req.fechaEmision == null || req.fechaVencimiento == null) {
            return ResponseEntity.badRequest().body("Todos los campos son obligatorios");
        }

        var choferOpt = choferRepository.findByEmpleado_IdEmpleado(idEmpleado);
        if (choferOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("El empleado no es un chofer");
        }

        Brevete brevete = Brevete.builder()
                .numero(req.numero)
                .fechaEmision(req.fechaEmision)
                .fechaVencimiento(req.fechaVencimiento)
                .build();
        brevete = breveteRepository.save(brevete);

        Chofer chofer = choferOpt.get();
        chofer.setBrevete(brevete);
        choferRepository.save(chofer);

        return ResponseEntity.ok(brevete.getIdBrevete());
    }

    @PutMapping("/brevetes/{idBrevete}")
    public ResponseEntity<?> actualizarBrevete(@PathVariable Integer idBrevete, @RequestBody BreveteRequest req) {
        var breveteOpt = breveteRepository.findById(idBrevete);
        if (breveteOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Brevete brevete = breveteOpt.get();
        if (!isBlank(req.numero)) brevete.setNumero(req.numero);
        if (req.fechaEmision != null) brevete.setFechaEmision(req.fechaEmision);
        if (req.fechaVencimiento != null) brevete.setFechaVencimiento(req.fechaVencimiento);
        
        breveteRepository.save(brevete);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/empleados/{idEmpleado}/brevete")
    public ResponseEntity<?> quitarBrevete(@PathVariable Integer idEmpleado) {
        var choferOpt = choferRepository.findByEmpleado_IdEmpleado(idEmpleado);
        if (choferOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("El empleado no es un chofer");
        }

        Chofer chofer = choferOpt.get();
        Brevete brevete = chofer.getBrevete();
        
        if (brevete != null) {
            chofer.setBrevete(null);
            choferRepository.save(chofer);
            breveteRepository.delete(brevete);
        }

        return ResponseEntity.noContent().build();
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    public static class BreveteRequest {
        public String numero;
        public LocalDate fechaEmision;
        public LocalDate fechaVencimiento;
    }
}
