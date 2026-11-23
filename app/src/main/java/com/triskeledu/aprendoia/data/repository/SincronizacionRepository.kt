package com.triskeledu.aprendoia.data.repository

import com.triskeledu.aprendoia.data.local.AppDatabase
import com.triskeledu.aprendoia.data.local.Transaccion
import com.triskeledu.aprendoia.data.local.entity.DescubrimientoEntity
import com.triskeledu.aprendoia.data.local.entity.SelfieEntity
import com.triskeledu.aprendoia.data.remote.ErroresRed
import com.triskeledu.aprendoia.data.remote.aEntidad
import com.triskeledu.aprendoia.data.remote.api.ContenidosApi
import com.triskeledu.aprendoia.data.remote.api.JuegoApi
import com.triskeledu.aprendoia.data.remote.dto.EscaneoRequestDto
import com.triskeledu.aprendoia.data.remote.dto.SelfieRequestDto
import com.triskeledu.aprendoia.data.remote.fechaIsoAMillis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Estado de la conexión con los microservicios (para mostrar un aviso en pantalla). */
data class EstadoConexion(
    /** null = aún no se intenta; true = el servidor respondió; false = sin conexión. */
    val conectado: Boolean? = null,
    val mensaje: String? = null
)

/**
 * Sincroniza Room (caché local) con los microservicios.
 *  - Contenidos (puntos y cápsulas): el servidor es la fuente de verdad.
 *  - Progreso del jugador: primero SUBE lo hecho sin conexión y luego BAJA lo del servidor.
 * Si el servidor no responde, la app sigue funcionando con los datos guardados.
 */
class SincronizacionRepository(
    private val db: AppDatabase,
    private val contenidosApi: ContenidosApi,
    private val juegoApi: JuegoApi,
    private val transaccion: Transaccion
) {

    private val _estado = MutableStateFlow(EstadoConexion())
    val estado: StateFlow<EstadoConexion> = _estado.asStateFlow()

    /** Descarga puntos y cápsulas de ms-contenidos y reemplaza la copia local. */
    suspend fun sincronizarContenidos(): Result<Unit> {
        val resultado = runCatching {
            val puntos = contenidosApi.listarPuntos()
            val capsulas = contenidosApi.listarCapsulas()
            transaccion.ejecutar {
                // Primero los puntos (las cápsulas tienen clave foránea hacia ellos).
                db.puntoDao().guardarTodos(puntos.map { it.aEntidad() })
                db.contenidoDao().guardarTodos(capsulas.map { it.aEntidad() })
                db.contenidoDao().eliminarExcepto(capsulas.map { it.id })
                db.puntoDao().eliminarExcepto(puntos.map { it.id })
            }
        }
        registrar(resultado)
        return resultado
    }

    /**
     * Sincroniza el progreso de un jugador con ms-juego:
     * 1. Sube los QR y selfies registrados sin conexión.
     * 2. Descarga descubrimientos, selfies e historial del servidor.
     */
    suspend fun sincronizarProgreso(usuarioId: Long): Result<Unit> {
        val resultado = runCatching {
            subirPendientes(usuarioId)

            val descubrimientos = juegoApi.descubrimientos(usuarioId)
            val selfies = juegoApi.selfies(usuarioId)
            val historial = juegoApi.historial(usuarioId)

            transaccion.ejecutar {
                descubrimientos.forEach { d ->
                    // Solo si la cápsula existe localmente (clave foránea).
                    if (db.contenidoDao().obtenerPorId(d.capsulaId) != null) {
                        db.descubrimientoDao().insertar(
                            DescubrimientoEntity(
                                usuarioId = usuarioId,
                                contenidoId = d.capsulaId,
                                puntos = d.puntos,
                                fecha = fechaIsoAMillis(d.fecha)
                            )
                        )
                        if (d.visto) {
                            db.descubrimientoDao().marcarVisto(usuarioId, d.capsulaId, fechaIsoAMillis(d.fechaVisto))
                        }
                    }
                }
                selfies.forEach { s ->
                    if (db.puntoDao().obtenerPorId(s.puntoId) != null) {
                        db.selfieDao().insertar(
                            SelfieEntity(
                                usuarioId = usuarioId,
                                puntoId = s.puntoId,
                                rutaFoto = s.rutaFoto,
                                puntos = s.puntos,
                                fecha = fechaIsoAMillis(s.fecha)
                            )
                        )
                    }
                }
                // El historial del servidor reemplaza al local solo si está completo.
                if (historial.size >= db.historialDao().contar(usuarioId)) {
                    db.historialDao().eliminarDeUsuario(usuarioId)
                    db.historialDao().insertarVarios(historial.map { it.aEntidad() })
                }
            }
        }
        registrar(resultado)
        return resultado
    }

    /** Envía al servidor los descubrimientos y selfies que solo existen en el teléfono. */
    private suspend fun subirPendientes(usuarioId: Long) {
        val enServidor = juegoApi.descubrimientos(usuarioId).map { it.capsulaId }.toSet()
        db.descubrimientoDao().obtenerDeUsuario(usuarioId)
            .filter { it.contenidoId !in enServidor }
            .forEach { local ->
                val codigo = db.contenidoDao().obtenerPorId(local.contenidoId)?.codigoQr ?: return@forEach
                runCatching { juegoApi.escanear(EscaneoRequestDto(usuarioId, codigo)) }
            }

        val selfiesServidor = juegoApi.selfies(usuarioId).map { it.puntoId }.toSet()
        db.selfieDao().obtenerDeUsuario(usuarioId)
            .filter { it.puntoId !in selfiesServidor }
            .forEach { local ->
                runCatching { juegoApi.registrarSelfie(SelfieRequestDto(usuarioId, local.puntoId, local.rutaFoto)) }
            }
    }

    /** Guarda si la última llamada funcionó, para mostrar "sin conexión" en la interfaz. */
    fun registrar(resultado: Result<*>) {
        _estado.value = resultado.fold(
            onSuccess = { EstadoConexion(conectado = true) },
            onFailure = { EstadoConexion(conectado = false, mensaje = ErroresRed.mensaje(it)) }
        )
    }
}
