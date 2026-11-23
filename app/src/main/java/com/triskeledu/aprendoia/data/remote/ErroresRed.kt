package com.triskeledu.aprendoia.data.remote

import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

/**
 * Traduce los errores de red a mensajes para el usuario.
 * Los microservicios responden los errores así: {"estado":409,"error":"Ese correo ya está en uso"}
 */
object ErroresRed {

    const val SIN_CONEXION = "Sin conexión con el servidor. Revisa que los microservicios estén iniciados."

    /** true si el servidor no respondió (apagado, sin red, tiempo agotado). */
    fun esSinConexion(error: Throwable): Boolean = error is IOException

    /** Código HTTP del error, o null si no hubo respuesta del servidor. */
    fun codigoHttp(error: Throwable): Int? = (error as? HttpException)?.code()

    fun mensaje(error: Throwable): String = when (error) {
        is IOException -> SIN_CONEXION
        is HttpException -> leerMensajeServidor(error) ?: "Error del servidor (${error.code()})"
        else -> error.message ?: "Ocurrió un error inesperado"
    }

    private fun leerMensajeServidor(error: HttpException): String? = try {
        val cuerpo = error.response()?.errorBody()?.string().orEmpty()
        val json = JSONObject(cuerpo)
        // Si es error de validación, se muestra el primer campo con problema.
        val campos = json.optJSONObject("campos")
        if (campos != null && campos.length() > 0) campos.getString(campos.keys().next())
        else json.optString("error").takeIf { it.isNotBlank() }
    } catch (e: Exception) {
        null
    }
}

/** Excepción con un mensaje ya listo para mostrar en pantalla. */
class ErrorServidor(mensaje: String, val codigo: Int? = null) : Exception(mensaje)
