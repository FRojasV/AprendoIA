package cl.triskeledu.contenidos.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Datos para crear o editar una cápsula de IA. */
public record CapsulaRequest(
        @NotNull(message = "Elige el punto turístico")
        Long puntoId,

        @NotBlank(message = "El título es obligatorio")
        @Size(min = 3, max = 80, message = "El título debe tener entre 3 y 80 caracteres")
        String titulo,

        @NotBlank(message = "El tema es obligatorio")
        @Size(max = 60)
        String tema,

        @NotBlank(message = "El autor es obligatorio")
        @Size(max = 60)
        String autor,

        /* Opcional: si viene, debe ser un link web. */
        @Pattern(regexp = "^$|^https?://\\S+$", message = "Debe comenzar con https://")
        String url,

        @Pattern(regexp = "^$|^\\d{1,2}:[0-5]\\d$", message = "Usa el formato mm:ss")
        String duracion,

        @Size(max = 300, message = "Máximo 300 caracteres")
        String descripcion,

        @NotBlank(message = "El código QR es obligatorio")
        @Pattern(regexp = "^[A-Za-z0-9-]{4,30}$", message = "Entre 4 y 30 letras, números o guiones")
        String codigoQr,

        @NotNull(message = "Los puntos son obligatorios")
        @Min(value = 1, message = "Entre 1 y 100")
        @Max(value = 100, message = "Entre 1 y 100")
        Integer puntos,

        boolean publicado
) {
}
