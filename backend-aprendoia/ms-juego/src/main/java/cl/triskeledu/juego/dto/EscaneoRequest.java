package cl.triskeledu.juego.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** El jugador escaneó un QR (pantalla 7). */
public record EscaneoRequest(
        @NotNull(message = "Falta el jugador") Long usuarioId,
        @NotBlank(message = "Falta el código QR") String codigoQr
) {
}
