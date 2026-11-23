package com.triskeledu.aprendoia.data.remote.dto

/** JSON de ms-contenidos: puntos turísticos. */
data class PuntoDto(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val infoTuristica: String?,
    val latitud: Double,
    val longitud: Double,
    val esHitoTuristico: Boolean,
    val imagenUrl: String?
)

data class PuntoRequestDto(
    val nombre: String,
    val descripcion: String,
    val infoTuristica: String,
    val latitud: Double,
    val longitud: Double,
    val esHitoTuristico: Boolean,
    val imagenUrl: String?
)

/** JSON de ms-contenidos: cápsulas de IA. */
data class CapsulaDto(
    val id: Long,
    val puntoId: Long,
    val puntoNombre: String?,
    val titulo: String,
    val tema: String,
    val autor: String,
    val url: String?,
    val duracion: String?,
    val descripcion: String?,
    val codigoQr: String,
    val puntos: Int,
    val publicado: Boolean
)

data class CapsulaRequestDto(
    val puntoId: Long,
    val titulo: String,
    val tema: String,
    val autor: String,
    val url: String,
    val duracion: String,
    val descripcion: String,
    val codigoQr: String,
    val puntos: Int,
    val publicado: Boolean
)
