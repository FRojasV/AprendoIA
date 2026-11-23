package cl.triskeledu.juego.controller;

import cl.triskeledu.juego.dto.DescubrimientoResponse;
import cl.triskeledu.juego.dto.EscaneoRequest;
import cl.triskeledu.juego.dto.EscaneoResponse;
import cl.triskeledu.juego.dto.VistoRequest;
import cl.triskeledu.juego.service.JuegoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 *  POST /api/descubrimientos            registrar escaneo de QR (201 nuevo / 200 repetido)
 *  GET  /api/descubrimientos?usuarioId= descubrimientos del jugador
 *  PUT  /api/descubrimientos/visto      marcar cápsula como vista
 */
@RestController
@RequestMapping("/api/descubrimientos")
public class DescubrimientoController {

    private final JuegoService servicio;

    public DescubrimientoController(JuegoService servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    public ResponseEntity<EscaneoResponse> escanear(@Valid @RequestBody EscaneoRequest datos) {
        EscaneoResponse r = servicio.registrarEscaneo(datos);
        return ResponseEntity.status(r.nuevo() ? HttpStatus.CREATED : HttpStatus.OK).body(r);
    }

    @GetMapping
    public List<DescubrimientoResponse> listar(@RequestParam Long usuarioId) {
        return servicio.descubrimientos(usuarioId);
    }

    @PutMapping("/visto")
    public Map<String, Boolean> marcarVisto(@Valid @RequestBody VistoRequest datos) {
        return Map.of("actualizado", servicio.marcarVisto(datos));
    }
}
