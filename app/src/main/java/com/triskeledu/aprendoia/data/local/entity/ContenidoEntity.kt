package com.triskeledu.aprendoia.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabla "contenidos": cápsulas de IA (videos) asociadas a un punto.
 * Cada contenido tiene un código QR único que el cliente escanea.
 */
@Entity(
    tableName = "contenidos",
    foreignKeys = [
        ForeignKey(
            entity = PuntoEntity::class,
            parentColumns = ["id"],
            childColumns = ["puntoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("puntoId"), Index(value = ["codigoQr"], unique = true)]
)
data class ContenidoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val puntoId: Long,
    val titulo: String,
    val tema: String,
    val autor: String,
    val url: String,
    val duracion: String = "",
    val descripcion: String = "",
    /** Texto que identifica el QR físico, por ejemplo "QR-PLAZA-ARMAS". */
    val codigoQr: String,
    /** Puntos que entrega el QR (estándar = 10). */
    val puntos: Int = 10,
    /** El Creador sube el contenido; el Administrador lo publica. */
    val publicado: Boolean = true
)
