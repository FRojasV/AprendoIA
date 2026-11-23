package cl.triskeledu.juego.cliente;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Datos de una cápsula tal como los entrega ms-contenidos. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CapsulaRemota(
        Long id,
        Long puntoId,
        String puntoNombre,
        boolean puntoEsHito,
        String titulo,
        Integer puntos,
        boolean publicado
) {
}
