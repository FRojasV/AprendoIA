package com.triskeledu.aprendoia.data.local.relacion

import androidx.room.Embedded
import com.triskeledu.aprendoia.data.local.entity.ContenidoEntity
import com.triskeledu.aprendoia.data.local.entity.PuntoEntity
import com.triskeledu.aprendoia.data.local.entity.UsuarioEntity

/**
 * Clases de resultado para consultas que combinan varias tablas.
 * No son tablas: Room solo las llena con las columnas del SELECT.
 */

/** Pantalla 3: jugador con su puntaje total y QR encontrados. */
data class JugadorResumen(
    @Embedded val usuario: UsuarioEntity,
    val puntaje: Int,
    val qrEncontrados: Int
)

/** Pantalla 5: punto del mapa con su estado pendiente/completado. */
data class PuntoConEstado(
    @Embedded val punto: PuntoEntity,
    val descubierto: Boolean
)

/** Pantalla 6: cápsula de un punto con su estado para el jugador activo. */
data class ContenidoConEstado(
    @Embedded val contenido: ContenidoEntity,
    val descubierto: Boolean,
    val visto: Boolean
)

/** Gestión: cápsula con el nombre de su punto. */
data class CapsulaGestion(
    @Embedded val contenido: ContenidoEntity,
    val puntoNombre: String
)

/** Gestión: números del panel del Administrador / Creador. */
data class ResumenGestion(
    val puntos: Int = 0,
    val capsulas: Int = 0,
    val pendientes: Int = 0,
    val jugadores: Int = 0
)

/** Pantalla 11: elemento de la colección de descubrimientos. */
data class DescubrimientoDetalle(
    val descubrimientoId: Long,
    val contenidoId: Long,
    val titulo: String,
    val tema: String,
    val url: String,
    val puntoId: Long,
    val puntoNombre: String,
    val esHitoTuristico: Boolean,
    val imagenUrl: String?,
    val puntos: Int,
    val fecha: Long,
    val visto: Boolean
)

/** Pantalla 11 (pestaña Selfies): selfie con el nombre del lugar. */
data class SelfieDetalle(
    val selfieId: Long,
    val puntoId: Long,
    val puntoNombre: String,
    val rutaFoto: String,
    val puntos: Int,
    val fecha: Long
)

/** Pantallas 4, 12 y 15: estadísticas del jugador activo. */
data class EstadisticasJugador(
    val puntosQr: Int,
    val puntosSelfies: Int,
    val qrEncontrados: Int,
    val videosVistos: Int,
    val selfies: Int,
    val lugaresVisitados: Int
)

/** Puntaje total = puntos por QR + puntos por selfies. */
val EstadisticasJugador.puntajeTotal: Int
    get() = puntosQr + puntosSelfies

/** Máximo estimado por el caso para calcular la barra de progreso. */
const val PUNTAJE_MAXIMO = 10_000
