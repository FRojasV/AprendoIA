package com.triskeledu.aprendoia.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.triskeledu.aprendoia.data.local.entity.DescubrimientoEntity
import com.triskeledu.aprendoia.data.local.relacion.DescubrimientoDetalle
import com.triskeledu.aprendoia.data.local.relacion.EstadisticasJugador
import kotlinx.coroutines.flow.Flow

@Dao
interface DescubrimientoDao {

    /**
     * Devuelve el id nuevo, o -1 si ya existía (índice único usuario+contenido).
     * Así sabemos si corresponde sumar puntos o no.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(descubrimiento: DescubrimientoEntity): Long

    @Query("SELECT * FROM descubrimientos WHERE usuarioId = :usuarioId AND contenidoId = :contenidoId LIMIT 1")
    suspend fun obtener(usuarioId: Long, contenidoId: Long): DescubrimientoEntity?

    /** Pantalla 9: se actualiza sola al marcar el video como visto. */
    @Query("SELECT * FROM descubrimientos WHERE usuarioId = :usuarioId AND contenidoId = :contenidoId LIMIT 1")
    fun observar(usuarioId: Long, contenidoId: Long): Flow<DescubrimientoEntity?>

    /** Sincronización: descubrimientos locales del jugador (para subir los hechos sin conexión). */
    @Query("SELECT * FROM descubrimientos WHERE usuarioId = :usuarioId")
    suspend fun obtenerDeUsuario(usuarioId: Long): List<DescubrimientoEntity>

    @Query(
        """
        UPDATE descubrimientos SET visto = 1, fechaVisto = :fecha
        WHERE usuarioId = :usuarioId AND contenidoId = :contenidoId AND visto = 0
        """
    )
    suspend fun marcarVisto(usuarioId: Long, contenidoId: Long, fecha: Long = System.currentTimeMillis()): Int

    /** Colección del jugador (pantalla 11), del más reciente al más antiguo. */
    @Query(
        """
        SELECT d.id AS descubrimientoId, c.id AS contenidoId, c.titulo, c.tema, c.url,
               p.id AS puntoId, p.nombre AS puntoNombre, p.esHitoTuristico, p.imagenUrl,
               d.puntos, d.fecha, d.visto
        FROM descubrimientos d
        INNER JOIN contenidos c ON c.id = d.contenidoId
        INNER JOIN puntos p ON p.id = c.puntoId
        WHERE d.usuarioId = :usuarioId
        ORDER BY d.fecha DESC
        """
    )
    fun observarDetalle(usuarioId: Long): Flow<List<DescubrimientoDetalle>>

    /** Resumen numérico del jugador para Dashboard, Progreso y Perfil. */
    @Query(
        """
        SELECT
            COALESCE((SELECT SUM(puntos) FROM descubrimientos WHERE usuarioId = :usuarioId), 0) AS puntosQr,
            COALESCE((SELECT SUM(puntos) FROM selfies WHERE usuarioId = :usuarioId), 0) AS puntosSelfies,
            (SELECT COUNT(*) FROM descubrimientos WHERE usuarioId = :usuarioId) AS qrEncontrados,
            (SELECT COUNT(*) FROM descubrimientos WHERE usuarioId = :usuarioId AND visto = 1) AS videosVistos,
            (SELECT COUNT(*) FROM selfies WHERE usuarioId = :usuarioId) AS selfies,
            (SELECT COUNT(DISTINCT c.puntoId) FROM descubrimientos d
                INNER JOIN contenidos c ON c.id = d.contenidoId
                WHERE d.usuarioId = :usuarioId) AS lugaresVisitados
        """
    )
    fun observarEstadisticas(usuarioId: Long): Flow<EstadisticasJugador>
}
