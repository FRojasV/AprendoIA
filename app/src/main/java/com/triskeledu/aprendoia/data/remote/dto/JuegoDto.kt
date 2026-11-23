package com.triskeledu.aprendoia.data.remote.dto

/** JSON de ms-juego. Las fechas llegan como texto ISO ("2026-11-20T10:15:00"). */
data class EscaneoRequestDto(val usuarioId: Long, val codigoQr: String)

data class EscaneoDto(
    val nuevo: Boolean,
    val puntosGanados: Int,
    val descubrimientoId: Long,
    val capsulaId: Long,
    val puntoId: Long,
    val titulo: String,
    val puntoNombre: String?,
    val puntoEsHito: Boolean
)

data class DescubrimientoDto(
    val id: Long,
    val usuarioId: Long,
    val capsulaId: Long,
    val puntoId: Long,
    val puntos: Int,
    val visto: Boolean,
    val fecha: String?,
    val fechaVisto: String?
)

data class VistoRequestDto(val usuarioId: Long, val capsulaId: Long)

data class SelfieRequestDto(val usuarioId: Long, val puntoId: Long, val rutaFoto: String)

data class SelfieDto(
    val id: Long,
    val usuarioId: Long,
    val puntoId: Long,
    val rutaFoto: String,
    val puntos: Int,
    val fecha: String?
)

data class ActividadDto(
    val id: Long,
    val usuarioId: Long,
    val tipo: String,
    val titulo: String,
    val detalle: String?,
    val puntos: Int,
    val fecha: String?
)

data class RankingDto(val posicion: Int, val usuarioId: Long, val puntaje: Long)
