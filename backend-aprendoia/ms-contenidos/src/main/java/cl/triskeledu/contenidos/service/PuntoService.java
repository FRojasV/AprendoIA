package cl.triskeledu.contenidos.service;

import cl.triskeledu.contenidos.dto.PuntoRequest;
import cl.triskeledu.contenidos.dto.PuntoResponse;
import cl.triskeledu.contenidos.exception.RecursoNoEncontradoException;
import cl.triskeledu.contenidos.model.Punto;
import cl.triskeledu.contenidos.repository.PuntoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** CRUD de puntos turísticos. */
@Service
public class PuntoService {

    private final PuntoRepository repositorio;

    public PuntoService(PuntoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<PuntoResponse> listar() {
        return repositorio.findAllByOrderByNombreAsc().stream().map(PuntoResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public PuntoResponse obtener(Long id) {
        return PuntoResponse.desde(buscar(id));
    }

    @Transactional
    public PuntoResponse crear(PuntoRequest datos) {
        Punto punto = new Punto();
        copiar(datos, punto);
        punto.setFechaCreacion(LocalDateTime.now());
        return PuntoResponse.desde(repositorio.save(punto));
    }

    @Transactional
    public PuntoResponse actualizar(Long id, PuntoRequest datos) {
        Punto punto = buscar(id);
        copiar(datos, punto);
        return PuntoResponse.desde(repositorio.save(punto));
    }

    /** Las cápsulas del punto se eliminan en cascada (FK ON DELETE CASCADE). */
    @Transactional
    public void eliminar(Long id) {
        repositorio.delete(buscar(id));
    }

    Punto buscar(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el punto " + id));
    }

    private void copiar(PuntoRequest d, Punto p) {
        p.setNombre(d.nombre().trim());
        p.setDescripcion(d.descripcion().trim());
        p.setInfoTuristica(d.infoTuristica() == null ? "" : d.infoTuristica().trim());
        p.setLatitud(d.latitud());
        p.setLongitud(d.longitud());
        p.setEsHitoTuristico(d.esHitoTuristico());
        p.setImagenUrl(d.imagenUrl());
    }
}
