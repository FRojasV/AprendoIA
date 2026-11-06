package com.triskeledu.aprendoia.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabla "selfies": bonificación de +100 puntos en hitos turísticos.
 * Solo una selfie con puntaje por jugador y por punto.
 */
@Entity(
    tableName = "selfies",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PuntoEntity::class,
            parentColumns = ["id"],
            childColumns = ["puntoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["usuarioId", "puntoId"], unique = true),
        Index("puntoId")
    ]
)
data class SelfieEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val puntoId: Long,
    /** Uri (content://) de la foto guardada en la galería. */
    val rutaFoto: String,
    val puntos: Int = 100,
    val fecha: Long = System.currentTimeMillis()
)
