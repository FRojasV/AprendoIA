package cl.triskeledu.juego.dto;

import cl.triskeledu.juego.model.Actividad;

import java.time.LocalDateTime;

public record ActividadResponse(Long id, Long usuarioId, String tipo, String titulo, String detalle,
                                Integer puntos, LocalDateTime fecha) {
    public static ActividadResponse desde(Actividad a) {
        return new ActividadResponse(a.getId(), a.getUsuarioId(), a.getTipo().name(), a.getTitulo(),
                a.getDetalle(), a.getPuntos(), a.getFecha());
    }
}
