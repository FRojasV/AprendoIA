package cl.triskeledu.juego.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Selfie en un hito turístico (pantalla 10). */
public record SelfieRequest(
        @NotNull(message = "Falta el jugador") Long usuarioId,
        @NotNull(message = "Falta el punto") Long puntoId,
        @NotBlank(message = "Falta la foto") @Size(max = 300) String rutaFoto
) {
}
