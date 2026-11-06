package com.triskeledu.aprendoia.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabla "notificaciones": bandeja de cada jugador (pantalla 14).
 * "destino" indica a qué pantalla ir al tocarla, por ejemplo "detalle_punto/3".
 */
@Entity(
    tableName = "notificaciones",
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
data class NotificacionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val titulo: String,
    val mensaje: String,
    val tipo: TipoNotificacion,
    val destino: String? = null,
    val leida: Boolean = false,
    val fecha: Long = System.currentTimeMillis()
)
