package com.triskeledu.aprendoia.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.triskeledu.aprendoia.data.local.entity.HistorialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistorialDao {

    @Insert
    suspend fun insertar(actividad: HistorialEntity): Long

    @Insert
    suspend fun insertarVarios(actividades: List<HistorialEntity>)

    @Query("SELECT * FROM historial WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    fun observarPorUsuario(usuarioId: Long): Flow<List<HistorialEntity>>

    @Query("SELECT COUNT(*) FROM historial WHERE usuarioId = :usuarioId")
    suspend fun contar(usuarioId: Long): Int

    /** Al sincronizar, el historial del servidor reemplaza al local. */
    @Query("DELETE FROM historial WHERE usuarioId = :usuarioId")
    suspend fun eliminarDeUsuario(usuarioId: Long)
}
