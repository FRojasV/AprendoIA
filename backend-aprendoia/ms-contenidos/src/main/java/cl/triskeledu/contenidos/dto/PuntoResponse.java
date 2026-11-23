package cl.triskeledu.contenidos.dto;

import cl.triskeledu.contenidos.model.Punto;

public record PuntoResponse(
        Long id,
        String nombre,
        String descripcion,
        String infoTuristica,
        Double latitud,
        Double longitud,
        boolean esHitoTuristico,
        String imagenUrl
) {
    public static PuntoResponse desde(Punto p) {
        return new PuntoResponse(p.getId(), p.getNombre(), p.getDescripcion(),
                p.getInfoTuristica() == null ? "" : p.getInfoTuristica(),
                p.getLatitud(), p.getLongitud(), p.isEsHitoTuristico(), p.getImagenUrl());
    }
}
