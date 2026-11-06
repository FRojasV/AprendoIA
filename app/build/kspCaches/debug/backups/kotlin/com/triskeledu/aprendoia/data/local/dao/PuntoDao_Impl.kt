package com.triskeledu.aprendoia.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.triskeledu.aprendoia.`data`.local.entity.PuntoEntity
import com.triskeledu.aprendoia.`data`.local.relacion.PuntoConEstado
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Double
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
public class PuntoDao_Impl(
  __db: RoomDatabase,
) : PuntoDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfPuntoEntity: EntityInsertAdapter<PuntoEntity>

  private val __insertAdapterOfPuntoEntity_1: EntityInsertAdapter<PuntoEntity>

  private val __deleteAdapterOfPuntoEntity: EntityDeleteOrUpdateAdapter<PuntoEntity>

  private val __updateAdapterOfPuntoEntity: EntityDeleteOrUpdateAdapter<PuntoEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfPuntoEntity = object : EntityInsertAdapter<PuntoEntity>() {
      protected override fun createQuery(): String = "INSERT OR IGNORE INTO `puntos` (`id`,`nombre`,`descripcion`,`infoTuristica`,`latitud`,`longitud`,`esHitoTuristico`,`imagenUrl`,`fechaCreacion`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: PuntoEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.nombre)
        statement.bindText(3, entity.descripcion)
        statement.bindText(4, entity.infoTuristica)
        statement.bindDouble(5, entity.latitud)
        statement.bindDouble(6, entity.longitud)
        val _tmp: Int = if (entity.esHitoTuristico) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        val _tmpImagenUrl: String? = entity.imagenUrl
        if (_tmpImagenUrl == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpImagenUrl)
        }
        statement.bindLong(9, entity.fechaCreacion)
      }
    }
    this.__insertAdapterOfPuntoEntity_1 = object : EntityInsertAdapter<PuntoEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `puntos` (`id`,`nombre`,`descripcion`,`infoTuristica`,`latitud`,`longitud`,`esHitoTuristico`,`imagenUrl`,`fechaCreacion`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: PuntoEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.nombre)
        statement.bindText(3, entity.descripcion)
        statement.bindText(4, entity.infoTuristica)
        statement.bindDouble(5, entity.latitud)
        statement.bindDouble(6, entity.longitud)
        val _tmp: Int = if (entity.esHitoTuristico) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        val _tmpImagenUrl: String? = entity.imagenUrl
        if (_tmpImagenUrl == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpImagenUrl)
        }
        statement.bindLong(9, entity.fechaCreacion)
      }
    }
    this.__deleteAdapterOfPuntoEntity = object : EntityDeleteOrUpdateAdapter<PuntoEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `puntos` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: PuntoEntity) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfPuntoEntity = object : EntityDeleteOrUpdateAdapter<PuntoEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `puntos` SET `id` = ?,`nombre` = ?,`descripcion` = ?,`infoTuristica` = ?,`latitud` = ?,`longitud` = ?,`esHitoTuristico` = ?,`imagenUrl` = ?,`fechaCreacion` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: PuntoEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.nombre)
        statement.bindText(3, entity.descripcion)
        statement.bindText(4, entity.infoTuristica)
        statement.bindDouble(5, entity.latitud)
        statement.bindDouble(6, entity.longitud)
        val _tmp: Int = if (entity.esHitoTuristico) 1 else 0
        statement.bindLong(7, _tmp.toLong())
        val _tmpImagenUrl: String? = entity.imagenUrl
        if (_tmpImagenUrl == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpImagenUrl)
        }
        statement.bindLong(9, entity.fechaCreacion)
        statement.bindLong(10, entity.id)
      }
    }
  }

  public override suspend fun insertarSiNoExisten(puntos: List<PuntoEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfPuntoEntity.insert(_connection, puntos)
  }

  public override suspend fun insertar(punto: PuntoEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfPuntoEntity_1.insertAndReturnId(_connection, punto)
    _result
  }

  public override suspend fun eliminar(punto: PuntoEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfPuntoEntity.handle(_connection, punto)
  }

  public override suspend fun actualizar(punto: PuntoEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfPuntoEntity.handle(_connection, punto)
  }

  public override suspend fun contar(): Int {
    val _sql: String = "SELECT COUNT(*) FROM puntos"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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

  public override fun observarTotal(): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM puntos"
    return createFlow(__db, false, arrayOf("puntos")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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

  public override fun observarTodos(): Flow<List<PuntoEntity>> {
    val _sql: String = "SELECT * FROM puntos ORDER BY nombre COLLATE NOCASE"
    return createFlow(__db, false, arrayOf("puntos")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfDescripcion: Int = getColumnIndexOrThrow(_stmt, "descripcion")
        val _columnIndexOfInfoTuristica: Int = getColumnIndexOrThrow(_stmt, "infoTuristica")
        val _columnIndexOfLatitud: Int = getColumnIndexOrThrow(_stmt, "latitud")
        val _columnIndexOfLongitud: Int = getColumnIndexOrThrow(_stmt, "longitud")
        val _columnIndexOfEsHitoTuristico: Int = getColumnIndexOrThrow(_stmt, "esHitoTuristico")
        val _columnIndexOfImagenUrl: Int = getColumnIndexOrThrow(_stmt, "imagenUrl")
        val _columnIndexOfFechaCreacion: Int = getColumnIndexOrThrow(_stmt, "fechaCreacion")
        val _result: MutableList<PuntoEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: PuntoEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpDescripcion: String
          _tmpDescripcion = _stmt.getText(_columnIndexOfDescripcion)
          val _tmpInfoTuristica: String
          _tmpInfoTuristica = _stmt.getText(_columnIndexOfInfoTuristica)
          val _tmpLatitud: Double
          _tmpLatitud = _stmt.getDouble(_columnIndexOfLatitud)
          val _tmpLongitud: Double
          _tmpLongitud = _stmt.getDouble(_columnIndexOfLongitud)
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
          val _tmpFechaCreacion: Long
          _tmpFechaCreacion = _stmt.getLong(_columnIndexOfFechaCreacion)
          _item = PuntoEntity(_tmpId,_tmpNombre,_tmpDescripcion,_tmpInfoTuristica,_tmpLatitud,_tmpLongitud,_tmpEsHitoTuristico,_tmpImagenUrl,_tmpFechaCreacion)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun obtenerPorId(id: Long): PuntoEntity? {
    val _sql: String = "SELECT * FROM puntos WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfDescripcion: Int = getColumnIndexOrThrow(_stmt, "descripcion")
        val _columnIndexOfInfoTuristica: Int = getColumnIndexOrThrow(_stmt, "infoTuristica")
        val _columnIndexOfLatitud: Int = getColumnIndexOrThrow(_stmt, "latitud")
        val _columnIndexOfLongitud: Int = getColumnIndexOrThrow(_stmt, "longitud")
        val _columnIndexOfEsHitoTuristico: Int = getColumnIndexOrThrow(_stmt, "esHitoTuristico")
        val _columnIndexOfImagenUrl: Int = getColumnIndexOrThrow(_stmt, "imagenUrl")
        val _columnIndexOfFechaCreacion: Int = getColumnIndexOrThrow(_stmt, "fechaCreacion")
        val _result: PuntoEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpDescripcion: String
          _tmpDescripcion = _stmt.getText(_columnIndexOfDescripcion)
          val _tmpInfoTuristica: String
          _tmpInfoTuristica = _stmt.getText(_columnIndexOfInfoTuristica)
          val _tmpLatitud: Double
          _tmpLatitud = _stmt.getDouble(_columnIndexOfLatitud)
          val _tmpLongitud: Double
          _tmpLongitud = _stmt.getDouble(_columnIndexOfLongitud)
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
          val _tmpFechaCreacion: Long
          _tmpFechaCreacion = _stmt.getLong(_columnIndexOfFechaCreacion)
          _result = PuntoEntity(_tmpId,_tmpNombre,_tmpDescripcion,_tmpInfoTuristica,_tmpLatitud,_tmpLongitud,_tmpEsHitoTuristico,_tmpImagenUrl,_tmpFechaCreacion)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observarPorId(id: Long): Flow<PuntoEntity?> {
    val _sql: String = "SELECT * FROM puntos WHERE id = ?"
    return createFlow(__db, false, arrayOf("puntos")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfDescripcion: Int = getColumnIndexOrThrow(_stmt, "descripcion")
        val _columnIndexOfInfoTuristica: Int = getColumnIndexOrThrow(_stmt, "infoTuristica")
        val _columnIndexOfLatitud: Int = getColumnIndexOrThrow(_stmt, "latitud")
        val _columnIndexOfLongitud: Int = getColumnIndexOrThrow(_stmt, "longitud")
        val _columnIndexOfEsHitoTuristico: Int = getColumnIndexOrThrow(_stmt, "esHitoTuristico")
        val _columnIndexOfImagenUrl: Int = getColumnIndexOrThrow(_stmt, "imagenUrl")
        val _columnIndexOfFechaCreacion: Int = getColumnIndexOrThrow(_stmt, "fechaCreacion")
        val _result: PuntoEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpDescripcion: String
          _tmpDescripcion = _stmt.getText(_columnIndexOfDescripcion)
          val _tmpInfoTuristica: String
          _tmpInfoTuristica = _stmt.getText(_columnIndexOfInfoTuristica)
          val _tmpLatitud: Double
          _tmpLatitud = _stmt.getDouble(_columnIndexOfLatitud)
          val _tmpLongitud: Double
          _tmpLongitud = _stmt.getDouble(_columnIndexOfLongitud)
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
          val _tmpFechaCreacion: Long
          _tmpFechaCreacion = _stmt.getLong(_columnIndexOfFechaCreacion)
          _result = PuntoEntity(_tmpId,_tmpNombre,_tmpDescripcion,_tmpInfoTuristica,_tmpLatitud,_tmpLongitud,_tmpEsHitoTuristico,_tmpImagenUrl,_tmpFechaCreacion)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observarConEstado(usuarioId: Long): Flow<List<PuntoConEstado>> {
    val _sql: String = """
        |
        |        SELECT p.*,
        |            EXISTS(
        |                SELECT 1 FROM descubrimientos d
        |                INNER JOIN contenidos c ON c.id = d.contenidoId
        |                WHERE c.puntoId = p.id AND d.usuarioId = ?
        |            ) AS descubierto
        |        FROM puntos p
        |        ORDER BY p.nombre COLLATE NOCASE
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("descubrimientos", "contenidos", "puntos")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, usuarioId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfDescripcion: Int = getColumnIndexOrThrow(_stmt, "descripcion")
        val _columnIndexOfInfoTuristica: Int = getColumnIndexOrThrow(_stmt, "infoTuristica")
        val _columnIndexOfLatitud: Int = getColumnIndexOrThrow(_stmt, "latitud")
        val _columnIndexOfLongitud: Int = getColumnIndexOrThrow(_stmt, "longitud")
        val _columnIndexOfEsHitoTuristico: Int = getColumnIndexOrThrow(_stmt, "esHitoTuristico")
        val _columnIndexOfImagenUrl: Int = getColumnIndexOrThrow(_stmt, "imagenUrl")
        val _columnIndexOfFechaCreacion: Int = getColumnIndexOrThrow(_stmt, "fechaCreacion")
        val _columnIndexOfDescubierto: Int = getColumnIndexOrThrow(_stmt, "descubierto")
        val _result: MutableList<PuntoConEstado> = mutableListOf()
        while (_stmt.step()) {
          val _item: PuntoConEstado
          val _tmpDescubierto: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDescubierto).toInt()
          _tmpDescubierto = _tmp != 0
          val _tmpPunto: PuntoEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpDescripcion: String
          _tmpDescripcion = _stmt.getText(_columnIndexOfDescripcion)
          val _tmpInfoTuristica: String
          _tmpInfoTuristica = _stmt.getText(_columnIndexOfInfoTuristica)
          val _tmpLatitud: Double
          _tmpLatitud = _stmt.getDouble(_columnIndexOfLatitud)
          val _tmpLongitud: Double
          _tmpLongitud = _stmt.getDouble(_columnIndexOfLongitud)
          val _tmpEsHitoTuristico: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfEsHitoTuristico).toInt()
          _tmpEsHitoTuristico = _tmp_1 != 0
          val _tmpImagenUrl: String?
          if (_stmt.isNull(_columnIndexOfImagenUrl)) {
            _tmpImagenUrl = null
          } else {
            _tmpImagenUrl = _stmt.getText(_columnIndexOfImagenUrl)
          }
          val _tmpFechaCreacion: Long
          _tmpFechaCreacion = _stmt.getLong(_columnIndexOfFechaCreacion)
          _tmpPunto = PuntoEntity(_tmpId,_tmpNombre,_tmpDescripcion,_tmpInfoTuristica,_tmpLatitud,_tmpLongitud,_tmpEsHitoTuristico,_tmpImagenUrl,_tmpFechaCreacion)
          _item = PuntoConEstado(_tmpPunto,_tmpDescubierto)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observarHitoPendiente(usuarioId: Long): Flow<PuntoEntity?> {
    val _sql: String = """
        |
        |        SELECT * FROM puntos p
        |        WHERE p.esHitoTuristico = 1
        |          AND NOT EXISTS (SELECT 1 FROM selfies s WHERE s.puntoId = p.id AND s.usuarioId = ?)
        |        ORDER BY p.id
        |        LIMIT 1
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("puntos", "selfies")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, usuarioId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfDescripcion: Int = getColumnIndexOrThrow(_stmt, "descripcion")
        val _columnIndexOfInfoTuristica: Int = getColumnIndexOrThrow(_stmt, "infoTuristica")
        val _columnIndexOfLatitud: Int = getColumnIndexOrThrow(_stmt, "latitud")
        val _columnIndexOfLongitud: Int = getColumnIndexOrThrow(_stmt, "longitud")
        val _columnIndexOfEsHitoTuristico: Int = getColumnIndexOrThrow(_stmt, "esHitoTuristico")
        val _columnIndexOfImagenUrl: Int = getColumnIndexOrThrow(_stmt, "imagenUrl")
        val _columnIndexOfFechaCreacion: Int = getColumnIndexOrThrow(_stmt, "fechaCreacion")
        val _result: PuntoEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpDescripcion: String
          _tmpDescripcion = _stmt.getText(_columnIndexOfDescripcion)
          val _tmpInfoTuristica: String
          _tmpInfoTuristica = _stmt.getText(_columnIndexOfInfoTuristica)
          val _tmpLatitud: Double
          _tmpLatitud = _stmt.getDouble(_columnIndexOfLatitud)
          val _tmpLongitud: Double
          _tmpLongitud = _stmt.getDouble(_columnIndexOfLongitud)
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
          val _tmpFechaCreacion: Long
          _tmpFechaCreacion = _stmt.getLong(_columnIndexOfFechaCreacion)
          _result = PuntoEntity(_tmpId,_tmpNombre,_tmpDescripcion,_tmpInfoTuristica,_tmpLatitud,_tmpLongitud,_tmpEsHitoTuristico,_tmpImagenUrl,_tmpFechaCreacion)
        } else {
          _result = null
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
