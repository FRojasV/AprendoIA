package com.triskeledu.aprendoia.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.triskeledu.aprendoia.data.local.entity.SelfieEntity
import com.triskeledu.aprendoia.data.local.relacion.SelfieDetalle
import kotlinx.coroutines.flow.Flow

@Dao
interface SelfieDao {

    /** Devuelve -1 si el jugador ya tenía la selfie de ese punto. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(selfie: SelfieEntity): Long

    @Query("SELECT EXISTS(SELECT 1 FROM selfies WHERE usuarioId = :usuarioId AND puntoId = :puntoId)")
    suspend fun existe(usuarioId: Long, puntoId: Long): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM selfies WHERE usuarioId = :usuarioId AND puntoId = :puntoId)")
    fun observarExiste(usuarioId: Long, puntoId: Long): Flow<Boolean>

    @Query("SELECT * FROM selfies WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    fun observarPorUsuario(usuarioId: Long): Flow<List<SelfieEntity>>

    /** Selfies del jugador con el nombre del lugar (pantalla 11). */
    @Query(
        """
        SELECT s.id AS selfieId, p.id AS puntoId, p.nombre AS puntoNombre,
               s.rutaFoto, s.puntos, s.fecha
        FROM selfies s
        INNER JOIN puntos p ON p.id = s.puntoId
        WHERE s.usuarioId = :usuarioId
        ORDER BY s.fecha DESC
        """
    )
    fun observarDetalle(usuarioId: Long): Flow<List<SelfieDetalle>>
}
