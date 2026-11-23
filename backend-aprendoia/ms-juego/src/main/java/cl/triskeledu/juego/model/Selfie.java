package cl.triskeledu.juego.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

/** Tabla "selfies": +100 puntos, una por jugador y por hito turístico. */
@Entity
@Table(name = "selfies",
        uniqueConstraints = @UniqueConstraint(name = "uk_selfie_usuario_punto", columnNames = {"usuario_id", "punto_id"}))
public class Selfie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "punto_id", nullable = false)
    private Long puntoId;

    /** Ruta (Uri) de la foto en la galería del teléfono. */
    @Column(name = "ruta_foto", nullable = false, length = 300)
    private String rutaFoto;

    @Column(nullable = false)
    private Integer puntos;

    @Column(nullable = false)
    private LocalDateTime fecha;

    public Selfie() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getPuntoId() { return puntoId; }
    public void setPuntoId(Long puntoId) { this.puntoId = puntoId; }
    public String getRutaFoto() { return rutaFoto; }
    public void setRutaFoto(String rutaFoto) { this.rutaFoto = rutaFoto; }
    public Integer getPuntos() { return puntos; }
    public void setPuntos(Integer puntos) { this.puntos = puntos; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
