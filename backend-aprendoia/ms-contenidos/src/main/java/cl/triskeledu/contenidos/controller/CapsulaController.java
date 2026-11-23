package cl.triskeledu.contenidos.controller;

import cl.triskeledu.contenidos.dto.CapsulaRequest;
import cl.triskeledu.contenidos.dto.CapsulaResponse;
import cl.triskeledu.contenidos.service.CapsulaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  GET    /api/capsulas                     todas (?publicadas=true, ?puntoId=1)
 *  GET    /api/capsulas/{id}                detalle
 *  GET    /api/capsulas/codigo/{codigo}     buscar por código QR (solo publicadas)
 *  POST   /api/capsulas                     crear
 *  PUT    /api/capsulas/{id}                editar
 *  PATCH  /api/capsulas/{id}/publicar       publicar (Administrador)
 *  DELETE /api/capsulas/{id}                eliminar
 */
@RestController
@RequestMapping("/api/capsulas")
public class CapsulaController {

    private final CapsulaService servicio;

    public CapsulaController(CapsulaService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<CapsulaResponse> listar(
            @RequestParam(defaultValue = "false") boolean publicadas,
            @RequestParam(required = false) Long puntoId) {
        return servicio.listar(publicadas, puntoId);
    }

    @GetMapping("/{id}")
    public CapsulaResponse obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @GetMapping("/codigo/{codigo}")
    public CapsulaResponse porCodigo(@PathVariable String codigo) {
        return servicio.buscarPorCodigo(codigo);
    }

    @PostMapping
    public ResponseEntity<CapsulaResponse> crear(@Valid @RequestBody CapsulaRequest datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.crear(datos));
    }

    @PutMapping("/{id}")
    public CapsulaResponse actualizar(@PathVariable Long id, @Valid @RequestBody CapsulaRequest datos) {
        return servicio.actualizar(id, datos);
    }

    @PatchMapping("/{id}/publicar")
    public CapsulaResponse publicar(@PathVariable Long id) {
        return servicio.publicar(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
