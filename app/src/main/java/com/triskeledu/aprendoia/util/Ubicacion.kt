package com.triskeledu.aprendoia.util

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import java.util.Locale

/** Coordenada simple, independiente de osmdroid (se usa en los UiState). */
data class Coordenada(val latitud: Double, val longitud: Double)

/** Plaza de Armas: centro inicial del mapa. */
val CENTRO_SANTIAGO = Coordenada(-33.4378, -70.6505)

object Ubicacion {

    /** Distancia en metros entre dos coordenadas. */
    fun distanciaMetros(desde: Coordenada, hasta: Coordenada): Float {
        val resultado = FloatArray(1)
        Location.distanceBetween(desde.latitud, desde.longitud, hasta.latitud, hasta.longitud, resultado)
        return resultado[0]
    }

    /** 350 → "350 m"  ·  1830 → "1,8 km". */
    fun formatearDistancia(metros: Float): String =
        if (metros < 1000) "${metros.toInt()} m"
        else String.format(Locale.forLanguageTag("es-CL"), "%.1f km", metros / 1000f)

    /**
     * Última posición conocida por el teléfono (sin esperar al GPS).
     * Devuelve null si no hay permiso o si el teléfono aún no tiene una lectura.
     */
    @SuppressLint("MissingPermission")
    fun ultimaConocida(context: Context): Coordenada? {
        if (!Permisos.tieneUbicacion(context)) return null
        val gestor = context.getSystemService(LocationManager::class.java) ?: return null
        return gestor.getProviders(true)
            .mapNotNull { proveedor -> runCatching { gestor.getLastKnownLocation(proveedor) }.getOrNull() }
            .maxByOrNull { it.time }
            ?.let { Coordenada(it.latitude, it.longitude) }
    }

    /**
     * "Cómo llegar": abre la app de mapas instalada (Google Maps, Waze, etc.).
     * Si no hay ninguna, abre la ruta en el navegador.
     */
    fun abrirComoLlegar(context: Context, destino: Coordenada, nombre: String) {
        val geo = Uri.parse(
            "geo:${destino.latitud},${destino.longitud}?q=${destino.latitud},${destino.longitud}(${Uri.encode(nombre)})"
        )
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, geo).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (e: ActivityNotFoundException) {
            val web = Uri.parse(
                "https://www.google.com/maps/dir/?api=1&destination=${destino.latitud},${destino.longitud}"
            )
            context.startActivity(Intent(Intent.ACTION_VIEW, web).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
}
