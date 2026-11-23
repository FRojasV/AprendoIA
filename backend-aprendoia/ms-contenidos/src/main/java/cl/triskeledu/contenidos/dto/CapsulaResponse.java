package cl.triskeledu.contenidos.dto;

import cl.triskeledu.contenidos.model.Capsula;

/** Cápsula con el nombre de su punto (lo usan la app y ms-juego). */
public record CapsulaResponse(
        Long id,
        Long puntoId,
        String puntoNombre,
        boolean puntoEsHito,
        String titulo,
        String tema,
        String autor,
        String url,
        String duracion,
        String descripcion,
        String codigoQr,
        Integer puntos,
        boolean publicado
) {
    public static CapsulaResponse desde(Capsula c, String puntoNombre, boolean puntoEsHito) {
        return new CapsulaResponse(c.getId(), c.getPuntoId(), puntoNombre, puntoEsHito, c.getTitulo(),
                c.getTema(), c.getAutor(), nulo(c.getUrl()), nulo(c.getDuracion()), nulo(c.getDescripcion()),
                c.getCodigoQr(), c.getPuntos(), c.isPublicado());
    }

    private static String nulo(String s) {
        return s == null ? "" : s;
    }
}
