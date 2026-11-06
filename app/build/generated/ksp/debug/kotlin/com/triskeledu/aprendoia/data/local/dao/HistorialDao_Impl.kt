package com.triskeledu.aprendoia.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.triskeledu.aprendoia.`data`.local.entity.HistorialEntity
import com.triskeledu.aprendoia.`data`.local.entity.TipoActividad
import javax.`annotation`.processing.Generated
import kotlin.IllegalArgumentException
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
public class HistorialDao_Impl(
  __db: RoomDatabase,
) : HistorialDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfHistorialEntity: EntityInsertAdapter<HistorialEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfHistorialEntity = object : EntityInsertAdapter<HistorialEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `historial` (`id`,`usuarioId`,`tipo`,`titulo`,`detalle`,`puntos`,`fecha`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: HistorialEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.usuarioId)
        statement.bindText(3, __TipoActividad_enumToString(entity.tipo))
        statement.bindText(4, entity.titulo)
        statement.bindText(5, entity.detalle)
        statement.bindLong(6, entity.puntos.toLong())
        statement.bindLong(7, entity.fecha)
      }
    }
  }

  public override suspend fun insertar(actividad: HistorialEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfHistorialEntity.insertAndReturnId(_connection, actividad)
    _result
  }

  public override fun observarPorUsuario(usuarioId: Long): Flow<List<HistorialEntity>> {
    val _sql: String = "SELECT * FROM historial WHERE usuarioId = ? ORDER BY fecha DESC"
    return createFlow(__db, false, arrayOf("historial")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, usuarioId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUsuarioId: Int = getColumnIndexOrThrow(_stmt, "usuarioId")
        val _columnIndexOfTipo: Int = getColumnIndexOrThrow(_stmt, "tipo")
        val _columnIndexOfTitulo: Int = getColumnIndexOrThrow(_stmt, "titulo")
        val _columnIndexOfDetalle: Int = getColumnIndexOrThrow(_stmt, "detalle")
        val _columnIndexOfPuntos: Int = getColumnIndexOrThrow(_stmt, "puntos")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _result: MutableList<HistorialEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: HistorialEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpUsuarioId: Long
          _tmpUsuarioId = _stmt.getLong(_columnIndexOfUsuarioId)
          val _tmpTipo: TipoActividad
          _tmpTipo = __TipoActividad_stringToEnum(_stmt.getText(_columnIndexOfTipo))
          val _tmpTitulo: String
          _tmpTitulo = _stmt.getText(_columnIndexOfTitulo)
          val _tmpDetalle: String
          _tmpDetalle = _stmt.getText(_columnIndexOfDetalle)
          val _tmpPuntos: Int
          _tmpPuntos = _stmt.getLong(_columnIndexOfPuntos).toInt()
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          _item = HistorialEntity(_tmpId,_tmpUsuarioId,_tmpTipo,_tmpTitulo,_tmpDetalle,_tmpPuntos,_tmpFecha)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  private fun __TipoActividad_enumToString(_value: TipoActividad): String = when (_value) {
    TipoActividad.QR -> "QR"
    TipoActividad.VIDEO -> "VIDEO"
    TipoActividad.SELFIE -> "SELFIE"
    TipoActividad.VISITA -> "VISITA"
  }

  private fun __TipoActividad_stringToEnum(_value: String): TipoActividad = when (_value) {
    "QR" -> TipoActividad.QR
    "VIDEO" -> TipoActividad.VIDEO
    "SELFIE" -> TipoActividad.SELFIE
    "VISITA" -> TipoActividad.VISITA
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
