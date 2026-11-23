package cl.triskeledu.juego.service;

import cl.triskeledu.juego.cliente.CapsulaRemota;
import cl.triskeledu.juego.cliente.ContenidosClient;
import cl.triskeledu.juego.cliente.PuntoRemoto;
import cl.triskeledu.juego.dto.ActividadResponse;
import cl.triskeledu.juego.dto.DescubrimientoResponse;
import cl.triskeledu.juego.dto.EscaneoRequest;
import cl.triskeledu.juego.dto.EscaneoResponse;
import cl.triskeledu.juego.dto.EstadisticasResponse;
import cl.triskeledu.juego.dto.RankingResponse;
import cl.triskeledu.juego.dto.SelfieRequest;
import cl.triskeledu.juego.dto.SelfieResponse;
import cl.triskeledu.juego.dto.VistoRequest;
import cl.triskeledu.juego.exception.ConflictoException;
import cl.triskeledu.juego.exception.RecursoNoEncontradoException;
import cl.triskeledu.juego.model.Actividad;
import cl.triskeledu.juego.model.Descubrimiento;
import cl.triskeledu.juego.model.Selfie;
import cl.triskeledu.juego.model.TipoActividad;
import cl.triskeledu.juego.repository.ActividadRepository;
import cl.triskeledu.juego.repository.DescubrimientoRepository;
import cl.triskeledu.juego.repository.PuntajeUsuario;
import cl.triskeledu.juego.repository.SelfieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Reglas del juego del caso Triskeledu:
 *  - QR escaneado por primera vez: suma los puntos de la cápsula (10).
 *  - QR repetido: no suma.
 *  - Selfie en hito turístico, después de escanear su QR: +100, una sola vez.
 */
@Service
public class JuegoService {

    public static final int PUNTOS_SELFIE = 100;

    private final DescubrimientoRepository descubrimientos;
    private final SelfieRepository selfies;
    private final ActividadRepository historial;
    private final ContenidosClient contenidos;

    public JuegoService(DescubrimientoRepository descubrimientos, SelfieRepository selfies,
                        ActividadRepository historial, ContenidosClient contenidos) {
        this.descubrimientos = descubrimientos;
        this.selfies = selfies;
        this.historial = historial;
        this.contenidos = contenidos;
    }

    // ---------------------------------------------------------------- QR

    @Transactional
    public EscaneoResponse registrarEscaneo(EscaneoRequest datos) {
        String codigo = datos.codigoQr().trim().toUpperCase(Locale.ROOT);
        CapsulaRemota capsula = contenidos.capsulaPorCodigo(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException("El código " + codigo + " no existe o no está publicado"));

        Optional<Descubrimiento> existente = descubrimientos.findByUsuarioIdAndCapsulaId(datos.usuarioId(), capsula.id());
        if (existente.isPresent()) {
            return respuesta(false, 0, existente.get().getId(), capsula);
        }

        Descubrimiento d = new Descubrimiento();
        d.setUsuarioId(datos.usuarioId());
        d.setCapsulaId(capsula.id());
        d.setPuntoId(capsula.puntoId());
        d.setPuntos(capsula.puntos());
        d.setVisto(false);
        d.setFecha(LocalDateTime.now());
        Descubrimiento guardado = descubrimientos.save(d);

        historial.save(new Actividad(datos.usuarioId(), TipoActividad.QR, "Escaneaste un QR",
                capsula.titulo() + " · " + capsula.puntoNombre(), capsula.puntos()));
        return respuesta(true, capsula.puntos(), guardado.getId(), capsula);
    }

    @Transactional(readOnly = true)
    public List<DescubrimientoResponse> descubrimientos(Long usuarioId) {
        return descubrimientos.findByUsuarioIdOrderByFechaDesc(usuarioId).stream()
                .map(DescubrimientoResponse::desde).toList();
    }

