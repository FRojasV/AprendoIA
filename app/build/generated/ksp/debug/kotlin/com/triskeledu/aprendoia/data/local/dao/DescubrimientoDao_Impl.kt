package com.triskeledu.aprendoia.`data`.local.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.getTotalChangedRows
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.triskeledu.aprendoia.`data`.local.entity.DescubrimientoEntity
import com.triskeledu.aprendoia.`data`.local.relacion.DescubrimientoDetalle
import com.triskeledu.aprendoia.`data`.local.relacion.EstadisticasJugador
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
public class DescubrimientoDao_Impl(
  __db: RoomDatabase,
) : DescubrimientoDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfDescubrimientoEntity: EntityInsertAdapter<DescubrimientoEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfDescubrimientoEntity = object : EntityInsertAdapter<DescubrimientoEntity>() {
      protected override fun createQuery(): String = "INSERT OR IGNORE INTO `descubrimientos` (`id`,`usuarioId`,`contenidoId`,`puntos`,`fecha`,`visto`,`fechaVisto`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DescubrimientoEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.usuarioId)
        statement.bindLong(3, entity.contenidoId)
        statement.bindLong(4, entity.puntos.toLong())
        statement.bindLong(5, entity.fecha)
        val _tmp: Int = if (entity.visto) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        val _tmpFechaVisto: Long? = entity.fechaVisto
        if (_tmpFechaVisto == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpFechaVisto)
        }
      }
    }
  }

  public override suspend fun insertar(descubrimiento: DescubrimientoEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfDescubrimientoEntity.insertAndReturnId(_connection, descubrimiento)
    _result
  }

  public override suspend fun obtener(usuarioId: Long, contenidoId: Long): DescubrimientoEntity? {
    val _sql: String = "SELECT * FROM descubrimientos WHERE usuarioId = ? AND contenidoId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, usuarioId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, contenidoId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUsuarioId: Int = getColumnIndexOrThrow(_stmt, "usuarioId")
        val _columnIndexOfContenidoId: Int = getColumnIndexOrThrow(_stmt, "contenidoId")
        val _columnIndexOfPuntos: Int = getColumnIndexOrThrow(_stmt, "puntos")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _columnIndexOfVisto: Int = getColumnIndexOrThrow(_stmt, "visto")
        val _columnIndexOfFechaVisto: Int = getColumnIndexOrThrow(_stmt, "fechaVisto")
        val _result: DescubrimientoEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpUsuarioId: Long
          _tmpUsuarioId = _stmt.getLong(_columnIndexOfUsuarioId)
          val _tmpContenidoId: Long
          _tmpContenidoId = _stmt.getLong(_columnIndexOfContenidoId)
          val _tmpPuntos: Int
          _tmpPuntos = _stmt.getLong(_columnIndexOfPuntos).toInt()
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          val _tmpVisto: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfVisto).toInt()
          _tmpVisto = _tmp != 0
          val _tmpFechaVisto: Long?
          if (_stmt.isNull(_columnIndexOfFechaVisto)) {
            _tmpFechaVisto = null
          } else {
            _tmpFechaVisto = _stmt.getLong(_columnIndexOfFechaVisto)
          }
          _result = DescubrimientoEntity(_tmpId,_tmpUsuarioId,_tmpContenidoId,_tmpPuntos,_tmpFecha,_tmpVisto,_tmpFechaVisto)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observar(usuarioId: Long, contenidoId: Long): Flow<DescubrimientoEntity?> {
    val _sql: String = "SELECT * FROM descubrimientos WHERE usuarioId = ? AND contenidoId = ? LIMIT 1"
    return createFlow(__db, false, arrayOf("descubrimientos")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, usuarioId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, contenidoId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUsuarioId: Int = getColumnIndexOrThrow(_stmt, "usuarioId")
        val _columnIndexOfContenidoId: Int = getColumnIndexOrThrow(_stmt, "contenidoId")
        val _columnIndexOfPuntos: Int = getColumnIndexOrThrow(_stmt, "puntos")
        val _columnIndexOfFecha: Int = getColumnIndexOrThrow(_stmt, "fecha")
        val _columnIndexOfVisto: Int = getColumnIndexOrThrow(_stmt, "visto")
        val _columnIndexOfFechaVisto: Int = getColumnIndexOrThrow(_stmt, "fechaVisto")
        val _result: DescubrimientoEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpUsuarioId: Long
          _tmpUsuarioId = _stmt.getLong(_columnIndexOfUsuarioId)
          val _tmpContenidoId: Long
          _tmpContenidoId = _stmt.getLong(_columnIndexOfContenidoId)
          val _tmpPuntos: Int
          _tmpPuntos = _stmt.getLong(_columnIndexOfPuntos).toInt()
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          val _tmpVisto: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfVisto).toInt()
          _tmpVisto = _tmp != 0
          val _tmpFechaVisto: Long?
          if (_stmt.isNull(_columnIndexOfFechaVisto)) {
            _tmpFechaVisto = null
          } else {
            _tmpFechaVisto = _stmt.getLong(_columnIndexOfFechaVisto)
          }
          _result = DescubrimientoEntity(_tmpId,_tmpUsuarioId,_tmpContenidoId,_tmpPuntos,_tmpFecha,_tmpVisto,_tmpFechaVisto)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observarDetalle(usuarioId: Long): Flow<List<DescubrimientoDetalle>> {
    val _sql: String = """
        |
        |        SELECT d.id AS descubrimientoId, c.id AS contenidoId, c.titulo, c.tema, c.url,
        |               p.id AS puntoId, p.nombre AS puntoNombre, p.esHitoTuristico, p.imagenUrl,
        |               d.puntos, d.fecha, d.visto
        |        FROM descubrimientos d
        |        INNER JOIN contenidos c ON c.id = d.contenidoId
        |        INNER JOIN puntos p ON p.id = c.puntoId
        |        WHERE d.usuarioId = ?
        |        ORDER BY d.fecha DESC
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("descubrimientos", "contenidos", "puntos")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, usuarioId)
        val _columnIndexOfDescubrimientoId: Int = 0
        val _columnIndexOfContenidoId: Int = 1
        val _columnIndexOfTitulo: Int = 2
        val _columnIndexOfTema: Int = 3
        val _columnIndexOfUrl: Int = 4
        val _columnIndexOfPuntoId: Int = 5
        val _columnIndexOfPuntoNombre: Int = 6
        val _columnIndexOfEsHitoTuristico: Int = 7
        val _columnIndexOfImagenUrl: Int = 8
        val _columnIndexOfPuntos: Int = 9
        val _columnIndexOfFecha: Int = 10
        val _columnIndexOfVisto: Int = 11
        val _result: MutableList<DescubrimientoDetalle> = mutableListOf()
        while (_stmt.step()) {
          val _item: DescubrimientoDetalle
          val _tmpDescubrimientoId: Long
          _tmpDescubrimientoId = _stmt.getLong(_columnIndexOfDescubrimientoId)
          val _tmpContenidoId: Long
          _tmpContenidoId = _stmt.getLong(_columnIndexOfContenidoId)
          val _tmpTitulo: String
          _tmpTitulo = _stmt.getText(_columnIndexOfTitulo)
          val _tmpTema: String
          _tmpTema = _stmt.getText(_columnIndexOfTema)
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpPuntoId: Long
          _tmpPuntoId = _stmt.getLong(_columnIndexOfPuntoId)
          val _tmpPuntoNombre: String
          _tmpPuntoNombre = _stmt.getText(_columnIndexOfPuntoNombre)
          val _tmpEsHitoTuristico: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfEsHitoTuristico).toInt()
          _tmpEsHitoTuristico = _tmp != 0
          val _tmpImagenUrl: String?
          if (_stmt.isNull(_columnIndexOfImagenUrl)) {
            _tmpImagenUrl = null
          } else {
            _tmpImagenUrl = _stmt.getText(_columnIndexOfImagenUrl)
          }
          val _tmpPuntos: Int
          _tmpPuntos = _stmt.getLong(_columnIndexOfPuntos).toInt()
          val _tmpFecha: Long
          _tmpFecha = _stmt.getLong(_columnIndexOfFecha)
          val _tmpVisto: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfVisto).toInt()
          _tmpVisto = _tmp_1 != 0
          _item = DescubrimientoDetalle(_tmpDescubrimientoId,_tmpContenidoId,_tmpTitulo,_tmpTema,_tmpUrl,_tmpPuntoId,_tmpPuntoNombre,_tmpEsHitoTuristico,_tmpImagenUrl,_tmpPuntos,_tmpFecha,_tmpVisto)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observarEstadisticas(usuarioId: Long): Flow<EstadisticasJugador> {
    val _sql: String = """
        |
        |        SELECT
        |            COALESCE((SELECT SUM(puntos) FROM descubrimientos WHERE usuarioId = ?), 0) AS puntosQr,
        |            COALESCE((SELECT SUM(puntos) FROM selfies WHERE usuarioId = ?), 0) AS puntosSelfies,
        |            (SELECT COUNT(*) FROM descubrimientos WHERE usuarioId = ?) AS qrEncontrados,
        |            (SELECT COUNT(*) FROM descubrimientos WHERE usuarioId = ? AND visto = 1) AS videosVistos,
        |            (SELECT COUNT(*) FROM selfies WHERE usuarioId = ?) AS selfies,
        |            (SELECT COUNT(DISTINCT c.puntoId) FROM descubrimientos d
        |                INNER JOIN contenidos c ON c.id = d.contenidoId
        |                WHERE d.usuarioId = ?) AS lugaresVisitados
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("descubrimientos", "selfies", "contenidos")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, usuarioId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, usuarioId)
        _argIndex = 3
        _stmt.bindLong(_argIndex, usuarioId)
        _argIndex = 4
        _stmt.bindLong(_argIndex, usuarioId)
        _argIndex = 5
        _stmt.bindLong(_argIndex, usuarioId)
        _argIndex = 6
        _stmt.bindLong(_argIndex, usuarioId)
        val _columnIndexOfPuntosQr: Int = 0
        val _columnIndexOfPuntosSelfies: Int = 1
        val _columnIndexOfQrEncontrados: Int = 2
        val _columnIndexOfVideosVistos: Int = 3
        val _columnIndexOfSelfies: Int = 4
        val _columnIndexOfLugaresVisitados: Int = 5
        val _result: EstadisticasJugador
        if (_stmt.step()) {
          val _tmpPuntosQr: Int
          _tmpPuntosQr = _stmt.getLong(_columnIndexOfPuntosQr).toInt()
          val _tmpPuntosSelfies: Int
          _tmpPuntosSelfies = _stmt.getLong(_columnIndexOfPuntosSelfies).toInt()
          val _tmpQrEncontrados: Int
          _tmpQrEncontrados = _stmt.getLong(_columnIndexOfQrEncontrados).toInt()
          val _tmpVideosVistos: Int
          _tmpVideosVistos = _stmt.getLong(_columnIndexOfVideosVistos).toInt()
          val _tmpSelfies: Int
          _tmpSelfies = _stmt.getLong(_columnIndexOfSelfies).toInt()
          val _tmpLugaresVisitados: Int
          _tmpLugaresVisitados = _stmt.getLong(_columnIndexOfLugaresVisitados).toInt()
          _result = EstadisticasJugador(_tmpPuntosQr,_tmpPuntosSelfies,_tmpQrEncontrados,_tmpVideosVistos,_tmpSelfies,_tmpLugaresVisitados)
        } else {
          error("The query result was empty, but expected a single row to return a NON-NULL object of type 'com.triskeledu.aprendoia.`data`.local.relacion.EstadisticasJugador'.")
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun marcarVisto(
    usuarioId: Long,
    contenidoId: Long,
    fecha: Long,
  ): Int {
    val _sql: String = """
        |
        |        UPDATE descubrimientos SET visto = 1, fechaVisto = ?
        |        WHERE usuarioId = ? AND contenidoId = ? AND visto = 0
        |        
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, fecha)
        _argIndex = 2
        _stmt.bindLong(_argIndex, usuarioId)
        _argIndex = 3
        _stmt.bindLong(_argIndex, contenidoId)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
