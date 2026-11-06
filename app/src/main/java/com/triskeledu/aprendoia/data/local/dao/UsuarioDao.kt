package com.triskeledu.aprendoia.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.triskeledu.aprendoia.data.local.entity.UsuarioEntity
import com.triskeledu.aprendoia.data.local.relacion.JugadorResumen
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    /** IGNORE: si el id ya existe (admin/creador en duro) no lo duplica. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarSiNoExisten(usuarios: List<UsuarioEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Update
    suspend fun actualizar(usuario: UsuarioEntity)

    @Delete
    suspend fun eliminar(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun obtenerPorId(id: Long): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE id = :id")
    fun observarPorId(id: Long): Flow<UsuarioEntity?>

    @Query("SELECT COUNT(*) FROM usuarios WHERE correo = :correo COLLATE NOCASE AND id != :excluirId")
    suspend fun contarPorCorreo(correo: String, excluirId: Long = 0): Int

    /**
     * Lista para la pantalla de selección: primero los clientes y al final
     * las cuentas en duro (Administrador y Creador de Contenido).
     */
    @Query(
        """
        SELECT u.*,
            (COALESCE((SELECT SUM(d.puntos) FROM descubrimientos d WHERE d.usuarioId = u.id), 0) +
             COALESCE((SELECT SUM(s.puntos) FROM selfies s WHERE s.usuarioId = u.id), 0)) AS puntaje,
            (SELECT COUNT(*) FROM descubrimientos d2 WHERE d2.usuarioId = u.id) AS qrEncontrados
        FROM usuarios u
        ORDER BY CASE u.rol WHEN 'CLIENTE' THEN 0 WHEN 'ADMIN' THEN 1 ELSE 2 END,
                 u.nombre COLLATE NOCASE
        """
    )
    fun observarJugadores(): Flow<List<JugadorResumen>>

    @Query("SELECT COUNT(*) FROM usuarios WHERE rol = 'CLIENTE'")
    fun observarCantidadClientes(): Flow<Int>

    /** Ids de todos los clientes (para enviar notificaciones a todos). */
    @Query("SELECT id FROM usuarios WHERE rol = 'CLIENTE'")
    suspend fun obtenerIdsClientes(): List<Long>
}
