package cl.triskeledu.juego.dto;

/** Resultado del escaneo (pantalla 8). nuevo=false → ya lo tenía y no suma puntos. */
public record EscaneoResponse(
        boolean nuevo,
        int puntosGanados,
        Long descubrimientoId,
        Long capsulaId,
        Long puntoId,
        String titulo,
        String puntoNombre,
        boolean puntoEsHito
) {
}
