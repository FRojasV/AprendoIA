package com.triskeledu.aprendoia.ui.perfil

import com.triskeledu.aprendoia.data.local.entity.UsuarioEntity
import com.triskeledu.aprendoia.data.local.relacion.EstadisticasJugador
import com.triskeledu.aprendoia.data.local.relacion.puntajeTotal

/** MODELO: estado de la pantalla 15 (Perfil y Configuración). */
data class PerfilUiState(
    val cargando: Boolean = true,
    val usuario: UsuarioEntity? = null,
    val estadisticas: EstadisticasJugador = EstadisticasJugador(0, 0, 0, 0, 0, 0),

    // Diálogos
    val mostrarDialogoAvatar: Boolean = false,
    val avatarTemporal: String = "avatar_1",
    val mostrarDialogoPermisos: Boolean = false,
    val mostrarAyuda: Boolean = false,
    val mostrarConfirmarCierre: Boolean = false,

    val mensaje: String? = null,
    /** Evento: la sesión se cerró y hay que volver a la selección de jugador. */
    val sesionCerrada: Boolean = false
) {
    val puntaje: Int get() = estadisticas.puntajeTotal
}
