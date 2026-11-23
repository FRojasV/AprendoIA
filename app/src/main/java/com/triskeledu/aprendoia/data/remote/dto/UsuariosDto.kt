package com.triskeledu.aprendoia.data.remote.dto

/**
 * DTO = "Data Transfer Object": la forma del JSON que viaja entre la app y
 * el microservicio ms-usuarios. Gson convierte el JSON a estas clases.
 */
data class UsuarioDto(
    val id: Long,
    val nombre: String,
    val alias: String,
    val correo: String,
    val telefono: String?,
    val direccion: String?,
    val avatar: String?,
    val rol: String,
    val notificacionesActivas: Boolean
)

/** Cuerpo para crear o actualizar (password vacía al actualizar = no cambiarla). */
data class UsuarioRequestDto(
    val nombre: String,
    val correo: String,
    val telefono: String,
    val password: String,
    val direccion: String,
    val avatar: String,
    val notificacionesActivas: Boolean
)

data class LoginRequestDto(val usuarioId: Long, val password: String)
