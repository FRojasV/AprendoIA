package com.triskeledu.aprendoia.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.triskeledu.aprendoia.data.local.entity.PuntoEntity
import com.triskeledu.aprendoia.data.local.relacion.PuntoConEstado
import kotlinx.coroutines.flow.Flow

@Dao
interface PuntoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarSiNoExisten(puntos: List<PuntoEntity>)

    @Insert
    suspend fun insertar(punto: PuntoEntity): Long

    @Update
    suspend fun actualizar(punto: PuntoEntity)

    @Delete
    suspend fun eliminar(punto: PuntoEntity)

    // ---------- Sincronización con ms-contenidos ----------

    /** Inserta o actualiza SIN borrar la fila (REPLACE borraría en cascada el progreso). */
    @Upsert
    suspend fun guardarTodos(puntos: List<PuntoEntity>)

    @Upsert
    suspend fun guardar(punto: PuntoEntity)

    /** Quita los puntos que ya no existen en el servidor. */
    @Query("DELETE FROM puntos WHERE id NOT IN (:ids)")
    suspend fun eliminarExcepto(ids: List<Long>)

    @Query("DELETE FROM puntos WHERE id = :id")
    suspend fun eliminarPorId(id: Long)

    @Query("SELECT COUNT(*) FROM puntos")
    suspend fun contar(): Int

    @Query("SELECT COUNT(*) FROM puntos")
    fun observarTotal(): Flow<Int>

    @Query("SELECT * FROM puntos ORDER BY nombre COLLATE NOCASE")
    fun observarTodos(): Flow<List<PuntoEntity>>

    @Query("SELECT * FROM puntos WHERE id = :id")
    suspend fun obtenerPorId(id: Long): PuntoEntity?

    @Query("SELECT * FROM puntos WHERE id = :id")
    fun observarPorId(id: Long): Flow<PuntoEntity?>

    /**
     * Puntos con su estado para el jugador activo: "descubierto" es true
     * si el jugador escaneó algún QR (contenido) de ese punto.
     */
    @Query(
        """
        SELECT p.*,
            EXISTS(
                SELECT 1 FROM descubrimientos d
                INNER JOIN contenidos c ON c.id = d.contenidoId
                WHERE c.puntoId = p.id AND d.usuarioId = :usuarioId
            ) AS descubierto
        FROM puntos p
        ORDER BY p.nombre COLLATE NOCASE
        """
    )
    fun observarConEstado(usuarioId: Long): Flow<List<PuntoConEstado>>

    /**
     * "Desafío actual" del Dashboard: primer hito turístico donde el
     * jugador todavía no tiene su selfie (+100 puntos).
     */
    @Query(
        """
        SELECT * FROM puntos p
        WHERE p.esHitoTuristico = 1
          AND NOT EXISTS (SELECT 1 FROM selfies s WHERE s.puntoId = p.id AND s.usuarioId = :usuarioId)
        ORDER BY p.id
        LIMIT 1
        """
    )
    fun observarHitoPendiente(usuarioId: Long): Flow<PuntoEntity?>
}
