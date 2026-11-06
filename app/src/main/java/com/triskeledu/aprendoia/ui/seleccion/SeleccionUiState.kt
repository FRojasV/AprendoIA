package com.triskeledu.aprendoia.ui.seleccion

import com.triskeledu.aprendoia.data.local.entity.Rol
import com.triskeledu.aprendoia.data.local.relacion.JugadorResumen

/**
 * MODELO: estado de la pantalla 3 (Selección de Jugador).
 */
data class SeleccionUiState(
    val cargando: Boolean = true,
    val jugadores: List<JugadorResumen> = emptyList(),
    val usuarioActivoId: Long? = null,

    /** true solo si se abrió desde el panel Y el usuario activo es Administrador. */
    val modoAdmin: Boolean = false,

    // Diálogo de contraseña
    val jugadorParaLogin: JugadorResumen? = null,
    val password: String = "",
    val passwordVisible: Boolean = false,
    val errorPassword: String? = null,
    val validando: Boolean = false,

    // Diálogo de eliminación (solo Administrador)
    val jugadorParaEliminar: JugadorResumen? = null,

    val mensaje: String? = null,

    /** Evento: rol con el que se ingresó; la pantalla navega y lo limpia. */
    val rolIngresado: Rol? = null
)
