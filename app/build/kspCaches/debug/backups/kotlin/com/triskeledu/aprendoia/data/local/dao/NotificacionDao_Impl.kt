package com.triskeledu.aprendoia.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.triskeledu.aprendoia.`data`.local.entity.NotificacionEntity
import com.triskeledu.aprendoia.`data`.local.entity.TipoNotificacion
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.IllegalArgumentException
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class NotificacionDao_Impl(
  __db: RoomDatabase,
) : NotificacionDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfNotificacionEntity: EntityInsertAdapter<NotificacionEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfNotificacionEntity = object : EntityInsertAdapter<NotificacionEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `notificaciones` (`id`,`usuarioId`,`titulo`,`mensaje`,`tipo`,`destino`,`leida`,`fecha`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: NotificacionEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.usuarioId)
        statement.bindText(3, entity.titulo)
        statement.bindText(4, entity.mensaje)
        statement.bindText(5, __TipoNotificacion_enumToString(entity.tipo))
        val _tmpDestino: String? = entity.destino
        if (_tmpDestino == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpDestino)
        }
        val _tmp: Int = if (entity.leida) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        statement.bindLong(8, entity.fecha)
      }
    }
  }

  public override suspend fun insertar(notificacion: NotificacionEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfNotificacionEntity.insertAndReturnId(_connection, notificacion)
    _result
  }

  public override suspend fun insertarVarias(notificaciones: List<NotificacionEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfNotificacionEntity.insert(_connection, notificaciones)
  }

  public override fun observarPorUsuario(usuarioId: Long): Flow<List<NotificacionEntity>> {
    val _sql: String = "SELECT * FROM notificaciones WHERE usuarioId = ? ORDER BY fecha DESC"
    return createFlow(__db, false, arrayOf("notificaciones")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, usuarioId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUsuarioId: Int = getColumnIndexOrThrow(_stmt, "usuarioId")
        val _columnIndexOfTitulo: Int = getColumnIndexOrThrow(_stmt, "titulo")
        val _columnIndexOfMensaje: Int = getColumnIndexOrThrow(_stmt, "mensaje")
        val _columnIndexOfTipo: Int = getColumnIndexOrThrow(_stmt, "tipo")
        val _columnIndexOfDestino: Int = getColumnIndexOrThrow(_stmt, "destino")
        val _columnIndexOfLeida: Int = getColumnIndexOrThrow(_stmt, "leida")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _result: MutableList<NotificacionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: NotificacionEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpUsuarioId: Long
          _tmpUsuarioId = _stmt.getLong(_columnIndexOfUsuarioId)
          val _tmpTitulo: String
          _tmpTitulo = _stmt.getText(_columnIndexOfTitulo)
          val _tmpMensaje: String
          _tmpMensaje = _stmt.getText(_columnIndexOfMensaje)
          val _tmpTipo: TipoNotificacion
          _tmpTipo = __TipoNotificacion_stringToEnum(_stmt.getText(_columnIndexOfTipo))
          val _tmpDestino: String?
          if (_stmt.isNull(_columnIndexOfDestino)) {
            _tmpDestino = null
          } else {
            _tmpDestino = _stmt.getText(_columnIndexOfDestino)
          }
          val _tmpLeida: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfLeida).toInt()
          _tmpLeida = _tmp != 0
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          _item = NotificacionEntity(_tmpId,_tmpUsuarioId,_tmpTitulo,_tmpMensaje,_tmpTipo,_tmpDestino,_tmpLeida,_tmpFecha)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observarNoLeidas(usuarioId: Long): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM notificaciones WHERE usuarioId = ? AND leida = 0"
    return createFlow(__db, false, arrayOf("notificaciones")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, usuarioId)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun existe(
    usuarioId: Long,
    tipo: TipoNotificacion,
    destino: String,
  ): Boolean {
    val _sql: String = """
        |
        |        SELECT EXISTS(SELECT 1 FROM notificaciones
        |                      WHERE usuarioId = ? AND tipo = ? AND destino = ?)
        |        
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, usuarioId)
        _argIndex = 2
        _stmt.bindText(_argIndex, __TipoNotificacion_enumToString(tipo))
        _argIndex = 3
        _stmt.bindText(_argIndex, destino)
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

  public override suspend fun marcarLeida(id: Long) {
    val _sql: String = "UPDATE notificaciones SET leida = 1 WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun marcarTodasLeidas(usuarioId: Long) {
    val _sql: String = "UPDATE notificaciones SET leida = 1 WHERE usuarioId = ? AND leida = 0"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, usuarioId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun eliminar(id: Long) {
    val _sql: String = "DELETE FROM notificaciones WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  private fun __TipoNotificacion_enumToString(_value: TipoNotificacion): String = when (_value) {
    TipoNotificacion.PUNTO_CERCANO -> "PUNTO_CERCANO"
    TipoNotificacion.DESAFIO -> "DESAFIO"
    TipoNotificacion.NUEVO_VIDEO -> "NUEVO_VIDEO"
    TipoNotificacion.LOGRO -> "LOGRO"
    TipoNotificacion.EVENTO -> "EVENTO"
  }

  private fun __TipoNotificacion_stringToEnum(_value: String): TipoNotificacion = when (_value) {
    "PUNTO_CERCANO" -> TipoNotificacion.PUNTO_CERCANO
    "DESAFIO" -> TipoNotificacion.DESAFIO
    "NUEVO_VIDEO" -> TipoNotificacion.NUEVO_VIDEO
    "LOGRO" -> TipoNotificacion.LOGRO
    "EVENTO" -> TipoNotificacion.EVENTO
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