    /** Marca la cápsula como vista. Devuelve false si ya estaba vista. */
    @Transactional
    public boolean marcarVisto(VistoRequest datos) {
        Descubrimiento d = descubrimientos.findByUsuarioIdAndCapsulaId(datos.usuarioId(), datos.capsulaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Primero escanea el QR de esta cápsula"));
        if (d.isVisto()) return false;
        d.setVisto(true);
        d.setFechaVisto(LocalDateTime.now());
        descubrimientos.save(d);
        String titulo = contenidos.capsula(d.getCapsulaId()).map(CapsulaRemota::titulo).orElse("Cápsula de IA");
        historial.save(new Actividad(datos.usuarioId(), TipoActividad.VIDEO, "Viste una cápsula de IA", titulo, 0));
        return true;
    }

    // ---------------------------------------------------------------- Selfies

    @Transactional
    public SelfieResponse registrarSelfie(SelfieRequest datos) {
        PuntoRemoto punto = contenidos.punto(datos.puntoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el punto " + datos.puntoId()));
        if (!punto.esHitoTuristico()) {
            throw new ConflictoException("La selfie solo suma puntos en hitos turísticos");
        }
        if (!descubrimientos.existsByUsuarioIdAndPuntoId(datos.usuarioId(), datos.puntoId())) {
            throw new ConflictoException("Primero escanea el código QR de este lugar");
        }
        if (selfies.existsByUsuarioIdAndPuntoId(datos.usuarioId(), datos.puntoId())) {
            throw new ConflictoException("Ya tienes tu selfie en este lugar");
        }
        Selfie s = new Selfie();
        s.setUsuarioId(datos.usuarioId());
        s.setPuntoId(datos.puntoId());
        s.setRutaFoto(datos.rutaFoto());
        s.setPuntos(PUNTOS_SELFIE);
        s.setFecha(LocalDateTime.now());
        Selfie guardada = selfies.save(s);
        historial.save(new Actividad(datos.usuarioId(), TipoActividad.SELFIE, "Selfie en hito turístico",
                punto.nombre(), PUNTOS_SELFIE));
        return SelfieResponse.desde(guardada);
    }

    @Transactional(readOnly = true)
    public List<SelfieResponse> selfies(Long usuarioId) {
        return selfies.findByUsuarioIdOrderByFechaDesc(usuarioId).stream().map(SelfieResponse::desde).toList();
    }

    @Transactional
    public void eliminarSelfie(Long id) {
        Selfie s = selfies.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la selfie " + id));
        selfies.delete(s);
    }

    // ---------------------------------------------------------------- Historial y puntajes

    @Transactional(readOnly = true)
    public List<ActividadResponse> historial(Long usuarioId) {
        return historial.findByUsuarioIdOrderByFechaDesc(usuarioId).stream().map(ActividadResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public EstadisticasResponse estadisticas(Long usuarioId) {
        long puntosQr = descubrimientos.sumarPuntos(usuarioId);
        long puntosSelfies = selfies.sumarPuntos(usuarioId);
        return new EstadisticasResponse(
                usuarioId, puntosQr, puntosSelfies, puntosQr + puntosSelfies,
                descubrimientos.countByUsuarioId(usuarioId),
                descubrimientos.countByUsuarioIdAndVistoTrue(usuarioId),
                selfies.countByUsuarioId(usuarioId),
                descubrimientos.contarLugares(usuarioId)
        );
    }

    /** Ranking de jugadores por puntaje total (QR + selfies), de mayor a menor. */
    @Transactional(readOnly = true)
    public List<RankingResponse> ranking(int limite) {
        Map<Long, Long> totales = new HashMap<>();
        sumar(totales, descubrimientos.puntosPorUsuario());
        sumar(totales, selfies.puntosPorUsuario());

        List<Map.Entry<Long, Long>> ordenado = new ArrayList<>(totales.entrySet());
        ordenado.sort(Map.Entry.<Long, Long>comparingByValue().reversed());

        List<RankingResponse> resultado = new ArrayList<>();
        for (int i = 0; i < ordenado.size() && i < limite; i++) {
            resultado.add(new RankingResponse(i + 1, ordenado.get(i).getKey(), ordenado.get(i).getValue()));
        }
        return resultado;
    }

    /** Borra todo el progreso de un jugador (cuando el Administrador lo elimina). */
    @Transactional
    public void eliminarProgreso(Long usuarioId) {
        descubrimientos.deleteByUsuarioId(usuarioId);
        selfies.deleteByUsuarioId(usuarioId);
        historial.deleteByUsuarioId(usuarioId);
    }

    private static void sumar(Map<Long, Long> totales, List<PuntajeUsuario> parciales) {
        parciales.forEach(p -> totales.merge(p.getUsuarioId(), p.getPuntos() == null ? 0L : p.getPuntos(), Long::sum));
    }

    private static EscaneoResponse respuesta(boolean nuevo, int puntos, Long descubrimientoId, CapsulaRemota c) {
        return new EscaneoResponse(nuevo, puntos, descubrimientoId, c.id(), c.puntoId(), c.titulo(),
                c.puntoNombre(), c.puntoEsHito());
    }
}
