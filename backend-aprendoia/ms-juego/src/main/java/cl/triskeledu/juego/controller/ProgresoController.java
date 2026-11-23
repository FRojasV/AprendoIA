package cl.triskeledu.juego.controller;

import cl.triskeledu.juego.dto.ActividadResponse;
import cl.triskeledu.juego.dto.EstadisticasResponse;
import cl.triskeledu.juego.dto.RankingResponse;
import cl.triskeledu.juego.service.JuegoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  GET    /api/historial?usuarioId=      historial de actividades
 *  GET    /api/estadisticas/{usuarioId}  puntaje y contadores
 *  GET    /api/ranking?limite=10         ranking de jugadores
 *  DELETE /api/progreso/{usuarioId}      borrar todo el progreso de un jugador
 */
@RestController
@RequestMapping("/api")
public class ProgresoController {

    private final JuegoService servicio;

    public ProgresoController(JuegoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/historial")
    public List<ActividadResponse> historial(@RequestParam Long usuarioId) {
        return servicio.historial(usuarioId);
    }

    @GetMapping("/estadisticas/{usuarioId}")
    public EstadisticasResponse estadisticas(@PathVariable Long usuarioId) {
        return servicio.estadisticas(usuarioId);
    }

    @GetMapping("/ranking")
    public List<RankingResponse> ranking(@RequestParam(defaultValue = "10") int limite) {
        return servicio.ranking(limite);
    }

    @DeleteMapping("/progreso/{usuarioId}")
    public ResponseEntity<Void> eliminarProgreso(@PathVariable Long usuarioId) {
        servicio.eliminarProgreso(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
