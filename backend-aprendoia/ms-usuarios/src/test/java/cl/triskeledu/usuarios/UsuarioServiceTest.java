package cl.triskeledu.usuarios;

import cl.triskeledu.usuarios.dto.LoginRequest;
import cl.triskeledu.usuarios.dto.UsuarioRequest;
import cl.triskeledu.usuarios.dto.UsuarioResponse;
import cl.triskeledu.usuarios.exception.ConflictoException;
import cl.triskeledu.usuarios.exception.CredencialesInvalidasException;
import cl.triskeledu.usuarios.exception.RecursoNoEncontradoException;
import cl.triskeledu.usuarios.model.Rol;
import cl.triskeledu.usuarios.model.Usuario;
import cl.triskeledu.usuarios.repository.UsuarioRepository;
import cl.triskeledu.usuarios.service.Seguridad;
import cl.triskeledu.usuarios.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas unitarias de las reglas de negocio de usuarios (sin base de datos real). */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repositorio;

    private UsuarioService servicio;

    @BeforeEach
    void preparar() {
        servicio = new UsuarioService(repositorio);
    }

    private Usuario usuario(long id, Rol rol, String password) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setNombre("Ana González");
        u.setAlias("Ana");
        u.setCorreo("ana@email.com");
        u.setPasswordHash(Seguridad.hash(password));
        u.setAvatar("avatar_1");
        u.setRol(rol);
        u.setFechaRegistro(LocalDateTime.now());
        return u;
    }

    private UsuarioRequest solicitud(String password) {
        return new UsuarioRequest("Ana González", "ana@email.com", "+56 9 1234 5678", password, "", "avatar_2", true);
    }

    @Test
    void registrar_guardaClienteConHashYAlias() {
        when(repositorio.existsByCorreoIgnoreCase("ana@email.com")).thenReturn(false);
        when(repositorio.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(10L);
            return u;
        });

        UsuarioResponse r = servicio.registrar(solicitud("1234"));

        assertThat(r.id()).isEqualTo(10L);
        assertThat(r.alias()).isEqualTo("Ana");
        assertThat(r.rol()).isEqualTo("CLIENTE");
        assertThat(r.avatar()).isEqualTo("avatar_2");
    }

    @Test
    void registrar_conCorreoRepetido_lanzaConflicto() {
        when(repositorio.existsByCorreoIgnoreCase("ana@email.com")).thenReturn(true);

        assertThatThrownBy(() -> servicio.registrar(solicitud("1234")))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("correo");
        verify(repositorio, never()).save(any());
    }

    @Test
    void registrar_conPasswordCorta_lanzaConflicto() {
        assertThatThrownBy(() -> servicio.registrar(solicitud("12")))
                .isInstanceOf(ConflictoException.class);
    }

    @Test
    void login_correcto_devuelveUsuario() {
        when(repositorio.findById(3L)).thenReturn(Optional.of(usuario(3, Rol.CLIENTE, "1234")));

        UsuarioResponse r = servicio.login(new LoginRequest(3L, "1234"));

        assertThat(r.nombre()).isEqualTo("Ana González");
    }

    @Test
    void login_conPasswordIncorrecta_lanza401() {
        when(repositorio.findById(3L)).thenReturn(Optional.of(usuario(3, Rol.CLIENTE, "1234")));

        assertThatThrownBy(() -> servicio.login(new LoginRequest(3L, "9999")))
                .isInstanceOf(CredencialesInvalidasException.class);
    }

    @Test
    void actualizar_sinPassword_mantieneLaAnterior() {
        Usuario existente = usuario(3, Rol.CLIENTE, "1234");
        String hashAnterior = existente.getPasswordHash();
        when(repositorio.findById(3L)).thenReturn(Optional.of(existente));
        when(repositorio.existsByCorreoIgnoreCaseAndIdNot("ana@email.com", 3L)).thenReturn(false);
        when(repositorio.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        servicio.actualizar(3L, solicitud(""));

        assertThat(existente.getPasswordHash()).isEqualTo(hashAnterior);
    }

    @Test
    void eliminar_cuentaDelSistema_lanzaConflicto() {
        when(repositorio.findById(1L)).thenReturn(Optional.of(usuario(1, Rol.ADMIN, "123")));

        assertThatThrownBy(() -> servicio.eliminar(1L)).isInstanceOf(ConflictoException.class);
        verify(repositorio, never()).delete(any());
    }

    @Test
    void obtener_inexistente_lanza404() {
        when(repositorio.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.obtener(99L)).isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void listar_porRol_usaConsultaFiltrada() {
        when(repositorio.findByRolOrderByNombreAsc(Rol.CLIENTE)).thenReturn(List.of(usuario(3, Rol.CLIENTE, "1234")));

        assertThat(servicio.listar(Rol.CLIENTE)).hasSize(1);
    }
}
