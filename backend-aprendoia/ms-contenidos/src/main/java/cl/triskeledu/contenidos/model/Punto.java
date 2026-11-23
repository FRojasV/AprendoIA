package cl.triskeledu.contenidos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/** Tabla "puntos": lugares de Santiago donde hay un código QR. */
@Entity
@Table(name = "puntos")
public class Punto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String nombre;

    @Column(nullable = false, length = 200)
    private String descripcion;

    @Column(name = "info_turistica", length = 500)
    private String infoTuristica;

    @Column(nullable = false)
    private Double latitud;

    @Column(nullable = false)
    private Double longitud;

    /** Si es hito turístico, la selfie entrega +100 puntos. */
    @Column(name = "es_hito_turistico", nullable = false)
    private boolean esHitoTuristico;

    @Column(name = "imagen_url", length = 300)
    private String imagenUrl;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    public Punto() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getInfoTuristica() { return infoTuristica; }
    public void setInfoTuristica(String infoTuristica) { this.infoTuristica = infoTuristica; }
    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
    public boolean isEsHitoTuristico() { return esHitoTuristico; }
    public void setEsHitoTuristico(boolean esHitoTuristico) { this.esHitoTuristico = esHitoTuristico; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
