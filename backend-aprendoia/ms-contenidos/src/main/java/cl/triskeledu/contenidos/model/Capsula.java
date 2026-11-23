package cl.triskeledu.contenidos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Tabla "capsulas": videos cortos de IA asociados a un punto.
 * La clave foránea punto_id tiene ON DELETE CASCADE (schema.sql): al borrar un
 * punto se borran sus cápsulas.
 */
@Entity
@Table(name = "capsulas")
public class Capsula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "punto_id", nullable = false)
    private Long puntoId;

    @Column(nullable = false, length = 80)
    private String titulo;

    @Column(nullable = false, length = 60)
    private String tema;

    @Column(nullable = false, length = 60)
    private String autor;

    @Column(length = 300)
    private String url;

    @Column(length = 5)
    private String duracion;

    @Column(length = 300)
    private String descripcion;

    /** Texto que va dentro del QR, por ejemplo "QR-PLAZA-ARMAS". Único. */
    @Column(name = "codigo_qr", nullable = false, unique = true, length = 30)
    private String codigoQr;

    @Column(nullable = false)
    private Integer puntos;

    /** El Creador sube la cápsula (false) y el Administrador la publica (true). */
    @Column(nullable = false)
    private boolean publicado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    public Capsula() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPuntoId() { return puntoId; }
    public void setPuntoId(Long puntoId) { this.puntoId = puntoId; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getTema() { return tema; }
    public void setTema(String tema) { this.tema = tema; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getCodigoQr() { return codigoQr; }
    public void setCodigoQr(String codigoQr) { this.codigoQr = codigoQr; }
    public Integer getPuntos() { return puntos; }
    public void setPuntos(Integer puntos) { this.puntos = puntos; }
    public boolean isPublicado() { return publicado; }
    public void setPublicado(boolean publicado) { this.publicado = publicado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
