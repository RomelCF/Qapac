package com.qapac.api.web;

import com.qapac.api.domain.TipoTarjeta;
import com.qapac.api.repository.TipoTarjetaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tipos-tarjeta")
public class TipoTarjetaController {

    private final TipoTarjetaRepository tipoTarjetaRepository;

    public TipoTarjetaController(TipoTarjetaRepository tipoTarjetaRepository) {
        this.tipoTarjetaRepository = tipoTarjetaRepository;
    }

    @GetMapping
    public ResponseEntity<List<TipoTarjeta>> list() {
        return ResponseEntity.ok(tipoTarjetaRepository.findAll());
    }
}
