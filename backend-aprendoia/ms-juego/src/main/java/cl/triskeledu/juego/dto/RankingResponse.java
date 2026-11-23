package cl.triskeledu.juego.dto;

/** Posición en el ranking de jugadores. */
public record RankingResponse(int posicion, Long usuarioId, long puntaje) {
}
