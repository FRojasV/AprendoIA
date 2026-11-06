package com.triskeledu.aprendoia.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.triskeledu.aprendoia.`data`.local.entity.Rol
import com.triskeledu.aprendoia.`data`.local.entity.UsuarioEntity
import com.triskeledu.aprendoia.`data`.local.relacion.JugadorResumen
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
public class UsuarioDao_Impl(
  __db: RoomDatabase,
) : UsuarioDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfUsuarioEntity: EntityInsertAdapter<UsuarioEntity>

  private val __insertAdapterOfUsuarioEntity_1: EntityInsertAdapter<UsuarioEntity>

  private val __deleteAdapterOfUsuarioEntity: EntityDeleteOrUpdateAdapter<UsuarioEntity>

  private val __updateAdapterOfUsuarioEntity: EntityDeleteOrUpdateAdapter<UsuarioEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfUsuarioEntity = object : EntityInsertAdapter<UsuarioEntity>() {
      protected override fun createQuery(): String = "INSERT OR IGNORE INTO `usuarios` (`id`,`nombre`,`alias`,`correo`,`telefono`,`password`,`direccion`,`avatar`,`rol`,`notificacionesActivas`,`fechaRegistro`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: UsuarioEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.nombre)
        statement.bindText(3, entity.alias)
        statement.bindText(4, entity.correo)
        statement.bindText(5, entity.telefono)
        statement.bindText(6, entity.password)
        statement.bindText(7, entity.direccion)
        statement.bindText(8, entity.avatar)
        statement.bindText(9, __Rol_enumToString(entity.rol))
        val _tmp: Int = if (entity.notificacionesActivas) 1 else 0
        statement.bindLong(10, _tmp.toLong())
        statement.bindLong(11, entity.fechaRegistro)
      }
    }
    this.__insertAdapterOfUsuarioEntity_1 = object : EntityInsertAdapter<UsuarioEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `usuarios` (`id`,`nombre`,`alias`,`correo`,`telefono`,`password`,`direccion`,`avatar`,`rol`,`notificacionesActivas`,`fechaRegistro`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: UsuarioEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.nombre)
        statement.bindText(3, entity.alias)
        statement.bindText(4, entity.correo)
        statement.bindText(5, entity.telefono)
        statement.bindText(6, entity.password)
        statement.bindText(7, entity.direccion)
        statement.bindText(8, entity.avatar)
        statement.bindText(9, __Rol_enumToString(entity.rol))
        val _tmp: Int = if (entity.notificacionesActivas) 1 else 0
        statement.bindLong(10, _tmp.toLong())
        statement.bindLong(11, entity.fechaRegistro)
      }
    }
    this.__deleteAdapterOfUsuarioEntity = object : EntityDeleteOrUpdateAdapter<UsuarioEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `usuarios` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: UsuarioEntity) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfUsuarioEntity = object : EntityDeleteOrUpdateAdapter<UsuarioEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `usuarios` SET `id` = ?,`nombre` = ?,`alias` = ?,`correo` = ?,`telefono` = ?,`password` = ?,`direccion` = ?,`avatar` = ?,`rol` = ?,`notificacionesActivas` = ?,`fechaRegistro` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: UsuarioEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.nombre)
        statement.bindText(3, entity.alias)
        statement.bindText(4, entity.correo)
        statement.bindText(5, entity.telefono)
        statement.bindText(6, entity.password)
        statement.bindText(7, entity.direccion)
        statement.bindText(8, entity.avatar)
        statement.bindText(9, __Rol_enumToString(entity.rol))
        val _tmp: Int = if (entity.notificacionesActivas) 1 else 0
        statement.bindLong(10, _tmp.toLong())
        statement.bindLong(11, entity.fechaRegistro)
        statement.bindLong(12, entity.id)
      }
    }
  }

  public override suspend fun insertarSiNoExisten(usuarios: List<UsuarioEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfUsuarioEntity.insert(_connection, usuarios)
  }

  public override suspend fun insertar(usuario: UsuarioEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfUsuarioEntity_1.insertAndReturnId(_connection, usuario)
    _result
  }

  public override suspend fun eliminar(usuario: UsuarioEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfUsuarioEntity.handle(_connection, usuario)
  }

  public override suspend fun actualizar(usuario: UsuarioEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfUsuarioEntity.handle(_connection, usuario)
  }

  public override suspend fun obtenerPorId(id: Long): UsuarioEntity? {
    val _sql: String = "SELECT * FROM usuarios WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfAlias: Int = getColumnIndexOrThrow(_stmt, "alias")
        val _columnIndexOfCorreo: Int = getColumnIndexOrThrow(_stmt, "correo")
        val _columnIndexOfTelefono: Int = getColumnIndexOrThrow(_stmt, "telefono")
        val _columnIndexOfPassword: Int = getColumnIndexOrThrow(_stmt, "password")
        val _columnIndexOfDireccion: Int = getColumnIndexOrThrow(_stmt, "direccion")
        val _columnIndexOfAvatar: Int = getColumnIndexOrThrow(_stmt, "avatar")
        val _columnIndexOfRol: Int = getColumnIndexOrThrow(_stmt, "rol")
        val _columnIndexOfNotificacionesActivas: Int = getColumnIndexOrThrow(_stmt, "notificacionesActivas")
        val _columnIndexOfFechaRegistro: Int = getColumnIndexOrThrow(_stmt, "fechaRegistro")
        val _result: UsuarioEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpAlias: String
          _tmpAlias = _stmt.getText(_columnIndexOfAlias)
          val _tmpCorreo: String
          _tmpCorreo = _stmt.getText(_columnIndexOfCorreo)
          val _tmpTelefono: String
          _tmpTelefono = _stmt.getText(_columnIndexOfTelefono)
          val _tmpPassword: String
          _tmpPassword = _stmt.getText(_columnIndexOfPassword)
          val _tmpDireccion: String
          _tmpDireccion = _stmt.getText(_columnIndexOfDireccion)
          val _tmpAvatar: String
          _tmpAvatar = _stmt.getText(_columnIndexOfAvatar)
          val _tmpRol: Rol
          _tmpRol = __Rol_stringToEnum(_stmt.getText(_columnIndexOfRol))
          val _tmpNotificacionesActivas: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfNotificacionesActivas).toInt()
          _tmpNotificacionesActivas = _tmp != 0
          val _tmpFechaRegistro: Long
          _tmpFechaRegistro = _stmt.getLong(_columnIndexOfFechaRegistro)
          _result = UsuarioEntity(_tmpId,_tmpNombre,_tmpAlias,_tmpCorreo,_tmpTelefono,_tmpPassword,_tmpDireccion,_tmpAvatar,_tmpRol,_tmpNotificacionesActivas,_tmpFechaRegistro)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observarPorId(id: Long): Flow<UsuarioEntity?> {
    val _sql: String = "SELECT * FROM usuarios WHERE id = ?"
    return createFlow(__db, false, arrayOf("usuarios")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfAlias: Int = getColumnIndexOrThrow(_stmt, "alias")
        val _columnIndexOfCorreo: Int = getColumnIndexOrThrow(_stmt, "correo")
        val _columnIndexOfTelefono: Int = getColumnIndexOrThrow(_stmt, "telefono")
        val _columnIndexOfPassword: Int = getColumnIndexOrThrow(_stmt, "password")
        val _columnIndexOfDireccion: Int = getColumnIndexOrThrow(_stmt, "direccion")
        val _columnIndexOfAvatar: Int = getColumnIndexOrThrow(_stmt, "avatar")
        val _columnIndexOfRol: Int = getColumnIndexOrThrow(_stmt, "rol")
        val _columnIndexOfNotificacionesActivas: Int = getColumnIndexOrThrow(_stmt, "notificacionesActivas")
        val _columnIndexOfFechaRegistro: Int = getColumnIndexOrThrow(_stmt, "fechaRegistro")
        val _result: UsuarioEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpAlias: String
          _tmpAlias = _stmt.getText(_columnIndexOfAlias)
          val _tmpCorreo: String
          _tmpCorreo = _stmt.getText(_columnIndexOfCorreo)
          val _tmpTelefono: String
          _tmpTelefono = _stmt.getText(_columnIndexOfTelefono)
          val _tmpPassword: String
          _tmpPassword = _stmt.getText(_columnIndexOfPassword)
          val _tmpDireccion: String
          _tmpDireccion = _stmt.getText(_columnIndexOfDireccion)
          val _tmpAvatar: String
          _tmpAvatar = _stmt.getText(_columnIndexOfAvatar)
          val _tmpRol: Rol
          _tmpRol = __Rol_stringToEnum(_stmt.getText(_columnIndexOfRol))
          val _tmpNotificacionesActivas: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfNotificacionesActivas).toInt()
          _tmpNotificacionesActivas = _tmp != 0
          val _tmpFechaRegistro: Long
          _tmpFechaRegistro = _stmt.getLong(_columnIndexOfFechaRegistro)
          _result = UsuarioEntity(_tmpId,_tmpNombre,_tmpAlias,_tmpCorreo,_tmpTelefono,_tmpPassword,_tmpDireccion,_tmpAvatar,_tmpRol,_tmpNotificacionesActivas,_tmpFechaRegistro)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun contarPorCorreo(correo: String, excluirId: Long): Int {
    val _sql: String = "SELECT COUNT(*) FROM usuarios WHERE correo = ? COLLATE NOCASE AND id != ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, correo)
        _argIndex = 2
        _stmt.bindLong(_argIndex, excluirId)
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

  public override fun observarJugadores(): Flow<List<JugadorResumen>> {
    val _sql: String = """
        |
        |        SELECT u.*,
        |            (COALESCE((SELECT SUM(d.puntos) FROM descubrimientos d WHERE d.usuarioId = u.id), 0) +
        |             COALESCE((SELECT SUM(s.puntos) FROM selfies s WHERE s.usuarioId = u.id), 0)) AS puntaje,
        |            (SELECT COUNT(*) FROM descubrimientos d2 WHERE d2.usuarioId = u.id) AS qrEncontrados
        |        FROM usuarios u
        |        ORDER BY CASE u.rol WHEN 'CLIENTE' THEN 0 WHEN 'ADMIN' THEN 1 ELSE 2 END,
        |                 u.nombre COLLATE NOCASE
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("descubrimientos", "selfies", "usuarios")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfNombre: Int = getColumnIndexOrThrow(_stmt, "nombre")
        val _columnIndexOfAlias: Int = getColumnIndexOrThrow(_stmt, "alias")
        val _columnIndexOfCorreo: Int = getColumnIndexOrThrow(_stmt, "correo")
        val _columnIndexOfTelefono: Int = getColumnIndexOrThrow(_stmt, "telefono")
        val _columnIndexOfPassword: Int = getColumnIndexOrThrow(_stmt, "password")
        val _columnIndexOfDireccion: Int = getColumnIndexOrThrow(_stmt, "direccion")
        val _columnIndexOfAvatar: Int = getColumnIndexOrThrow(_stmt, "avatar")
        val _columnIndexOfRol: Int = getColumnIndexOrThrow(_stmt, "rol")
        val _columnIndexOfNotificacionesActivas: Int = getColumnIndexOrThrow(_stmt, "notificacionesActivas")
        val _columnIndexOfFechaRegistro: Int = getColumnIndexOrThrow(_stmt, "fechaRegistro")
        val _columnIndexOfPuntaje: Int = getColumnIndexOrThrow(_stmt, "puntaje")
        val _columnIndexOfQrEncontrados: Int = getColumnIndexOrThrow(_stmt, "qrEncontrados")
        val _result: MutableList<JugadorResumen> = mutableListOf()
        while (_stmt.step()) {
          val _item: JugadorResumen
          val _tmpPuntaje: Int
          _tmpPuntaje = _stmt.getLong(_columnIndexOfPuntaje).toInt()
          val _tmpQrEncontrados: Int
          _tmpQrEncontrados = _stmt.getLong(_columnIndexOfQrEncontrados).toInt()
          val _tmpUsuario: UsuarioEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpNombre: String
          _tmpNombre = _stmt.getText(_columnIndexOfNombre)
          val _tmpAlias: String
          _tmpAlias = _stmt.getText(_columnIndexOfAlias)
          val _tmpCorreo: String
          _tmpCorreo = _stmt.getText(_columnIndexOfCorreo)
          val _tmpTelefono: String
          _tmpTelefono = _stmt.getText(_columnIndexOfTelefono)
          val _tmpPassword: String
          _tmpPassword = _stmt.getText(_columnIndexOfPassword)
          val _tmpDireccion: String
          _tmpDireccion = _stmt.getText(_columnIndexOfDireccion)
          val _tmpAvatar: String
          _tmpAvatar = _stmt.getText(_columnIndexOfAvatar)
          val _tmpRol: Rol
          _tmpRol = __Rol_stringToEnum(_stmt.getText(_columnIndexOfRol))
          val _tmpNotificacionesActivas: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfNotificacionesActivas).toInt()
          _tmpNotificacionesActivas = _tmp != 0
          val _tmpFechaRegistro: Long
          _tmpFechaRegistro = _stmt.getLong(_columnIndexOfFechaRegistro)
          _tmpUsuario = UsuarioEntity(_tmpId,_tmpNombre,_tmpAlias,_tmpCorreo,_tmpTelefono,_tmpPassword,_tmpDireccion,_tmpAvatar,_tmpRol,_tmpNotificacionesActivas,_tmpFechaRegistro)
          _item = JugadorResumen(_tmpUsuario,_tmpPuntaje,_tmpQrEncontrados)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observarCantidadClientes(): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM usuarios WHERE rol = 'CLIENTE'"
    return createFlow(__db, false, arrayOf("usuarios")) { _connection ->
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

  public override suspend fun obtenerIdsClientes(): List<Long> {
    val _sql: String = "SELECT id FROM usuarios WHERE rol = 'CLIENTE'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: MutableList<Long> = mutableListOf()
        while (_stmt.step()) {
          val _item: Long
          _item = _stmt.getLong(0)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  private fun __Rol_enumToString(_value: Rol): String = when (_value) {
    Rol.ADMIN -> "ADMIN"
    Rol.CREADOR -> "CREADOR"
    Rol.CLIENTE -> "CLIENTE"
  }

  private fun __Rol_stringToEnum(_value: String): Rol = when (_value) {
    "ADMIN" -> Rol.ADMIN
    "CREADOR" -> Rol.CREADOR
    "CLIENTE" -> Rol.CLIENTE
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
