package com.triskeledu.aprendoia.ui.dashboard

import com.triskeledu.aprendoia.data.local.entity.PuntoEntity
import com.triskeledu.aprendoia.data.local.entity.UsuarioEntity
import com.triskeledu.aprendoia.data.local.relacion.EstadisticasJugador
import com.triskeledu.aprendoia.data.local.relacion.puntajeTotal
import com.triskeledu.aprendoia.ui.components.EstadoClima

/** MODELO: todo lo que muestra la pantalla 4 (Dashboard). */
data class DashboardUiState(
    val cargando: Boolean = true,
    val sinSesion: Boolean = false,
    val usuario: UsuarioEntity? = null,
    val estadisticas: EstadisticasJugador = EstadisticasJugador(0, 0, 0, 0, 0, 0),
    val totalLugares: Int = 0,
    /** Primer hito turístico pendiente de selfie (null = todos completados). */
    val desafio: PuntoEntity? = null,
    val notificacionesNoLeidas: Int = 0,
    /** API externa: clima actual en Santiago. */
    val clima: EstadoClima = EstadoClima(),
    /** Mensaje si los microservicios no responden (null = conectado). */
    val avisoConexion: String? = null
) {
    val puntaje: Int get() = estadisticas.puntajeTotal

    /** Avance de lugares descubiertos entre 0 y 1. */
    val progresoLugares: Float
        get() = if (totalLugares == 0) 0f
        else (estadisticas.lugaresVisitados.toFloat() / totalLugares).coerceIn(0f, 1f)
}
