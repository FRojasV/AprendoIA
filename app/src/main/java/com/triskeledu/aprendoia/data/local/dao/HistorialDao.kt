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

    @Query("SELECT * FROM historial WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    fun observarPorUsuario(usuarioId: Long): Flow<List<HistorialEntity>>
}
