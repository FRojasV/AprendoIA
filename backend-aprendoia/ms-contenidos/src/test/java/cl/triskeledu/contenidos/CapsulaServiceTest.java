package cl.triskeledu.contenidos;

import cl.triskeledu.contenidos.dto.CapsulaRequest;
import cl.triskeledu.contenidos.dto.CapsulaResponse;
import cl.triskeledu.contenidos.exception.ConflictoException;
import cl.triskeledu.contenidos.exception.RecursoNoEncontradoException;
import cl.triskeledu.contenidos.model.Capsula;
import cl.triskeledu.contenidos.model.Punto;
import cl.triskeledu.contenidos.repository.CapsulaRepository;
import cl.triskeledu.contenidos.repository.PuntoRepository;
import cl.triskeledu.contenidos.service.CapsulaService;
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

/** Pruebas unitarias de las reglas de cápsulas: código único, publicación y búsqueda por QR. */
@ExtendWith(MockitoExtension.class)
class CapsulaServiceTest {

    @Mock
    private CapsulaRepository capsulas;
    @Mock
    private PuntoRepository puntos;

    private CapsulaService servicio;

    @BeforeEach
    void preparar() {
        servicio = new CapsulaService(capsulas, puntos);
    }

    private Punto plaza() {
        Punto p = new Punto();
        p.setId(1L);
        p.setNombre("Plaza de Armas");
        p.setEsHitoTuristico(true);
        return p;
    }

    private Capsula capsula(boolean publicada) {
        Capsula c = new Capsula();
        c.setId(5L);
        c.setPuntoId(1L);
        c.setTitulo("Redes Neuronales");
        c.setTema("Deep Learning");
        c.setAutor("Triskeledu");
        c.setCodigoQr("QR-PLAZA-ARMAS");
        c.setPuntos(10);
        c.setPublicado(publicada);
        return c;
    }

    private CapsulaRequest solicitud(String codigo) {
        return new CapsulaRequest(1L, "Redes Neuronales", "Deep Learning", "Triskeledu",
                "", "2:20", "", codigo, 10, true);
    }

    @Test
    void crear_normalizaElCodigoYDevuelveElPunto() {
        when(puntos.findById(1L)).thenReturn(Optional.of(plaza()));
        when(capsulas.existsByCodigoQrIgnoreCase("QR-NUEVO")).thenReturn(false);
        when(capsulas.save(any(Capsula.class))).thenAnswer(inv -> inv.getArgument(0));

        CapsulaResponse r = servicio.crear(solicitud("qr-nuevo"));

        assertThat(r.codigoQr()).isEqualTo("QR-NUEVO");
        assertThat(r.puntoNombre()).isEqualTo("Plaza de Armas");
        assertThat(r.puntoEsHito()).isTrue();
    }

    @Test
    void crear_conCodigoRepetido_lanzaConflicto() {
        when(puntos.findById(1L)).thenReturn(Optional.of(plaza()));
        when(capsulas.existsByCodigoQrIgnoreCase("QR-PLAZA-ARMAS")).thenReturn(true);

        assertThatThrownBy(() -> servicio.crear(solicitud("QR-PLAZA-ARMAS")))
                .isInstanceOf(ConflictoException.class);
        verify(capsulas, never()).save(any());
    }

    @Test
    void crear_conPuntoInexistente_lanza404() {
        when(puntos.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.crear(solicitud("QR-NUEVO")))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void buscarPorCodigo_noPublicada_lanza404() {
        when(capsulas.findByCodigoQrIgnoreCase("QR-PLAZA-ARMAS")).thenReturn(Optional.of(capsula(false)));

        assertThatThrownBy(() -> servicio.buscarPorCodigo("QR-PLAZA-ARMAS"))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void buscarPorCodigo_publicada_laDevuelve() {
        when(capsulas.findByCodigoQrIgnoreCase("QR-PLAZA-ARMAS")).thenReturn(Optional.of(capsula(true)));
        when(puntos.findById(1L)).thenReturn(Optional.of(plaza()));

        assertThat(servicio.buscarPorCodigo("QR-PLAZA-ARMAS").titulo()).isEqualTo("Redes Neuronales");
    }

    @Test
    void publicar_marcaComoPublicada() {
        Capsula pendiente = capsula(false);
        when(capsulas.findById(5L)).thenReturn(Optional.of(pendiente));
        when(capsulas.save(any(Capsula.class))).thenAnswer(inv -> inv.getArgument(0));
        when(puntos.findById(1L)).thenReturn(Optional.of(plaza()));

        assertThat(servicio.publicar(5L).publicado()).isTrue();
    }

    @Test
    void listar_soloPublicadas_usaConsultaDePublicadas() {
        when(capsulas.findByPublicadoTrueOrderByTituloAsc()).thenReturn(List.of(capsula(true)));
        when(puntos.findAll()).thenReturn(List.of(plaza()));

        assertThat(servicio.listar(true, null)).hasSize(1);
    }
}
