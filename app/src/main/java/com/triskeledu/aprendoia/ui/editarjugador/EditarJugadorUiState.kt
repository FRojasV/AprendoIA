package com.triskeledu.aprendoia.ui.editarjugador

import com.triskeledu.aprendoia.data.local.entity.UsuarioEntity
import com.triskeledu.aprendoia.util.ErroresJugador

/**
 * MODELO: estado del formulario de edición de un jugador.
 * Lo usa el Administrador (pantalla 3) y el propio jugador (pantalla 15).
 */
data class EditarJugadorUiState(
    val cargando: Boolean = true,
    val original: UsuarioEntity? = null,

    val nombre: String = "",
    val correo: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val avatar: String = "avatar_1",
    /** Vacía = mantener la contraseña actual. */
    val nuevaPassword: String = "",

    val passwordVisible: Boolean = false,
    val mostrarTodosAvatares: Boolean = true,

    val errores: ErroresJugador = ErroresJugador(),
    val guardando: Boolean = false,
    val mensajeError: String? = null,
    val guardado: Boolean = false
)
