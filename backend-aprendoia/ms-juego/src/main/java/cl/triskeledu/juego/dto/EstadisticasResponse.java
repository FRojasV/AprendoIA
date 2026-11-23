package cl.triskeledu.juego.dto;

/** Resumen del jugador para Dashboard, Progreso y Perfil. */
public record EstadisticasResponse(
        Long usuarioId,
        long puntosQr,
        long puntosSelfies,
        long puntajeTotal,
        long qrEncontrados,
        long videosVistos,
        long selfies,
        long lugaresVisitados
) {
}
