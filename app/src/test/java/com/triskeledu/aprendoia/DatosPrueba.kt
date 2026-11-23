package com.triskeledu.aprendoia

import com.triskeledu.aprendoia.data.local.Transaccion
import com.triskeledu.aprendoia.data.local.entity.ContenidoEntity
import com.triskeledu.aprendoia.data.local.entity.PuntoEntity
import com.triskeledu.aprendoia.data.local.entity.Rol
import com.triskeledu.aprendoia.data.local.entity.UsuarioEntity
import com.triskeledu.aprendoia.data.local.relacion.EstadisticasJugador
import com.triskeledu.aprendoia.data.remote.dto.CapsulaDto
import com.triskeledu.aprendoia.data.remote.dto.PuntoDto
import com.triskeledu.aprendoia.data.remote.dto.UsuarioDto
import com.triskeledu.aprendoia.util.Seguridad
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.net.ConnectException

/** Objetos de ejemplo reutilizados por todas las pruebas. */
object DatosPrueba {

    val ana = UsuarioEntity(
        id = 3, nombre = "Ana González", alias = "Ana", correo = "ana@email.com",
        telefono = "+56 9 1234 5678", password = Seguridad.hash("1234"), rol = Rol.CLIENTE
    )

    val admin = UsuarioEntity(
        id = 1, nombre = "Administrador", alias = "Admin", correo = "admin@triskeledu.cl",
        password = Seguridad.hash("123"), rol = Rol.ADMIN
    )

    val anaDto = UsuarioDto(
        id = 3, nombre = "Ana González", alias = "Ana", correo = "ana@email.com",
        telefono = "+56 9 1234 5678", direccion = "", avatar = "avatar_1", rol = "CLIENTE",
        notificacionesActivas = true
    )

    val plaza = PuntoEntity(
        id = 1, nombre = "Plaza de Armas", descripcion = "Centro histórico de Santiago.",
        latitud = -33.4378, longitud = -70.6505, esHitoTuristico = true
    )

    val forestal = PuntoEntity(
        id = 3, nombre = "Parque Forestal", descripcion = "Parque junto al río Mapocho.",
        latitud = -33.4362, longitud = -70.6425, esHitoTuristico = false
    )

    val plazaDto = PuntoDto(1, "Plaza de Armas", "Centro histórico de Santiago.", "", -33.4378, -70.6505, true, null)

    val capsula = ContenidoEntity(
        id = 1, puntoId = 1, titulo = "Introducción a la IA", tema = "Fundamentos",
        autor = "Triskeledu", url = "", codigoQr = "QR-PLAZA-ARMAS", puntos = 10, publicado = true
    )

    val capsulaDto = CapsulaDto(
        id = 1, puntoId = 1, puntoNombre = "Plaza de Armas", titulo = "Introducción a la IA",
        tema = "Fundamentos", autor = "Triskeledu", url = "", duracion = "2:15", descripcion = "",
        codigoQr = "QR-PLAZA-ARMAS", puntos = 10, publicado = true
    )

    val estadisticasVacias = EstadisticasJugador(0, 0, 0, 0, 0, 0)

    /** Simula una respuesta de error de un microservicio, por ejemplo 409. */
    fun errorHttp(codigo: Int, mensaje: String): HttpException = HttpException(
        Response.error<Any>(
            codigo,
            """{"estado":$codigo,"error":"$mensaje"}""".toResponseBody("application/json".toMediaType())
        )
    )

    /** Simula que el microservicio está apagado. */
    fun sinConexion(): ConnectException = ConnectException("Failed to connect to /10.0.2.2:8081")

    /** Transacción falsa: ejecuta el bloque directamente (sin Room). */
    val transaccionDirecta = object : Transaccion {
        override suspend fun <R> ejecutar(bloque: suspend () -> R): R = bloque()
    }
}
