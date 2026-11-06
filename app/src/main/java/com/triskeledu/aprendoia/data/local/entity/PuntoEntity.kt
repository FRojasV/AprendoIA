package com.triskeledu.aprendoia.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabla "puntos": lugares georreferenciados de Santiago donde hay un QR.
 * Mientras no exista el backend, el mapa obtiene las coordenadas desde aquí.
 */
@Entity(tableName = "puntos")
data class PuntoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombre: String,
    val descripcion: String,
    val infoTuristica: String = "",
    val latitud: Double,
    val longitud: Double,
    /** Si es hito turístico, permite ganar +100 puntos con una selfie. */
    val esHitoTuristico: Boolean = false,
    val imagenUrl: String? = null,
    val fechaCreacion: Long = System.currentTimeMillis()
)
