package com.triskeledu.aprendoia.ui.notificaciones

import com.triskeledu.aprendoia.data.local.entity.NotificacionEntity

/** Filtro de la bandeja. */
enum class FiltroNotificaciones(val etiqueta: String) { TODAS("Todas"), NO_LEIDAS("No leídas") }

/** MODELO: estado de la pantalla 14 (Notificaciones). */
data class NotificacionesUiState(
    val cargando: Boolean = true,
    val notificaciones: List<NotificacionEntity> = emptyList(),
    val filtro: FiltroNotificaciones = FiltroNotificaciones.TODAS,
    /** Evento: ruta a la que hay que navegar al tocar un aviso. */
    val irA: String? = null
) {
    val noLeidas: Int get() = notificaciones.count { !it.leida }

    val visibles: List<NotificacionEntity>
        get() = when (filtro) {
            FiltroNotificaciones.TODAS -> notificaciones
            FiltroNotificaciones.NO_LEIDAS -> notificaciones.filter { !it.leida }
        }
}
