package com.triskeledu.aprendoia.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Respuesta de la API externa Open-Meteo (https://open-meteo.com).
 * Solo se mapean los campos que usamos. @SerializedName traduce los
 * nombres con guion bajo del JSON a nombres Kotlin.
 */
data class ClimaRespuestaDto(
    val current: ClimaActualDto?
)

data class ClimaActualDto(
    val time: String?,
    @SerializedName("temperature_2m") val temperatura: Double?,
    @SerializedName("apparent_temperature") val sensacionTermica: Double?,
    @SerializedName("relative_humidity_2m") val humedad: Int?,
    @SerializedName("wind_speed_10m") val viento: Double?,
    @SerializedName("weather_code") val codigoClima: Int?,
    @SerializedName("is_day") val esDeDia: Int?
)
