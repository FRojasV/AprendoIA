package com.triskeledu.aprendoia.data.repository

import com.triskeledu.aprendoia.data.local.AppDatabase
import com.triskeledu.aprendoia.data.local.entity.ContenidoEntity
import com.triskeledu.aprendoia.data.local.entity.NotificacionEntity
import com.triskeledu.aprendoia.data.local.entity.PuntoEntity
import com.triskeledu.aprendoia.data.local.entity.TipoNotificacion
import com.triskeledu.aprendoia.data.local.relacion.CapsulaGestion
import com.triskeledu.aprendoia.data.local.relacion.ResumenGestion
import com.triskeledu.aprendoia.data.remote.ErrorServidor
import com.triskeledu.aprendoia.data.remote.ErroresRed
import com.triskeledu.aprendoia.data.remote.aEntidad
import com.triskeledu.aprendoia.data.remote.aRequest
import com.triskeledu.aprendoia.data.remote.api.ContenidosApi
import com.triskeledu.aprendoia.util.Notificador
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Operaciones del Administrador y del Creador de Contenido (CRUD de puntos y cápsulas).
 *
 * Cada operación va PRIMERO al microservicio ms-contenidos y, si responde bien,
 * se guarda en Room. Así todos los teléfonos ven los mismos datos y la
 * interfaz (que observa Room) se actualiza sola.
 */
class GestionRepository(
    private val db: AppDatabase,
    private val notificador: Notificador,
    private val api: ContenidosApi,
    private val sincronizacion: SincronizacionRepository
) {

    fun observarResumen(): Flow<ResumenGestion> = combine(
        db.puntoDao().observarTotal(),
        db.contenidoDao().observarTotal(),
        db.contenidoDao().observarPendientes(),
        db.usuarioDao().observarCantidadClientes()
    ) { puntos, capsulas, pendientes, jugadores -> ResumenGestion(puntos, capsulas, pendientes, jugadores) }

    /** Trae del servidor la última versión de puntos y cápsulas. */
    suspend fun refrescar(): Result<Unit> = sincronizacion.sincronizarContenidos()

    // ---------- Puntos ----------

    fun observarPuntos(): Flow<List<PuntoEntity>> = db.puntoDao().observarTodos()

    suspend fun obtenerPunto(id: Long): PuntoEntity? = db.puntoDao().obtenerPorId(id)

    /** Crea (id = 0) o actualiza el punto en ms-contenidos y luego en Room. Devuelve el id. */
    suspend fun guardarPunto(punto: PuntoEntity): Result<Long> = remoto {
        val dto = if (punto.id == 0L) api.crearPunto(punto.aRequest())
        else api.actualizarPunto(punto.id, punto.aRequest())
        db.puntoDao().guardar(dto.aEntidad())
        dto.id
    }

    /** Al borrar un punto, el servidor y SQLite borran en cascada sus cápsulas. */
    suspend fun eliminarPunto(punto: PuntoEntity): Result<Unit> = remoto {
        api.eliminarPunto(punto.id)
        db.puntoDao().eliminarPorId(punto.id)
    }

    // ---------- Cápsulas ----------

    fun observarCapsulas(): Flow<List<CapsulaGestion>> = db.contenidoDao().observarParaGestion()

    suspend fun obtenerCapsula(id: Long): ContenidoEntity? = db.contenidoDao().obtenerPorId(id)

    /** true si ningún OTRO contenido usa ese código QR (el servidor también lo valida). */
    suspend fun codigoDisponible(codigo: String, excluirId: Long): Boolean =
        db.contenidoDao().contarPorCodigoQr(codigo, excluirId) == 0

    /**
     * Guarda la cápsula en ms-contenidos y en Room. Si queda publicada y antes
     * no lo estaba, se avisa a todos los jugadores.
     */
    suspend fun guardarCapsula(capsula: ContenidoEntity, estabaPublicada: Boolean): Result<Long> = remoto {
        val dto = if (capsula.id == 0L) api.crearCapsula(capsula.aRequest())
        else api.actualizarCapsula(capsula.id, capsula.aRequest())
        val guardada = dto.aEntidad()
        db.contenidoDao().guardar(guardada)
        if (guardada.publicado && !estabaPublicada) avisarNuevaCapsula(guardada)
        dto.id
    }

    /** Solo el Administrador: deja visible para los jugadores una cápsula pendiente. */
    suspend fun publicar(capsula: ContenidoEntity): Result<Unit> = remoto {
        if (!capsula.publicado) {
            val dto = api.publicarCapsula(capsula.id)
            db.contenidoDao().guardar(dto.aEntidad())
            avisarNuevaCapsula(capsula)
        }
    }

    suspend fun eliminarCapsula(capsula: ContenidoEntity): Result<Unit> = remoto {
        api.eliminarCapsula(capsula.id)
        db.contenidoDao().eliminarPorId(capsula.id)
    }

    /** Notificación "Nuevo video" en la bandeja de cada jugador + aviso en el teléfono. */
    private suspend fun avisarNuevaCapsula(capsula: ContenidoEntity) {
        val punto = db.puntoDao().obtenerPorId(capsula.puntoId)
        val lugar = punto?.nombre ?: "Santiago"
        val avisos = db.usuarioDao().obtenerIdsClientes().map { usuarioId ->
            NotificacionEntity(
                usuarioId = usuarioId,
                titulo = "Nueva cápsula: ${capsula.titulo}",
                mensaje = "Hay contenido nuevo en $lugar. ¡Ve a buscar su código QR!",
                tipo = TipoNotificacion.NUEVO_VIDEO,
                destino = "detalle_punto/${capsula.puntoId}"
            )
        }
        if (avisos.isNotEmpty()) {
            db.notificacionDao().insertarVarias(avisos)
            notificador.mostrar("🎬 Nueva cápsula de IA", "\"${capsula.titulo}\" ya está disponible en $lugar.")
        }
    }

    /**
     * Ejecuta una llamada al servidor y convierte cualquier error en un
     * mensaje claro para el usuario ("Ese código QR ya está en uso", "Sin conexión...").
     */
    private suspend fun <T> remoto(bloque: suspend () -> T): Result<T> {
        val resultado = runCatching { bloque() }
        sincronizacion.registrar(resultado)
        return resultado.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(ErrorServidor(ErroresRed.mensaje(it), ErroresRed.codigoHttp(it))) }
        )
    }
}
