package cl.triskeledu.usuarios.dto;

import cl.triskeledu.usuarios.model.Usuario;

import java.time.LocalDateTime;

/** Lo que se devuelve a la app. Nunca incluye el hash de la contraseña. */
public record UsuarioResponse(
        Long id,
        String nombre,
        String alias,
        String correo,
        String telefono,
        String direccion,
        String avatar,
        String rol,
        boolean notificacionesActivas,
        LocalDateTime fechaRegistro
) {
    public static UsuarioResponse desde(Usuario u) {
        return new UsuarioResponse(
                u.getId(), u.getNombre(), u.getAlias(), u.getCorreo(), u.getTelefono(),
                u.getDireccion(), u.getAvatar(), u.getRol().name(), u.isNotificacionesActivas(),
                u.getFechaRegistro()
        );
    }
}
