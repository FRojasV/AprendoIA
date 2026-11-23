package cl.triskeledu.juego;

import cl.triskeledu.juego.cliente.CapsulaRemota;
import cl.triskeledu.juego.cliente.ContenidosClient;
import cl.triskeledu.juego.cliente.PuntoRemoto;
import cl.triskeledu.juego.dto.EscaneoRequest;
import cl.triskeledu.juego.dto.EscaneoResponse;
import cl.triskeledu.juego.dto.RankingResponse;
import cl.triskeledu.juego.dto.SelfieRequest;
import cl.triskeledu.juego.dto.VistoRequest;
import cl.triskeledu.juego.exception.ConflictoException;
import cl.triskeledu.juego.exception.RecursoNoEncontradoException;
import cl.triskeledu.juego.model.Actividad;
import cl.triskeledu.juego.model.Descubrimiento;
import cl.triskeledu.juego.model.Selfie;
import cl.triskeledu.juego.repository.ActividadRepository;
import cl.triskeledu.juego.repository.DescubrimientoRepository;
import cl.triskeledu.juego.repository.PuntajeUsuario;
import cl.triskeledu.juego.repository.SelfieRepository;
import cl.triskeledu.juego.service.JuegoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas de las reglas del juego (puntos por QR, duplicados, selfies y ranking). */
@ExtendWith(MockitoExtension.class)
class JuegoServiceTest {

    @Mock private DescubrimientoRepository descubrimientos;
    @Mock private SelfieRepository selfies;
    @Mock private ActividadRepository historial;
    @Mock private ContenidosClient contenidos;

    private JuegoService servicio;

    private final CapsulaRemota capsula =
            new CapsulaRemota(1L, 1L, "Plaza de Armas", true, "Introducción a la IA", 10, true);

    @BeforeEach
    void preparar() {
        servicio = new JuegoService(descubrimientos, selfies, historial, contenidos);
    }

    @Test
    void escanear_primeraVez_sumaPuntosYRegistraHistorial() {
        when(contenidos.capsulaPorCodigo("QR-PLAZA-ARMAS")).thenReturn(Optional.of(capsula));
        when(descubrimientos.findByUsuarioIdAndCapsulaId(3L, 1L)).thenReturn(Optional.empty());
        when(descubrimientos.save(any(Descubrimiento.class))).thenAnswer(inv -> {
            Descubrimiento d = inv.getArgument(0);
            d.setId(50L);
            return d;
        });

        EscaneoResponse r = servicio.registrarEscaneo(new EscaneoRequest(3L, " qr-plaza-armas "));

        assertThat(r.nuevo()).isTrue();
        assertThat(r.puntosGanados()).isEqualTo(10);
        assertThat(r.descubrimientoId()).isEqualTo(50L);
        verify(historial).save(any(Actividad.class));
    }

    @Test
    void escanear_repetido_noSumaPuntos() {
        Descubrimiento previo = new Descubrimiento();
        previo.setId(7L);
        when(contenidos.capsulaPorCodigo("QR-PLAZA-ARMAS")).thenReturn(Optional.of(capsula));
        when(descubrimientos.findByUsuarioIdAndCapsulaId(3L, 1L)).thenReturn(Optional.of(previo));

        EscaneoResponse r = servicio.registrarEscaneo(new EscaneoRequest(3L, "QR-PLAZA-ARMAS"));

        assertThat(r.nuevo()).isFalse();
        assertThat(r.puntosGanados()).isZero();
        verify(descubrimientos, never()).save(any());
    }

    @Test
    void escanear_codigoInexistente_lanza404() {
        when(contenidos.capsulaPorCodigo("QR-FALSO")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.registrarEscaneo(new EscaneoRequest(3L, "QR-FALSO")))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void selfie_enHitoConQrEscaneado_suma100() {
        when(contenidos.punto(1L)).thenReturn(Optional.of(new PuntoRemoto(1L, "Plaza de Armas", true)));
        when(descubrimientos.existsByUsuarioIdAndPuntoId(3L, 1L)).thenReturn(true);
        when(selfies.existsByUsuarioIdAndPuntoId(3L, 1L)).thenReturn(false);
        when(selfies.save(any(Selfie.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThat(servicio.registrarSelfie(new SelfieRequest(3L, 1L, "content://foto")).puntos())
                .isEqualTo(JuegoService.PUNTOS_SELFIE);
    }

    @Test
    void selfie_enPuntoQueNoEsHito_lanzaConflicto() {
        when(contenidos.punto(3L)).thenReturn(Optional.of(new PuntoRemoto(3L, "Parque Forestal", false)));

        assertThatThrownBy(() -> servicio.registrarSelfie(new SelfieRequest(3L, 3L, "content://foto")))
                .isInstanceOf(ConflictoException.class);
    }

    @Test
    void selfie_sinEscanearQr_lanzaConflicto() {
        when(contenidos.punto(1L)).thenReturn(Optional.of(new PuntoRemoto(1L, "Plaza de Armas", true)));
        when(descubrimientos.existsByUsuarioIdAndPuntoId(3L, 1L)).thenReturn(false);

        assertThatThrownBy(() -> servicio.registrarSelfie(new SelfieRequest(3L, 1L, "content://foto")))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("escanea");
    }

    @Test
    void marcarVisto_soloLaPrimeraVez() {
        Descubrimiento d = new Descubrimiento();
        d.setCapsulaId(1L);
        d.setVisto(false);
        when(descubrimientos.findByUsuarioIdAndCapsulaId(3L, 1L)).thenReturn(Optional.of(d));
        when(contenidos.capsula(1L)).thenReturn(Optional.of(capsula));

        assertThat(servicio.marcarVisto(new VistoRequest(3L, 1L))).isTrue();
        assertThat(servicio.marcarVisto(new VistoRequest(3L, 1L))).isFalse();
    }

    @Test
    void ranking_sumaQrYSelfiesYOrdena() {
        when(descubrimientos.puntosPorUsuario()).thenReturn(List.of(puntaje(3L, 20L), puntaje(4L, 30L)));
        when(selfies.puntosPorUsuario()).thenReturn(List.of(puntaje(3L, 100L)));

        List<RankingResponse> ranking = servicio.ranking(10);

        assertThat(ranking).hasSize(2);
        assertThat(ranking.get(0).usuarioId()).isEqualTo(3L);
        assertThat(ranking.get(0).puntaje()).isEqualTo(120L);
        assertThat(ranking.get(1).posicion()).isEqualTo(2);
    }

    private static PuntajeUsuario puntaje(Long usuarioId, Long puntos) {
        return new PuntajeUsuario() {
            @Override public Long getUsuarioId() { return usuarioId; }
            @Override public Long getPuntos() { return puntos; }
        };
    }
}
