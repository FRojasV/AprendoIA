package com.triskeledu.aprendoia.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "descubrimientos",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ContenidoEntity::class,
            parentColumns = ["id"],
            childColumns = ["contenidoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["usuarioId", "contenidoId"], unique = true),
        Index("contenidoId")
    ]
)
data class DescubrimientoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val contenidoId: Long,
    val puntos: Int,
    val fecha: Long = System.currentTimeMillis(),
    /** false = solo descubierto, true = el video fue efectivamente visto. */
    val visto: Boolean = false,
    val fechaVisto: Long? = null
)
