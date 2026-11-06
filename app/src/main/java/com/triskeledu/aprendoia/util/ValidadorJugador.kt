package com.triskeledu.aprendoia.util

import android.util.Patterns

/** Errores por campo de los formularios de jugador (null = campo válido). */
data class ErroresJugador(
    val nombre: String? = null,
    val correo: String? = null,
    val telefono: String? = null,
    val password: String? = null
) {
    val hayErrores: Boolean
        get() = nombre != null || correo != null || telefono != null || password != null
}

/**
 * Reglas de validación compartidas por Registro (pantalla 2),
 * Edición del administrador y Perfil (pantalla 15).
 */
object ValidadorJugador {

    fun nombre(valor: String): String? = when {
        valor.isBlank() -> "Ingresa tu nombre"
        valor.trim().length < 3 -> "Debe tener al menos 3 caracteres"
        else -> null
    }

    fun correo(valor: String): String? = when {
        valor.isBlank() -> "Ingresa tu correo"
        !Patterns.EMAIL_ADDRESS.matcher(valor.trim()).matches() -> "El correo no es válido"
        else -> null
    }

    fun telefono(valor: String): String? {
        val digitos = valor.count { it.isDigit() }
        return when {
            valor.isBlank() -> "Ingresa tu teléfono"
            digitos !in 8..12 -> "Debe tener entre 8 y 12 dígitos"
            else -> null
        }
    }

    /** obligatoria = false se usa al editar: vacía significa "no cambiar". */
    fun password(valor: String, obligatoria: Boolean = true): String? = when {
        valor.isBlank() && obligatoria -> "Ingresa una contraseña"
        valor.isNotBlank() && valor.length < 4 -> "Debe tener al menos 4 caracteres"
        else -> null
    }

    /** Solo deja dígitos, espacios y el signo +. */
    fun limpiarTelefono(valor: String): String =
        valor.filter { it.isDigit() || it == '+' || it == ' ' }.take(16)
}
