package com.triskeledu.aprendoia.data.remote.api

import com.triskeledu.aprendoia.data.remote.dto.ClimaRespuestaDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API EXTERNA: Open-Meteo (https://api.open-meteo.com), gratuita y sin API key.
 * Ejemplo: /v1/forecast?latitude=-33.43&longitude=-70.65&current=temperature_2m,weather_code
 * Se consume con Retrofit y NO se guarda en Room ni pasa por los microservicios.
 */
interface ClimaApi {

    @GET("v1/forecast")
    suspend fun climaActual(
        @Query("latitude") latitud: Double,
        @Query("longitude") longitud: Double,
        @Query("current") campos: String =
            "temperature_2m,apparent_temperature,relative_humidity_2m,wind_speed_10m,weather_code,is_day",
        @Query("timezone") zonaHoraria: String = "America/Santiago"
    ): ClimaRespuestaDto
}
