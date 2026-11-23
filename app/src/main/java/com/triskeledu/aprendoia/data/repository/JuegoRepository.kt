package com.triskeledu.aprendoia.data.repository

import com.triskeledu.aprendoia.data.local.AppDatabase
import com.triskeledu.aprendoia.data.local.Transaccion
import com.triskeledu.aprendoia.data.local.entity.ContenidoEntity
import com.triskeledu.aprendoia.data.local.entity.DescubrimientoEntity
import com.triskeledu.aprendoia.data.local.entity.HistorialEntity
import com.triskeledu.aprendoia.data.local.entity.NotificacionEntity
import com.triskeledu.aprendoia.data.local.entity.PuntoEntity
import com.triskeledu.aprendoia.data.local.entity.SelfieEntity
import com.triskeledu.aprendoia.data.local.entity.TipoActividad
import com.triskeledu.aprendoia.data.local.entity.TipoNotificacion
import com.triskeledu.aprendoia.data.local.relacion.ContenidoConEstado
import com.triskeledu.aprendoia.data.local.relacion.DescubrimientoDetalle
import com.triskeledu.aprendoia.data.local.relacion.SelfieDetalle
import com.triskeledu.aprendoia.data.local.relacion.EstadisticasJugador
import com.triskeledu.aprendoia.data.local.relacion.PuntoConEstado
import com.triskeledu.aprendoia.data.remote.ErroresRed
import com.triskeledu.aprendoia.data.remote.api.JuegoApi
import com.triskeledu.aprendoia.data.remote.dto.EscaneoRequestDto
import com.triskeledu.aprendoia.data.remote.dto.SelfieRequestDto
import com.triskeledu.aprendoia.data.remote.dto.VistoRequestDto
import com.triskeledu.aprendoia.util.Coordenada
import com.triskeledu.aprendoia.util.Notificador
import com.triskeledu.aprendoia.util.Ubicacion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

const val RADIO_CERCANIA = 150f


/**
 * Datos y reglas del juego (puntos, escaneos, selfies, historial, notificaciones).
 *
 * Modo "offline-first": la interfaz siempre lee de Room, y cada acción del
 * jugador se envía también al microservicio ms-juego. Si el servidor no
 * responde, el juego sigue funcionando y SincronizacionRepository sube lo
 * pendiente cuando vuelve la conexión.
 */
