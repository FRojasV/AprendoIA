package cl.triskeledu.juego.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

/**
 * Tabla "descubrimientos": un jugador escaneó el QR de una cápsula.
 * UNIQUE(usuario_id, capsula_id) impide sumar puntos dos veces por el mismo QR.
 */
@Entity
@Table(name = "descubrimientos",
        uniqueConstraints = @UniqueConstraint(name = "uk_desc_usuario_capsula", columnNames = {"usuario_id", "capsula_id"}))
public class Descubrimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "capsula_id", nullable = false)
    private Long capsulaId;

    /** Se guarda para calcular "lugares visitados" sin consultar a ms-contenidos. */
    @Column(name = "punto_id", nullable = false)
    private Long puntoId;

    @Column(nullable = false)
    private Integer puntos;

    @Column(nullable = false)
    private boolean visto;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(name = "fecha_visto")
    private LocalDateTime fechaVisto;

    public Descubrimiento() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Long getCapsulaId() { return capsulaId; }
    public void setCapsulaId(Long capsulaId) { this.capsulaId = capsulaId; }
    public Long getPuntoId() { return puntoId; }
    public void setPuntoId(Long puntoId) { this.puntoId = puntoId; }
    public Integer getPuntos() { return puntos; }
    public void setPuntos(Integer puntos) { this.puntos = puntos; }
    public boolean isVisto() { return visto; }
    public void setVisto(boolean visto) { this.visto = visto; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public LocalDateTime getFechaVisto() { return fechaVisto; }
    public void setFechaVisto(LocalDateTime fechaVisto) { this.fechaVisto = fechaVisto; }
}
