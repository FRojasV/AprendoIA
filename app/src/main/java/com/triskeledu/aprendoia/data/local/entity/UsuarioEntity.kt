package com.triskeledu.aprendoia.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabla "usuarios": guarda Administrador, Creador de Contenido y Clientes.
 * El correo es único para no registrar dos veces al mismo jugador.
 */
@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["correo"], unique = true)]
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val alias: String,
    val correo: String,
    val telefono: String = "",
    /** Se guarda el hash SHA-256, nunca la contraseña en texto plano. */
    val password: String,
    val direccion: String = "",
    /** Clave del avatar elegido, por ejemplo "avatar_1". */
    val avatar: String = "avatar_1",
    val rol: Rol = Rol.CLIENTE,
    val notificacionesActivas: Boolean = true,
    val fechaRegistro: Long = System.currentTimeMillis()
)
