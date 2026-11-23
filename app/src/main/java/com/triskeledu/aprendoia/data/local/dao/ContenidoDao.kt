package com.triskeledu.aprendoia.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.triskeledu.aprendoia.data.local.entity.ContenidoEntity
import com.triskeledu.aprendoia.data.local.relacion.CapsulaGestion
import com.triskeledu.aprendoia.data.local.relacion.ContenidoConEstado
import kotlinx.coroutines.flow.Flow

@Dao
interface ContenidoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarSiNoExisten(contenidos: List<ContenidoEntity>)

    @Insert
    suspend fun insertar(contenido: ContenidoEntity): Long

    @Update
    suspend fun actualizar(contenido: ContenidoEntity)

    @Delete
    suspend fun eliminar(contenido: ContenidoEntity)

    // ---------- Sincronización con ms-contenidos ----------

    @Upsert
    suspend fun guardarTodos(contenidos: List<ContenidoEntity>)

    @Upsert
    suspend fun guardar(contenido: ContenidoEntity)

    @Query("DELETE FROM contenidos WHERE id NOT IN (:ids)")
    suspend fun eliminarExcepto(ids: List<Long>)

    @Query("DELETE FROM contenidos WHERE id = :id")
    suspend fun eliminarPorId(id: Long)

    @Query("SELECT * FROM contenidos ORDER BY titulo COLLATE NOCASE")
    fun observarTodos(): Flow<List<ContenidoEntity>>

    @Query("SELECT * FROM contenidos WHERE puntoId = :puntoId ORDER BY titulo COLLATE NOCASE")
    fun observarPorPunto(puntoId: Long): Flow<List<ContenidoEntity>>

    @Query("SELECT * FROM contenidos WHERE id = :id")
    suspend fun obtenerPorId(id: Long): ContenidoEntity?

    /** Gestión: todas las cápsulas (publicadas y pendientes) con el nombre del punto. */
    @Query(
        """
        SELECT c.*, p.nombre AS puntoNombre
        FROM contenidos c
        INNER JOIN puntos p ON p.id = c.puntoId
        ORDER BY c.publicado ASC, c.titulo COLLATE NOCASE
        """
    )
    fun observarParaGestion(): Flow<List<CapsulaGestion>>

    @Query("SELECT COUNT(*) FROM contenidos")
    fun observarTotal(): Flow<Int>

    @Query("SELECT COUNT(*) FROM contenidos WHERE publicado = 0")
    fun observarPendientes(): Flow<Int>

    /** Total de cápsulas publicadas (para "5 de 8 cápsulas"). */
    @Query("SELECT COUNT(*) FROM contenidos WHERE publicado = 1")
    fun observarTotalPublicados(): Flow<Int>

    /**
     * Cápsulas publicadas de un punto, indicando si el jugador ya escaneó su QR
     * (descubierto) y si vio el video (visto). Pantalla 6.
     */
    @Query(
        """
        SELECT c.*,
            EXISTS(SELECT 1 FROM descubrimientos d
                   WHERE d.contenidoId = c.id AND d.usuarioId = :usuarioId) AS descubierto,
            EXISTS(SELECT 1 FROM descubrimientos d
                   WHERE d.contenidoId = c.id AND d.usuarioId = :usuarioId AND d.visto = 1) AS visto
        FROM contenidos c
        WHERE c.puntoId = :puntoId AND c.publicado = 1
        ORDER BY c.titulo COLLATE NOCASE
        """
    )
    fun observarPorPuntoConEstado(puntoId: Long, usuarioId: Long): Flow<List<ContenidoConEstado>>

    /** Usado por el escáner: busca el contenido a partir del texto del QR. */
    @Query("SELECT * FROM contenidos WHERE codigoQr = :codigo AND publicado = 1 LIMIT 1")
    suspend fun buscarPorCodigoQr(codigo: String): ContenidoEntity?

    @Query("SELECT COUNT(*) FROM contenidos WHERE codigoQr = :codigo AND id != :excluirId")
    suspend fun contarPorCodigoQr(codigo: String, excluirId: Long = 0): Int
}
