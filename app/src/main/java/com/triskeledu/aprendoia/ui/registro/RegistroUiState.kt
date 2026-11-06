package com.triskeledu.aprendoia.ui.registro

import com.triskeledu.aprendoia.util.ErroresJugador

/**
 * MODELO: estado completo del formulario de registro (pantalla 2).
 * Es inmutable: el ViewModel crea una copia nueva con copy() en cada cambio.
 */
data class RegistroUiState(
    // Campos del formulario
    val nombre: String = "",
    val correo: String = "",
    val telefono: String = "",
    val password: String = "",
    val direccion: String = "",
    val avatar: String = "avatar_1",

    // Estado visual
    val passwordVisible: Boolean = false,
    val mostrarTodosAvatares: Boolean = false,

    // Validación y resultado
    val errores: ErroresJugador = ErroresJugador(),
    val guardando: Boolean = false,
    val mensajeError: String? = null,
    val registroExitoso: Boolean = false
)
