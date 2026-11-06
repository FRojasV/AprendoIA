package com.triskeledu.aprendoia.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.triskeledu.aprendoia.`data`.local.entity.SelfieEntity
import com.triskeledu.aprendoia.`data`.local.relacion.SelfieDetalle
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class SelfieDao_Impl(
  __db: RoomDatabase,
) : SelfieDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfSelfieEntity: EntityInsertAdapter<SelfieEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfSelfieEntity = object : EntityInsertAdapter<SelfieEntity>() {
      protected override fun createQuery(): String = "INSERT OR IGNORE INTO `selfies` (`id`,`usuarioId`,`puntoId`,`rutaFoto`,`puntos`,`fecha`) VALUES (nullif(?, 0),?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SelfieEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.usuarioId)
        statement.bindLong(3, entity.puntoId)
        statement.bindText(4, entity.rutaFoto)
        statement.bindLong(5, entity.puntos.toLong())
        statement.bindLong(6, entity.fecha)
      }
    }
  }

  public override suspend fun insertar(selfie: SelfieEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfSelfieEntity.insertAndReturnId(_connection, selfie)
    _result
  }

  public override suspend fun existe(usuarioId: Long, puntoId: Long): Boolean {
    val _sql: String = "SELECT EXISTS(SELECT 1 FROM selfies WHERE usuarioId = ? AND puntoId = ?)"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, usuarioId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, puntoId)
        val _result: Boolean
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp != 0
        } else {
          _result = false
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observarExiste(usuarioId: Long, puntoId: Long): Flow<Boolean> {
    val _sql: String = "SELECT EXISTS(SELECT 1 FROM selfies WHERE usuarioId = ? AND puntoId = ?)"
    return createFlow(__db, false, arrayOf("selfies")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, usuarioId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, puntoId)
        val _result: Boolean
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp != 0
        } else {
          _result = false
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observarPorUsuario(usuarioId: Long): Flow<List<SelfieEntity>> {
    val _sql: String = "SELECT * FROM selfies WHERE usuarioId = ? ORDER BY fecha DESC"
    return createFlow(__db, false, arrayOf("selfies")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, usuarioId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUsuarioId: Int = getColumnIndexOrThrow(_stmt, "usuarioId")
        val _columnIndexOfPuntoId: Int = getColumnIndexOrThrow(_stmt, "puntoId")
        val _columnIndexOfRutaFoto: Int = getColumnIndexOrThrow(_stmt, "rutaFoto")
        val _columnIndexOfPuntos: Int = getColumnIndexOrThrow(_stmt, "puntos")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _result: MutableList<SelfieEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: SelfieEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpUsuarioId: Long
          _tmpUsuarioId = _stmt.getLong(_columnIndexOfUsuarioId)
          val _tmpPuntoId: Long
          _tmpPuntoId = _stmt.getLong(_columnIndexOfPuntoId)
          val _tmpRutaFoto: String
          _tmpRutaFoto = _stmt.getText(_columnIndexOfRutaFoto)
          val _tmpPuntos: Int
          _tmpPuntos = _stmt.getLong(_columnIndexOfPuntos).toInt()
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          _item = SelfieEntity(_tmpId,_tmpUsuarioId,_tmpPuntoId,_tmpRutaFoto,_tmpPuntos,_tmpFecha)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observarDetalle(usuarioId: Long): Flow<List<SelfieDetalle>> {
    val _sql: String = """
        |
        |        SELECT s.id AS selfieId, p.id AS puntoId, p.nombre AS puntoNombre,
        |               s.rutaFoto, s.puntos, s.fecha
        |        FROM selfies s
        |        INNER JOIN puntos p ON p.id = s.puntoId
        |        WHERE s.usuarioId = ?
        |        ORDER BY s.fecha DESC
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("selfies", "puntos")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, usuarioId)
        val _columnIndexOfSelfieId: Int = 0
        val _columnIndexOfPuntoId: Int = 1
        val _columnIndexOfPuntoNombre: Int = 2
        val _columnIndexOfRutaFoto: Int = 3
        val _columnIndexOfPuntos: Int = 4
        val _columnIndexOfFecha: Int = 5
        val _result: MutableList<SelfieDetalle> = mutableListOf()
        while (_stmt.step()) {
          val _item: SelfieDetalle
          val _tmpSelfieId: Long
          _tmpSelfieId = _stmt.getLong(_columnIndexOfSelfieId)
          val _tmpPuntoId: Long
          _tmpPuntoId = _stmt.getLong(_columnIndexOfPuntoId)
          val _tmpPuntoNombre: String
          _tmpPuntoNombre = _stmt.getText(_columnIndexOfPuntoNombre)
          val _tmpRutaFoto: String
          _tmpRutaFoto = _stmt.getText(_columnIndexOfRutaFoto)
          val _tmpPuntos: Int
          _tmpPuntos = _stmt.getLong(_columnIndexOfPuntos).toInt()
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          _item = SelfieDetalle(_tmpSelfieId,_tmpPuntoId,_tmpPuntoNombre,_tmpRutaFoto,_tmpPuntos,_tmpFecha)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
