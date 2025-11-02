package com.qapac.api.web;

import com.qapac.api.domain.TiempoReembolso;
import com.qapac.api.repository.TiempoReembolsoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/config/reembolso")
public class TiempoReembolsoController {

    private final TiempoReembolsoRepository repo;

    public TiempoReembolsoController(TiempoReembolsoRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public ResponseEntity<TiempoReembolso> get() {
        Optional<TiempoReembolso> opt = repo.findById(1);
        return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.ok(new TiempoReembolso(1, 0)));
    }

    public static record UpdateReq(Integer horas) {}

    @PutMapping
    public ResponseEntity<?> update(@RequestBody UpdateReq req) {
        if (req == null || req.horas() == null || req.horas() < 0) {
            return ResponseEntity.badRequest().body("Horas inválidas");
        }
        TiempoReembolso tr = repo.findById(1).orElse(new TiempoReembolso(1, req.horas()));
        tr.setHoras(req.horas());
        repo.save(tr);
        return ResponseEntity.ok().build();
    }
}
