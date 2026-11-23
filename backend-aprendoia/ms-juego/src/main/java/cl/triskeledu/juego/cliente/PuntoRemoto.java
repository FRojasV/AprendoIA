package cl.triskeledu.juego.cliente;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Datos de un punto tal como los entrega ms-contenidos. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PuntoRemoto(Long id, String nombre, boolean esHitoTuristico) {
}
