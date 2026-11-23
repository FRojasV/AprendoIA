package cl.triskeledu.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Inicio de sesión desde la pantalla 3: el jugador elegido + su contraseña. */
public record LoginRequest(
        @NotNull(message = "Falta el usuario") Long usuarioId,
        @NotBlank(message = "Ingresa la contraseña") String password
) {
}
