package com.triskeledu.aprendoia.data.repository

import com.triskeledu.aprendoia.data.local.AppDatabase
import com.triskeledu.aprendoia.data.local.entity.ContenidoEntity
import com.triskeledu.aprendoia.data.local.entity.NotificacionEntity
import com.triskeledu.aprendoia.data.local.entity.PuntoEntity
import com.triskeledu.aprendoia.data.local.entity.TipoNotificacion
import com.triskeledu.aprendoia.data.local.relacion.CapsulaGestion
import com.triskeledu.aprendoia.data.local.relacion.ResumenGestion
import com.triskeledu.aprendoia.util.Notificador
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Operaciones del Administrador y del Creador de Contenido:
 * mantener puntos georreferenciados, cápsulas de IA y su publicación.
 * (Reemplaza al backend mientras no exista.)
 */
class GestionRepository(
    private val db: AppDatabase,
    private val notificador: Notificador
) {

    fun observarResumen(): Flow<ResumenGestion> = combine(
        db.puntoDao().observarTotal(),
        db.contenidoDao().observarTotal(),
        db.contenidoDao().observarPendientes(),
        db.usuarioDao().observarCantidadClientes()
    ) { puntos, capsulas, pendientes, jugadores -> ResumenGestion(puntos, capsulas, pendientes, jugadores) }

    // ---------- Puntos ----------

    fun observarPuntos(): Flow<List<PuntoEntity>> = db.puntoDao().observarTodos()

    suspend fun obtenerPunto(id: Long): PuntoEntity? = db.puntoDao().obtenerPorId(id)

    /** Inserta si es nuevo (id = 0) o actualiza si ya existe. Devuelve el id. */
    suspend fun guardarPunto(punto: PuntoEntity): Long =
        if (punto.id == 0L) db.puntoDao().insertar(punto)
        else {
            db.puntoDao().actualizar(punto)
            punto.id
        }

    /** Al borrar un punto, SQLite borra en cascada sus cápsulas, descubrimientos y selfies. */
    suspend fun eliminarPunto(punto: PuntoEntity) = db.puntoDao().eliminar(punto)

    // ---------- Cápsulas ----------

    fun observarCapsulas(): Flow<List<CapsulaGestion>> = db.contenidoDao().observarParaGestion()

    suspend fun obtenerCapsula(id: Long): ContenidoEntity? = db.contenidoDao().obtenerPorId(id)

    /** true si ningún OTRO contenido usa ese código QR. */
    suspend fun codigoDisponible(codigo: String, excluirId: Long): Boolean =
        db.contenidoDao().contarPorCodigoQr(codigo, excluirId) == 0

    /**
     * Guarda la cápsula. Si queda publicada y antes no lo estaba,
     * se avisa a todos los jugadores.
     */
    suspend fun guardarCapsula(capsula: ContenidoEntity, estabaPublicada: Boolean): Long {
        val id = if (capsula.id == 0L) db.contenidoDao().insertar(capsula)
        else {
            db.contenidoDao().actualizar(capsula)
            capsula.id
        }
        if (capsula.publicado && !estabaPublicada) avisarNuevaCapsula(capsula)
        return id
    }

    /** Solo el Administrador: deja visible para los jugadores una cápsula pendiente. */
    suspend fun publicar(capsula: ContenidoEntity) {
        if (capsula.publicado) return
        db.contenidoDao().actualizar(capsula.copy(publicado = true))
        avisarNuevaCapsula(capsula)
    }

    suspend fun eliminarCapsula(capsula: ContenidoEntity) = db.contenidoDao().eliminar(capsula)

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
}
