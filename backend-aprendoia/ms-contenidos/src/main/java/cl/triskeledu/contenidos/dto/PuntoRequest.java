package cl.triskeledu.contenidos.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Datos para crear o editar un punto turístico (panel del Administrador). */
public record PuntoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 60, message = "El nombre debe tener entre 3 y 60 caracteres")
        String nombre,

        @NotBlank(message = "La descripción es obligatoria")
        @Size(min = 10, max = 200, message = "La descripción debe tener entre 10 y 200 caracteres")
        String descripcion,

        @Size(max = 500, message = "Máximo 500 caracteres")
        String infoTuristica,

        @NotNull(message = "La latitud es obligatoria")
        @DecimalMin(value = "-90.0", message = "Latitud entre -90 y 90")
        @DecimalMax(value = "90.0", message = "Latitud entre -90 y 90")
        Double latitud,

        @NotNull(message = "La longitud es obligatoria")
        @DecimalMin(value = "-180.0", message = "Longitud entre -180 y 180")
        @DecimalMax(value = "180.0", message = "Longitud entre -180 y 180")
        Double longitud,

        boolean esHitoTuristico,

        String imagenUrl
) {
}
