package cl.triskeledu.juego.dto;

import cl.triskeledu.juego.model.Selfie;

import java.time.LocalDateTime;

public record SelfieResponse(Long id, Long usuarioId, Long puntoId, String rutaFoto, Integer puntos, LocalDateTime fecha) {
    public static SelfieResponse desde(Selfie s) {
        return new SelfieResponse(s.getId(), s.getUsuarioId(), s.getPuntoId(), s.getRutaFoto(), s.getPuntos(), s.getFecha());
    }
}
