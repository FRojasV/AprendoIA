package com.triskeledu.aprendoia.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabla "historial": registro cronológico de acciones del jugador
 * (QR escaneado, video visto, selfie, visita). Pantalla 13.
 */
@Entity(
    tableName = "historial",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("usuarioId")]
)
data class HistorialEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val tipo: TipoActividad,
    val titulo: String,
    val detalle: String = "",
    val puntos: Int = 0,
    val fecha: Long = System.currentTimeMillis()
)
