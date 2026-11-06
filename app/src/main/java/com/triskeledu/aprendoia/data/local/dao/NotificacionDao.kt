package com.triskeledu.aprendoia.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.triskeledu.aprendoia.data.local.entity.NotificacionEntity
import com.triskeledu.aprendoia.data.local.entity.TipoNotificacion
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificacionDao {

    @Insert
    suspend fun insertar(notificacion: NotificacionEntity): Long

    @Insert
    suspend fun insertarVarias(notificaciones: List<NotificacionEntity>)

    @Query("SELECT * FROM notificaciones WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    fun observarPorUsuario(usuarioId: Long): Flow<List<NotificacionEntity>>

    @Query("SELECT COUNT(*) FROM notificaciones WHERE usuarioId = :usuarioId AND leida = 0")
    fun observarNoLeidas(usuarioId: Long): Flow<Int>

    @Query("UPDATE notificaciones SET leida = 1 WHERE id = :id")
    suspend fun marcarLeida(id: Long)

    @Query("UPDATE notificaciones SET leida = 1 WHERE usuarioId = :usuarioId AND leida = 0")
    suspend fun marcarTodasLeidas(usuarioId: Long)

    @Query("DELETE FROM notificaciones WHERE id = :id")
    suspend fun eliminar(id: Long)

    /** Evita repetir un mismo aviso (por ejemplo, "punto cercano" del mismo lugar). */
    @Query(
        """
        SELECT EXISTS(SELECT 1 FROM notificaciones
                      WHERE usuarioId = :usuarioId AND tipo = :tipo AND destino = :destino)
        """
    )
    suspend fun existe(usuarioId: Long, tipo: TipoNotificacion, destino: String): Boolean
}
