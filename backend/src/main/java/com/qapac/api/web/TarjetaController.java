package com.qapac.api.web;

import com.qapac.api.domain.Tarjeta;
import com.qapac.api.domain.Cliente;
import com.qapac.api.domain.MetodoPago;
import com.qapac.api.domain.TipoTarjeta;
import com.qapac.api.repository.ClienteRepository;
import com.qapac.api.repository.MetodoPagoRepository;
import com.qapac.api.repository.TarjetaRepository;
import com.qapac.api.repository.TipoTarjetaRepository;
import com.qapac.api.web.dto.TarjetaItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tarjetas")
public class TarjetaController {

    private final TarjetaRepository tarjetaRepository;
    private final ClienteRepository clienteRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final TipoTarjetaRepository tipoTarjetaRepository;

    public TarjetaController(TarjetaRepository tarjetaRepository,
                             ClienteRepository clienteRepository,
                             MetodoPagoRepository metodoPagoRepository,
                             TipoTarjetaRepository tipoTarjetaRepository) {
        this.tarjetaRepository = tarjetaRepository;
        this.clienteRepository = clienteRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.tipoTarjetaRepository = tipoTarjetaRepository;
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<TarjetaItem>> listByCliente(@PathVariable("idCliente") Integer idCliente) {
        List<Tarjeta> cards = tarjetaRepository.findByCliente_IdCliente(idCliente);
        List<TarjetaItem> out = cards.stream().map(t -> TarjetaItem.builder()
                .idTarjeta(t.getIdTarjeta())
                .numeroMasked(mask(t.getNumero()))
                .fechaCaducidad(t.getFechaCaducidad() != null ? t.getFechaCaducidad().toString() : null)
                .marca(t.getTipoTarjeta() != null ? t.getTipoTarjeta().getNombre() : null)
                .metodoPago(t.getMetodoPago() != null ? t.getMetodoPago().getNombre() : null)
                .build()).collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    public record CreateTarjetaRequest(Integer idCliente, Integer idMetodoPago, Integer idTipoTarjeta, String numero, String fechaCaducidad, String cvv) {}

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateTarjetaRequest req) {
        if (req == null || req.idCliente() == null || req.idMetodoPago() == null || req.idTipoTarjeta() == null
                || req.numero() == null || req.fechaCaducidad() == null || req.cvv() == null) {
            return ResponseEntity.badRequest().body("Parámetros inválidos");
        }
        var cliOpt = clienteRepository.findById(req.idCliente());
        if (cliOpt.isEmpty()) return ResponseEntity.badRequest().body("Cliente no existe");
        var mpOpt = metodoPagoRepository.findById(req.idMetodoPago());
        if (mpOpt.isEmpty()) return ResponseEntity.badRequest().body("Método de pago no existe");
        var ttOpt = tipoTarjetaRepository.findById(req.idTipoTarjeta());
        if (ttOpt.isEmpty()) return ResponseEntity.badRequest().body("Tipo de tarjeta no existe");
        java.time.LocalDate cad;
        try { cad = java.time.LocalDate.parse(req.fechaCaducidad()); }
        catch (Exception ex) { return ResponseEntity.badRequest().body("Fecha de caducidad inválida (yyyy-MM-dd)"); }
        Tarjeta t = Tarjeta.builder()
                .cliente(cliOpt.get())
                .metodoPago(mpOpt.get())
                .tipoTarjeta(ttOpt.get())
                .numero(req.numero())
                .fechaCaducidad(cad)
                .cvv(req.cvv())
                .build();
        t = tarjetaRepository.save(t);
        return ResponseEntity.ok(t.getIdTarjeta());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Integer id) {
        return tarjetaRepository.findById(id)
                .map(t -> { tarjetaRepository.delete(t); return ResponseEntity.noContent().build(); })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private String mask(String numero) {
        if (numero == null || numero.length() < 4) return "****";
        String last4 = numero.substring(numero.length() - 4);
        return "**** **** **** " + last4;
    }
}
