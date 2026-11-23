package cl.triskeledu.contenidos.controller;

import cl.triskeledu.contenidos.dto.PuntoRequest;
import cl.triskeledu.contenidos.dto.PuntoResponse;
import cl.triskeledu.contenidos.service.PuntoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  GET    /api/puntos          lista de puntos
 *  GET    /api/puntos/{id}     detalle
 *  POST   /api/puntos          crear (Administrador)
 *  PUT    /api/puntos/{id}     editar (Administrador)
 *  DELETE /api/puntos/{id}     eliminar con sus cápsulas (Administrador)
 */
@RestController
@RequestMapping("/api/puntos")
public class PuntoController {

    private final PuntoService servicio;

    public PuntoController(PuntoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<PuntoResponse> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    public PuntoResponse obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PostMapping
    public ResponseEntity<PuntoResponse> crear(@Valid @RequestBody PuntoRequest datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.crear(datos));
    }

    @PutMapping("/{id}")
    public PuntoResponse actualizar(@PathVariable Long id, @Valid @RequestBody PuntoRequest datos) {
        return servicio.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
