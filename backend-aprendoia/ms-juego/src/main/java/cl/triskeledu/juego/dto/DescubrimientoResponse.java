package cl.triskeledu.juego.dto;

import cl.triskeledu.juego.model.Descubrimiento;

import java.time.LocalDateTime;

public record DescubrimientoResponse(
        Long id, Long usuarioId, Long capsulaId, Long puntoId, Integer puntos,
        boolean visto, LocalDateTime fecha, LocalDateTime fechaVisto
) {
    public static DescubrimientoResponse desde(Descubrimiento d) {
        return new DescubrimientoResponse(d.getId(), d.getUsuarioId(), d.getCapsulaId(), d.getPuntoId(),
                d.getPuntos(), d.isVisto(), d.getFecha(), d.getFechaVisto());
    }
}
