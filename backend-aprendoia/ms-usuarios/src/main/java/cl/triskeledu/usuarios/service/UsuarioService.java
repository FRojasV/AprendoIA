package cl.triskeledu.usuarios.service;

import cl.triskeledu.usuarios.dto.LoginRequest;
import cl.triskeledu.usuarios.dto.UsuarioRequest;
import cl.triskeledu.usuarios.dto.UsuarioResponse;
import cl.triskeledu.usuarios.exception.ConflictoException;
import cl.triskeledu.usuarios.exception.CredencialesInvalidasException;
import cl.triskeledu.usuarios.exception.RecursoNoEncontradoException;
import cl.triskeledu.usuarios.model.Rol;
import cl.triskeledu.usuarios.model.Usuario;
import cl.triskeledu.usuarios.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** Reglas de negocio de los usuarios. El controlador solo recibe y responde HTTP. */
@Service
public class UsuarioService {

    private static final String AVATAR_POR_DEFECTO = "avatar_1";

    private final UsuarioRepository repositorio;

    public UsuarioService(UsuarioRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar(Rol rol) {
        List<Usuario> usuarios = (rol == null)
                ? repositorio.findAllByOrderByRolAscNombreAsc()
                : repositorio.findByRolOrderByNombreAsc(rol);
        return usuarios.stream().map(UsuarioResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id) {
        return UsuarioResponse.desde(buscar(id));
    }

    /** Registro de un nuevo jugador (siempre con rol CLIENTE). */
    @Transactional
    public UsuarioResponse registrar(UsuarioRequest datos) {
        if (datos.password() == null || datos.password().length() < 4) {
            throw new ConflictoException("La contraseña debe tener al menos 4 caracteres");
        }
        String correo = datos.correo().trim();
        if (repositorio.existsByCorreoIgnoreCase(correo)) {
            throw new ConflictoException("Ya existe un jugador con ese correo");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(datos.nombre().trim());
        usuario.setAlias(alias(datos.nombre()));
        usuario.setCorreo(correo);
        usuario.setTelefono(limpiar(datos.telefono()));
        usuario.setPasswordHash(Seguridad.hash(datos.password()));
        usuario.setDireccion(limpiar(datos.direccion()));
        usuario.setAvatar(datos.avatar() == null || datos.avatar().isBlank() ? AVATAR_POR_DEFECTO : datos.avatar());
        usuario.setRol(Rol.CLIENTE);
        usuario.setNotificacionesActivas(datos.notificacionesActivas() == null || datos.notificacionesActivas());
        usuario.setFechaRegistro(LocalDateTime.now());
        return UsuarioResponse.desde(repositorio.save(usuario));
    }

    /** Actualiza el perfil. Si la contraseña viene vacía se mantiene la actual. */
    @Transactional
    public UsuarioResponse actualizar(Long id, UsuarioRequest datos) {
        Usuario usuario = buscar(id);
        String correo = datos.correo().trim();
        if (repositorio.existsByCorreoIgnoreCaseAndIdNot(correo, id)) {
            throw new ConflictoException("Ese correo ya está en uso");
        }
        usuario.setNombre(datos.nombre().trim());
        // Las cuentas del sistema conservan su alias (Admin, GenCont).
        if (usuario.getRol() == Rol.CLIENTE) usuario.setAlias(alias(datos.nombre()));
        usuario.setCorreo(correo);
        usuario.setTelefono(limpiar(datos.telefono()));
        usuario.setDireccion(limpiar(datos.direccion()));
        if (datos.avatar() != null && !datos.avatar().isBlank()) usuario.setAvatar(datos.avatar());
        if (datos.notificacionesActivas() != null) usuario.setNotificacionesActivas(datos.notificacionesActivas());
        if (datos.password() != null && !datos.password().isBlank()) {
            if (datos.password().length() < 4) {
                throw new ConflictoException("La contraseña debe tener al menos 4 caracteres");
            }
            usuario.setPasswordHash(Seguridad.hash(datos.password()));
        }
        return UsuarioResponse.desde(repositorio.save(usuario));
    }

    /** Solo se pueden eliminar jugadores; las cuentas en duro están protegidas. */
    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = buscar(id);
        if (usuario.getRol() != Rol.CLIENTE) {
            throw new ConflictoException("No se puede eliminar una cuenta del sistema");
        }
        repositorio.delete(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse login(LoginRequest datos) {
        Usuario usuario = repositorio.findById(datos.usuarioId())
                .orElseThrow(() -> new CredencialesInvalidasException("Usuario o contraseña incorrectos"));
        if (!Seguridad.coincide(datos.password(), usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException("Contraseña incorrecta");
        }
        return UsuarioResponse.desde(usuario);
    }

    private Usuario buscar(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario " + id));
    }

    /** "Ana González" → "Ana". */
    static String alias(String nombre) {
        String limpio = nombre.trim();
        int espacio = limpio.indexOf(' ');
        return espacio > 0 ? limpio.substring(0, espacio) : limpio;
    }

    private static String limpiar(String texto) {
        return texto == null ? "" : texto.trim();
    }
}
