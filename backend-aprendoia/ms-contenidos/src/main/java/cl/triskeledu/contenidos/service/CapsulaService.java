package cl.triskeledu.contenidos.service;

import cl.triskeledu.contenidos.dto.CapsulaRequest;
import cl.triskeledu.contenidos.dto.CapsulaResponse;
import cl.triskeledu.contenidos.exception.ConflictoException;
import cl.triskeledu.contenidos.exception.RecursoNoEncontradoException;
import cl.triskeledu.contenidos.model.Capsula;
import cl.triskeledu.contenidos.model.Punto;
import cl.triskeledu.contenidos.repository.CapsulaRepository;
import cl.triskeledu.contenidos.repository.PuntoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** CRUD de cápsulas de IA, publicación y búsqueda por código QR. */
@Service
public class CapsulaService {

    private final CapsulaRepository capsulas;
    private final PuntoRepository puntos;

    public CapsulaService(CapsulaRepository capsulas, PuntoRepository puntos) {
        this.capsulas = capsulas;
        this.puntos = puntos;
    }

    /**
     * @param soloPublicadas true para la app de los jugadores; false para el panel de gestión.
     * @param puntoId        opcional: solo las cápsulas de ese punto.
     */
    @Transactional(readOnly = true)
    public List<CapsulaResponse> listar(boolean soloPublicadas, Long puntoId) {
        List<Capsula> lista;
        if (puntoId != null) {
            lista = capsulas.findByPuntoIdOrderByTituloAsc(puntoId).stream()
                    .filter(c -> !soloPublicadas || c.isPublicado()).toList();
        } else {
            lista = soloPublicadas ? capsulas.findByPublicadoTrueOrderByTituloAsc()
                    : capsulas.findAllByOrderByPublicadoAscTituloAsc();
        }
        Map<Long, Punto> mapaPuntos = puntos.findAll().stream()
                .collect(Collectors.toMap(Punto::getId, Function.identity()));
        return lista.stream().map(c -> respuesta(c, mapaPuntos.get(c.getPuntoId()))).toList();
    }

    @Transactional(readOnly = true)
    public CapsulaResponse obtener(Long id) {
        Capsula c = buscar(id);
        return respuesta(c, puntos.findById(c.getPuntoId()).orElse(null));
    }

    /** Lo usa ms-juego al escanear: solo devuelve cápsulas PUBLICADAS. */
    @Transactional(readOnly = true)
    public CapsulaResponse buscarPorCodigo(String codigo) {
        Capsula c = capsulas.findByCodigoQrIgnoreCase(codigo.trim())
                .filter(Capsula::isPublicado)
                .orElseThrow(() -> new RecursoNoEncontradoException("El código " + codigo + " no existe o no está publicado"));
        return respuesta(c, puntos.findById(c.getPuntoId()).orElse(null));
    }

    @Transactional
    public CapsulaResponse crear(CapsulaRequest datos) {
        Punto punto = validarPunto(datos.puntoId());
        String codigo = normalizar(datos.codigoQr());
        if (capsulas.existsByCodigoQrIgnoreCase(codigo)) {
            throw new ConflictoException("Ese código QR ya está en uso");
        }
        Capsula c = new Capsula();
        copiar(datos, c, codigo);
        c.setFechaCreacion(LocalDateTime.now());
        return respuesta(capsulas.save(c), punto);
    }

    @Transactional
    public CapsulaResponse actualizar(Long id, CapsulaRequest datos) {
        Capsula c = buscar(id);
        Punto punto = validarPunto(datos.puntoId());
        String codigo = normalizar(datos.codigoQr());
        if (capsulas.existsByCodigoQrIgnoreCaseAndIdNot(codigo, id)) {
            throw new ConflictoException("Ese código QR ya está en uso");
        }
        copiar(datos, c, codigo);
        return respuesta(capsulas.save(c), punto);
    }

    /** Solo el Administrador publica (la app controla el rol). */
    @Transactional
    public CapsulaResponse publicar(Long id) {
        Capsula c = buscar(id);
        c.setPublicado(true);
        return respuesta(capsulas.save(c), puntos.findById(c.getPuntoId()).orElse(null));
    }

    @Transactional
    public void eliminar(Long id) {
        capsulas.delete(buscar(id));
    }

    private Capsula buscar(Long id) {
        return capsulas.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la cápsula " + id));
    }

    private Punto validarPunto(Long puntoId) {
        return puntos.findById(puntoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el punto " + puntoId));
    }

    private void copiar(CapsulaRequest d, Capsula c, String codigo) {
        c.setPuntoId(d.puntoId());
        c.setTitulo(d.titulo().trim());
        c.setTema(d.tema().trim());
        c.setAutor(d.autor().trim());
        c.setUrl(d.url() == null ? "" : d.url().trim());
        c.setDuracion(d.duracion() == null ? "" : d.duracion());
        c.setDescripcion(d.descripcion() == null ? "" : d.descripcion().trim());
        c.setCodigoQr(codigo);
        c.setPuntos(d.puntos());
        c.setPublicado(d.publicado());
    }

    static String normalizar(String codigo) {
        return codigo.trim().toUpperCase(Locale.ROOT);
    }

    private CapsulaResponse respuesta(Capsula c, Punto punto) {
        return CapsulaResponse.desde(c,
                punto == null ? "" : punto.getNombre(),
                punto != null && punto.isEsHitoTuristico());
    }
}