class JuegoRepository(
    private val db: AppDatabase,
    private val notificador: Notificador,
    private val api: JuegoApi,
    private val sincronizacion: SincronizacionRepository,
    private val transaccion: Transaccion
) {

    // ---------- Dashboard / Perfil ----------

    fun observarEstadisticas(usuarioId: Long): Flow<EstadisticasJugador> =
        db.descubrimientoDao().observarEstadisticas(usuarioId)

    /** Cantidad total de lugares con QR (para "12 de 30 lugares descubiertos"). */
    fun observarTotalLugares(): Flow<Int> = db.puntoDao().observarTotal()

    fun observarDesafioActual(usuarioId: Long): Flow<PuntoEntity?> =
        db.puntoDao().observarHitoPendiente(usuarioId)

    fun observarNotificacionesNoLeidas(usuarioId: Long): Flow<Int> =
        db.notificacionDao().observarNoLeidas(usuarioId)

    // ---------- Mapa (pantalla 5) ----------

    fun observarPuntosConEstado(usuarioId: Long): Flow<List<PuntoConEstado>> =
        db.puntoDao().observarConEstado(usuarioId)

    // ---------- Detalle del punto (pantalla 6) ----------

    fun observarPunto(puntoId: Long): Flow<PuntoEntity?> =
        db.puntoDao().observarPorId(puntoId)

    fun observarContenidosDePunto(puntoId: Long, usuarioId: Long): Flow<List<ContenidoConEstado>> =
        db.contenidoDao().observarPorPuntoConEstado(puntoId, usuarioId)

    fun observarTieneSelfie(usuarioId: Long, puntoId: Long): Flow<Boolean> =
        db.selfieDao().observarExiste(usuarioId, puntoId)

    // ---------- Escáner y resultado (pantallas 7 y 8) ----------

    suspend fun obtenerContenido(contenidoId: Long): ContenidoEntity? =
        db.contenidoDao().obtenerPorId(contenidoId)

    suspend fun obtenerPunto(puntoId: Long): PuntoEntity? =
        db.puntoDao().obtenerPorId(puntoId)

    /**
     * Registra el QR escaneado por el jugador. Regla del caso: +10 puntos
     * (o lo que indique la cápsula) SOLO la primera vez.
     *
     * Todo ocurre dentro de una transacción: si algo falla, no queda
     * un descubrimiento sin su historial (o al revés).
     */
    suspend fun registrarEscaneo(usuarioId: Long?, codigo: String): ResultadoEscaneo {
        if (usuarioId == null) return ResultadoEscaneo.SinSesion

        // 1) POST /api/descubrimientos en ms-juego (valida el código en ms-contenidos).
        val remoto = runCatching { api.escanear(EscaneoRequestDto(usuarioId, codigo)) }
        sincronizacion.registrar(remoto)
        val error = remoto.exceptionOrNull()
        if (error != null && ErroresRed.codigoHttp(error) == 404) return ResultadoEscaneo.NoEncontrado

        // 2) Si el servidor conoce una cápsula nueva que el teléfono aún no tiene, se descarga.
        val contenido: ContenidoEntity = db.contenidoDao().buscarPorCodigoQr(codigo)
            ?: if (remoto.isSuccess) {
                sincronizacion.sincronizarContenidos()
                db.contenidoDao().buscarPorCodigoQr(codigo)
            } else {
                null
            }
            ?: return ResultadoEscaneo.NoEncontrado

        // 3) Se guarda en Room (la interfaz lee de aquí).
        val resultado = transaccion.ejecutar<ResultadoEscaneo> {
            val idNuevo = db.descubrimientoDao().insertar(
                DescubrimientoEntity(
                    usuarioId = usuarioId,
                    contenidoId = contenido.id,
                    puntos = contenido.puntos
                )
            )
            // IGNORE devuelve -1 cuando ya existía (índice único usuario + contenido).
            if (idNuevo == -1L) return@ejecutar ResultadoEscaneo.Repetido(contenido)

            val punto = db.puntoDao().obtenerPorId(contenido.puntoId)
            db.historialDao().insertar(
                HistorialEntity(
                    usuarioId = usuarioId,
                    tipo = TipoActividad.QR,
                    titulo = "Escaneaste un QR",
                    detalle = "${contenido.titulo} · ${punto?.nombre ?: ""}",
                    puntos = contenido.puntos
                )
            )
            db.notificacionDao().insertar(
                NotificacionEntity(
                    usuarioId = usuarioId,
                    titulo = "¡Ganaste ${contenido.puntos} puntos!",
                    mensaje = "Desbloqueaste la cápsula \"${contenido.titulo}\".",
                    tipo = TipoNotificacion.LOGRO,
                    destino = "video/${contenido.id}"
                )
            )
            if (punto?.esHitoTuristico == true &&
                !db.selfieDao().existe(usuarioId, punto.id)
            ) {
                db.notificacionDao().insertar(
                    NotificacionEntity(
                        usuarioId = usuarioId,
                        titulo = "Desafío: selfie en ${punto.nombre}",
                        mensaje = "Tómate una selfie con el QR de fondo y gana 100 puntos extra.",
                        tipo = TipoNotificacion.DESAFIO,
                        destino = "selfie/${punto.id}"
                    )
                )
            }
            ResultadoEscaneo.Nuevo(contenido, contenido.puntos)
        }

        // La notificación del sistema va fuera de la transacción.
        if (resultado is ResultadoEscaneo.Nuevo && notificacionesActivas(usuarioId)) {
            notificador.mostrar(
                titulo = "¡+${resultado.puntosGanados} puntos!",
                mensaje = "Desbloqueaste \"${contenido.titulo}\". ¡Sigue explorando Santiago!"
            )
        }
        return resultado
    }

    // ---------- Colección, Progreso e Historial (pantallas 11, 12 y 13) ----------

    fun observarColeccion(usuarioId: Long): Flow<List<DescubrimientoDetalle>> =
        db.descubrimientoDao().observarDetalle(usuarioId)

    fun observarSelfies(usuarioId: Long): Flow<List<SelfieDetalle>> =
        db.selfieDao().observarDetalle(usuarioId)

    fun observarTotalCapsulas(): Flow<Int> = db.contenidoDao().observarTotalPublicados()

    fun observarHistorial(usuarioId: Long): Flow<List<HistorialEntity>> =
        db.historialDao().observarPorUsuario(usuarioId)

    // ---------- Video educativo (pantalla 9) ----------

    fun observarDescubrimiento(usuarioId: Long, contenidoId: Long): Flow<DescubrimientoEntity?> =
        db.descubrimientoDao().observar(usuarioId, contenidoId)

    /**
     * Marca la cápsula como vista. Solo se registra en el historial la primera vez.
     * (Ver videos no suma puntos: según el caso, los puntos vienen del QR y la selfie.)
     */
    suspend fun marcarVideoVisto(usuarioId: Long, contenido: ContenidoEntity): Boolean {
        // PUT /api/descubrimientos/visto en ms-juego (si falla, se sincroniza después).
        runCatching { api.marcarVisto(VistoRequestDto(usuarioId, contenido.id)) }
            .also { sincronizacion.registrar(it) }

        val filas = db.descubrimientoDao().marcarVisto(usuarioId, contenido.id)
        if (filas == 0) return false
        db.historialDao().insertar(
            HistorialEntity(
                usuarioId = usuarioId,
                tipo = TipoActividad.VIDEO,
                titulo = "Viste una cápsula de IA",
                detalle = contenido.titulo,
                puntos = 0
            )
        )
        return true
    }

    // ---------- Selfie turística (pantalla 10) ----------

    /**
     * Guarda la selfie y suma +100 puntos (una sola vez por hito y jugador).
     * Devuelve los puntos ganados, o null si ya existía.
     */
    suspend fun registrarSelfie(usuarioId: Long, punto: PuntoEntity, rutaFoto: String): Int? {
        val puntosSelfie = 100
        val resultado = transaccion.ejecutar<Int?> {
            val id = db.selfieDao().insertar(
                SelfieEntity(usuarioId = usuarioId, puntoId = punto.id, rutaFoto = rutaFoto, puntos = puntosSelfie)
            )
            if (id == -1L) return@ejecutar null

            db.historialDao().insertar(
                HistorialEntity(
                    usuarioId = usuarioId,
                    tipo = TipoActividad.SELFIE,
                    titulo = "Selfie en hito turístico",
                    detalle = punto.nombre,
                    puntos = puntosSelfie
                )
            )
            db.notificacionDao().insertar(
                NotificacionEntity(
                    usuarioId = usuarioId,
                    titulo = "¡+$puntosSelfie puntos por tu selfie!",
                    mensaje = "Completaste el desafío turístico en ${punto.nombre}.",
                    tipo = TipoNotificacion.LOGRO,
                    destino = "detalle_punto/${punto.id}"
                )
            )
            puntosSelfie
        }
        if (resultado != null) {
            // POST /api/selfies en ms-juego (si falla, se sube en la próxima sincronización).
            runCatching { api.registrarSelfie(SelfieRequestDto(usuarioId, punto.id, rutaFoto)) }
                .also { sincronizacion.registrar(it) }
        }
        if (resultado != null && notificacionesActivas(usuarioId)) {
            notificador.mostrar("¡+$resultado puntos!", "Tu selfie en ${punto.nombre} quedó guardada en la galería.")
        }
        return resultado
    }

    // ---------- Notificaciones (pantalla 14) ----------

    fun observarNotificaciones(usuarioId: Long): Flow<List<NotificacionEntity>> =
        db.notificacionDao().observarPorUsuario(usuarioId)

    suspend fun marcarNotificacionLeida(id: Long) = db.notificacionDao().marcarLeida(id)

    suspend fun marcarTodasLeidas(usuarioId: Long) = db.notificacionDao().marcarTodasLeidas(usuarioId)

    suspend fun eliminarNotificacion(id: Long) = db.notificacionDao().eliminar(id)

    /** Mensaje de bienvenida al crear un jugador nuevo. */
    suspend fun crearBienvenida(usuarioId: Long, nombre: String) {
        db.notificacionDao().insertar(
            NotificacionEntity(
                usuarioId = usuarioId,
                titulo = "¡Bienvenido/a, $nombre!",
                mensaje = "Abre el mapa, busca los puntos de Santiago y escanea tu primer QR para ganar 10 puntos.",
                tipo = TipoNotificacion.EVENTO,
                destino = "mapa"
            )
        )
    }

    /**
     * Aviso de "punto cercano": si el jugador está a menos de [radioMetros] de un
     * punto que aún no descubre, se crea UNA notificación por punto (nunca se repite).
     * Se llama mientras la app está abierta (mapa y dashboard).
     */
    suspend fun verificarCercania(usuarioId: Long, posicion: Coordenada, radioMetros: Float = RADIO_CERCANIA) {
        val pendientes = db.puntoDao().observarConEstado(usuarioId).first().filter { !it.descubierto }
        pendientes.forEach { item ->
            val punto = item.punto
            val distancia = Ubicacion.distanciaMetros(posicion, Coordenada(punto.latitud, punto.longitud))
            val destino = "detalle_punto/${punto.id}"
            if (distancia <= radioMetros &&
                !db.notificacionDao().existe(usuarioId, TipoNotificacion.PUNTO_CERCANO, destino)
            ) {
                val mensaje = "Estás a ${Ubicacion.formatearDistancia(distancia)} de ${punto.nombre}. " +
                    "¡Busca su código QR y gana puntos!"
                db.notificacionDao().insertar(
                    NotificacionEntity(
                        usuarioId = usuarioId,
                        titulo = "Punto cercano: ${punto.nombre}",
                        mensaje = mensaje,
                        tipo = TipoNotificacion.PUNTO_CERCANO,
                        destino = destino
                    )
                )
                if (notificacionesActivas(usuarioId)) {
                    notificador.mostrar("📍 ${punto.nombre} está cerca", mensaje)
                }
            }
        }
    }

    private suspend fun notificacionesActivas(usuarioId: Long): Boolean =
        db.usuarioDao().obtenerPorId(usuarioId)?.notificacionesActivas == true
}
