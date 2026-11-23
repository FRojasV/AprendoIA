package cl.triskeledu.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos para crear o actualizar un jugador (pantalla 2 y edición).
 * En la actualización, "password" puede venir vacía: significa "no cambiar".
 */
public record UsuarioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 80, message = "El nombre debe tener entre 3 y 80 caracteres")
        String nombre,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no es válido")
        String correo,

        @Pattern(regexp = "^[+0-9 ]{8,16}$", message = "El teléfono debe tener entre 8 y 16 dígitos")
        String telefono,

        @Size(max = 64, message = "La contraseña es demasiado larga")
        String password,

        @Size(max = 150, message = "Máximo 150 caracteres")
        String direccion,

        String avatar,

        Boolean notificacionesActivas
) {
}
