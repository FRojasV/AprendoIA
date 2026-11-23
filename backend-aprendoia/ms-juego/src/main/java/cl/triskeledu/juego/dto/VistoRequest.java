package cl.triskeledu.juego.dto;

import jakarta.validation.constraints.NotNull;

/** El jugador vio la cápsula (pantalla 9). */
public record VistoRequest(
        @NotNull(message = "Falta el jugador") Long usuarioId,
        @NotNull(message = "Falta la cápsula") Long capsulaId
) {
}
