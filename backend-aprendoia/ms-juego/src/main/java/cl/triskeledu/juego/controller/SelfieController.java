package cl.triskeledu.juego.controller;

import cl.triskeledu.juego.dto.SelfieRequest;
import cl.triskeledu.juego.dto.SelfieResponse;
import cl.triskeledu.juego.service.JuegoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  POST   /api/selfies             registrar selfie en hito (+100)
 *  GET    /api/selfies?usuarioId=  selfies del jugador
 *  DELETE /api/selfies/{id}        eliminar selfie
 */
@RestController
@RequestMapping("/api/selfies")
public class SelfieController {

    private final JuegoService servicio;

    public SelfieController(JuegoService servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    public ResponseEntity<SelfieResponse> registrar(@Valid @RequestBody SelfieRequest datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.registrarSelfie(datos));
    }

    @GetMapping
    public List<SelfieResponse> listar(@RequestParam Long usuarioId) {
        return servicio.selfies(usuarioId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminarSelfie(id);
        return ResponseEntity.noContent().build();
    }
}
