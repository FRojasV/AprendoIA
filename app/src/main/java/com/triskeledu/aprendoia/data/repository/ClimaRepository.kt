package com.triskeledu.aprendoia.data.repository

import com.triskeledu.aprendoia.data.remote.ErrorServidor
import com.triskeledu.aprendoia.data.remote.api.ClimaApi
import kotlin.math.roundToInt

/** Clima listo para mostrar en pantalla. */
data class Clima(
    val temperatura: Int,
    val sensacionTermica: Int,
    val humedad: Int,
    val vientoKmH: Int,
    val descripcion: String,
    val emoji: String,
    val esDeDia: Boolean
) {
    /** Consejo para el recorrido según el clima. */
    val consejo: String
        get() = when {
            emoji == "🌧️" || emoji == "⛈️" || emoji == "🌦️" -> "Lleva paraguas para tu recorrido."
            temperatura >= 28 -> "Hace calor: lleva agua y bloqueador."
            temperatura <= 8 -> "Hace frío: abrígate antes de salir."
            else -> "Buen momento para buscar códigos QR."
        }
}

/**
 * API EXTERNA (Open-Meteo) consumida con Retrofit.
 * Es independiente: no guarda nada en Room ni usa los microservicios propios,
 * así un fallo del clima nunca afecta al juego.
 */
class ClimaRepository(private val api: ClimaApi) {

    suspend fun climaActual(latitud: Double, longitud: Double): Result<Clima> = runCatching {
        val actual = api.climaActual(latitud, longitud).current
            ?: throw ErrorServidor("El servicio de clima no envió datos")
        val codigo = actual.codigoClima ?: 0
        val (descripcion, emoji) = describir(codigo, actual.esDeDia != 0)
        Clima(
            temperatura = (actual.temperatura ?: 0.0).roundToInt(),
            sensacionTermica = (actual.sensacionTermica ?: actual.temperatura ?: 0.0).roundToInt(),
            humedad = actual.humedad ?: 0,
            vientoKmH = (actual.viento ?: 0.0).roundToInt(),
            descripcion = descripcion,
            emoji = emoji,
            esDeDia = actual.esDeDia != 0
        )
    }

    companion object {
        /**
         * Traduce el código meteorológico WMO que entrega Open-Meteo.
         * Tabla: https://open-meteo.com/en/docs (sección "WMO Weather interpretation codes").
         */
        fun describir(codigo: Int, esDeDia: Boolean = true): Pair<String, String> = when (codigo) {
            0 -> "Despejado" to (if (esDeDia) "☀️" else "🌙")
            1, 2 -> "Parcialmente nublado" to "⛅"
            3 -> "Nublado" to "☁️"
            45, 48 -> "Neblina" to "🌫️"
            51, 53, 55, 56, 57 -> "Llovizna" to "🌦️"
            61, 63, 65, 66, 67, 80, 81, 82 -> "Lluvia" to "🌧️"
            71, 73, 75, 77, 85, 86 -> "Nieve" to "❄️"
            95, 96, 99 -> "Tormenta" to "⛈️"
            else -> "Sin información" to "🌡️"
        }
    }
}
