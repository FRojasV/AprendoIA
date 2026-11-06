package com.triskeledu.aprendoia.`data`.local.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.triskeledu.aprendoia.`data`.local.entity.ContenidoEntity
import com.triskeledu.aprendoia.`data`.local.relacion.CapsulaGestion
import com.triskeledu.aprendoia.`data`.local.relacion.ContenidoConEstado
import javax.`annotation`.processing.Generated
import kotlin.Boolean
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
public class ContenidoDao_Impl(
  __db: RoomDatabase,
) : ContenidoDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfContenidoEntity: EntityInsertAdapter<ContenidoEntity>

  private val __insertAdapterOfContenidoEntity_1: EntityInsertAdapter<ContenidoEntity>

  private val __deleteAdapterOfContenidoEntity: EntityDeleteOrUpdateAdapter<ContenidoEntity>

  private val __updateAdapterOfContenidoEntity: EntityDeleteOrUpdateAdapter<ContenidoEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfContenidoEntity = object : EntityInsertAdapter<ContenidoEntity>() {
      protected override fun createQuery(): String = "INSERT OR IGNORE INTO `contenidos` (`id`,`puntoId`,`titulo`,`tema`,`autor`,`url`,`duracion`,`descripcion`,`codigoQr`,`puntos`,`publicado`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ContenidoEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.puntoId)
        statement.bindText(3, entity.titulo)
        statement.bindText(4, entity.tema)
        statement.bindText(5, entity.autor)
        statement.bindText(6, entity.url)
        statement.bindText(7, entity.duracion)
        statement.bindText(8, entity.descripcion)
        statement.bindText(9, entity.codigoQr)
        statement.bindLong(10, entity.puntos.toLong())
        val _tmp: Int = if (entity.publicado) 1 else 0
        statement.bindLong(11, _tmp.toLong())
      }
    }
    this.__insertAdapterOfContenidoEntity_1 = object : EntityInsertAdapter<ContenidoEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `contenidos` (`id`,`puntoId`,`titulo`,`tema`,`autor`,`url`,`duracion`,`descripcion`,`codigoQr`,`puntos`,`publicado`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ContenidoEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.puntoId)
        statement.bindText(3, entity.titulo)
        statement.bindText(4, entity.tema)
        statement.bindText(5, entity.autor)
        statement.bindText(6, entity.url)
        statement.bindText(7, entity.duracion)
        statement.bindText(8, entity.descripcion)
        statement.bindText(9, entity.codigoQr)
        statement.bindLong(10, entity.puntos.toLong())
        val _tmp: Int = if (entity.publicado) 1 else 0
        statement.bindLong(11, _tmp.toLong())
      }
    }
    this.__deleteAdapterOfContenidoEntity = object : EntityDeleteOrUpdateAdapter<ContenidoEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `contenidos` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ContenidoEntity) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfContenidoEntity = object : EntityDeleteOrUpdateAdapter<ContenidoEntity>() {
      protected override fun createQuery(): String = "UPDATE OR ABORT `contenidos` SET `id` = ?,`puntoId` = ?,`titulo` = ?,`tema` = ?,`autor` = ?,`url` = ?,`duracion` = ?,`descripcion` = ?,`codigoQr` = ?,`puntos` = ?,`publicado` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ContenidoEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.puntoId)
        statement.bindText(3, entity.titulo)
        statement.bindText(4, entity.tema)
        statement.bindText(5, entity.autor)
        statement.bindText(6, entity.url)
        statement.bindText(7, entity.duracion)
        statement.bindText(8, entity.descripcion)
        statement.bindText(9, entity.codigoQr)
        statement.bindLong(10, entity.puntos.toLong())
        val _tmp: Int = if (entity.publicado) 1 else 0
        statement.bindLong(11, _tmp.toLong())
        statement.bindLong(12, entity.id)
      }
    }
  }

  public override suspend fun insertarSiNoExisten(contenidos: List<ContenidoEntity>): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfContenidoEntity.insert(_connection, contenidos)
  }

  public override suspend fun insertar(contenido: ContenidoEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfContenidoEntity_1.insertAndReturnId(_connection, contenido)
    _result
  }

  public override suspend fun eliminar(contenido: ContenidoEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfContenidoEntity.handle(_connection, contenido)
  }

  public override suspend fun actualizar(contenido: ContenidoEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfContenidoEntity.handle(_connection, contenido)
  }

  public override fun observarTodos(): Flow<List<ContenidoEntity>> {
    val _sql: String = "SELECT * FROM contenidos ORDER BY titulo COLLATE NOCASE"
    return createFlow(__db, false, arrayOf("contenidos")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPuntoId: Int = getColumnIndexOrThrow(_stmt, "puntoId")
        val _columnIndexOfTitulo: Int = getColumnIndexOrThrow(_stmt, "titulo")
        val _columnIndexOfTema: Int = getColumnIndexOrThrow(_stmt, "tema")
        val _columnIndexOfAutor: Int = getColumnIndexOrThrow(_stmt, "autor")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfDuracion: Int = getColumnIndexOrThrow(_stmt, "duracion")
        val _columnIndexOfDescripcion: Int = getColumnIndexOrThrow(_stmt, "descripcion")
        val _columnIndexOfCodigoQr: Int = getColumnIndexOrThrow(_stmt, "codigoQr")
        val _columnIndexOfPuntos: Int = getColumnIndexOrThrow(_stmt, "puntos")
        val _columnIndexOfPublicado: Int = getColumnIndexOrThrow(_stmt, "publicado")
        val _result: MutableList<ContenidoEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ContenidoEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPuntoId: Long
          _tmpPuntoId = _stmt.getLong(_columnIndexOfPuntoId)
          val _tmpTitulo: String
          _tmpTitulo = _stmt.getText(_columnIndexOfTitulo)
          val _tmpTema: String
          _tmpTema = _stmt.getText(_columnIndexOfTema)
          val _tmpAutor: String
          _tmpAutor = _stmt.getText(_columnIndexOfAutor)
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpDuracion: String
          _tmpDuracion = _stmt.getText(_columnIndexOfDuracion)
          val _tmpDescripcion: String
          _tmpDescripcion = _stmt.getText(_columnIndexOfDescripcion)
          val _tmpCodigoQr: String
          _tmpCodigoQr = _stmt.getText(_columnIndexOfCodigoQr)
          val _tmpPuntos: Int
          _tmpPuntos = _stmt.getLong(_columnIndexOfPuntos).toInt()
          val _tmpPublicado: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfPublicado).toInt()
          _tmpPublicado = _tmp != 0
          _item = ContenidoEntity(_tmpId,_tmpPuntoId,_tmpTitulo,_tmpTema,_tmpAutor,_tmpUrl,_tmpDuracion,_tmpDescripcion,_tmpCodigoQr,_tmpPuntos,_tmpPublicado)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observarPorPunto(puntoId: Long): Flow<List<ContenidoEntity>> {
    val _sql: String = "SELECT * FROM contenidos WHERE puntoId = ? ORDER BY titulo COLLATE NOCASE"
    return createFlow(__db, false, arrayOf("contenidos")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, puntoId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPuntoId: Int = getColumnIndexOrThrow(_stmt, "puntoId")
        val _columnIndexOfTitulo: Int = getColumnIndexOrThrow(_stmt, "titulo")
        val _columnIndexOfTema: Int = getColumnIndexOrThrow(_stmt, "tema")
        val _columnIndexOfAutor: Int = getColumnIndexOrThrow(_stmt, "autor")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfDuracion: Int = getColumnIndexOrThrow(_stmt, "duracion")
        val _columnIndexOfDescripcion: Int = getColumnIndexOrThrow(_stmt, "descripcion")
        val _columnIndexOfCodigoQr: Int = getColumnIndexOrThrow(_stmt, "codigoQr")
        val _columnIndexOfPuntos: Int = getColumnIndexOrThrow(_stmt, "puntos")
        val _columnIndexOfPublicado: Int = getColumnIndexOrThrow(_stmt, "publicado")
        val _result: MutableList<ContenidoEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ContenidoEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPuntoId: Long
          _tmpPuntoId = _stmt.getLong(_columnIndexOfPuntoId)
          val _tmpTitulo: String
          _tmpTitulo = _stmt.getText(_columnIndexOfTitulo)
          val _tmpTema: String
          _tmpTema = _stmt.getText(_columnIndexOfTema)
          val _tmpAutor: String
          _tmpAutor = _stmt.getText(_columnIndexOfAutor)
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpDuracion: String
          _tmpDuracion = _stmt.getText(_columnIndexOfDuracion)
          val _tmpDescripcion: String
          _tmpDescripcion = _stmt.getText(_columnIndexOfDescripcion)
          val _tmpCodigoQr: String
          _tmpCodigoQr = _stmt.getText(_columnIndexOfCodigoQr)
          val _tmpPuntos: Int
          _tmpPuntos = _stmt.getLong(_columnIndexOfPuntos).toInt()
          val _tmpPublicado: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfPublicado).toInt()
          _tmpPublicado = _tmp != 0
          _item = ContenidoEntity(_tmpId,_tmpPuntoId,_tmpTitulo,_tmpTema,_tmpAutor,_tmpUrl,_tmpDuracion,_tmpDescripcion,_tmpCodigoQr,_tmpPuntos,_tmpPublicado)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun obtenerPorId(id: Long): ContenidoEntity? {
    val _sql: String = "SELECT * FROM contenidos WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPuntoId: Int = getColumnIndexOrThrow(_stmt, "puntoId")
        val _columnIndexOfTitulo: Int = getColumnIndexOrThrow(_stmt, "titulo")
        val _columnIndexOfTema: Int = getColumnIndexOrThrow(_stmt, "tema")
        val _columnIndexOfAutor: Int = getColumnIndexOrThrow(_stmt, "autor")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfDuracion: Int = getColumnIndexOrThrow(_stmt, "duracion")
        val _columnIndexOfDescripcion: Int = getColumnIndexOrThrow(_stmt, "descripcion")
        val _columnIndexOfCodigoQr: Int = getColumnIndexOrThrow(_stmt, "codigoQr")
        val _columnIndexOfPuntos: Int = getColumnIndexOrThrow(_stmt, "puntos")
        val _columnIndexOfPublicado: Int = getColumnIndexOrThrow(_stmt, "publicado")
        val _result: ContenidoEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPuntoId: Long
          _tmpPuntoId = _stmt.getLong(_columnIndexOfPuntoId)
          val _tmpTitulo: String
          _tmpTitulo = _stmt.getText(_columnIndexOfTitulo)
          val _tmpTema: String
          _tmpTema = _stmt.getText(_columnIndexOfTema)
          val _tmpAutor: String
          _tmpAutor = _stmt.getText(_columnIndexOfAutor)
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpDuracion: String
          _tmpDuracion = _stmt.getText(_columnIndexOfDuracion)
          val _tmpDescripcion: String
          _tmpDescripcion = _stmt.getText(_columnIndexOfDescripcion)
          val _tmpCodigoQr: String
          _tmpCodigoQr = _stmt.getText(_columnIndexOfCodigoQr)
          val _tmpPuntos: Int
          _tmpPuntos = _stmt.getLong(_columnIndexOfPuntos).toInt()
          val _tmpPublicado: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfPublicado).toInt()
          _tmpPublicado = _tmp != 0
          _result = ContenidoEntity(_tmpId,_tmpPuntoId,_tmpTitulo,_tmpTema,_tmpAutor,_tmpUrl,_tmpDuracion,_tmpDescripcion,_tmpCodigoQr,_tmpPuntos,_tmpPublicado)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observarParaGestion(): Flow<List<CapsulaGestion>> {
    val _sql: String = """
        |
        |        SELECT c.*, p.nombre AS puntoNombre
        |        FROM contenidos c
        |        INNER JOIN puntos p ON p.id = c.puntoId
        |        ORDER BY c.publicado ASC, c.titulo COLLATE NOCASE
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("contenidos", "puntos")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPuntoId: Int = getColumnIndexOrThrow(_stmt, "puntoId")
        val _columnIndexOfTitulo: Int = getColumnIndexOrThrow(_stmt, "titulo")
        val _columnIndexOfTema: Int = getColumnIndexOrThrow(_stmt, "tema")
        val _columnIndexOfAutor: Int = getColumnIndexOrThrow(_stmt, "autor")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfDuracion: Int = getColumnIndexOrThrow(_stmt, "duracion")
        val _columnIndexOfDescripcion: Int = getColumnIndexOrThrow(_stmt, "descripcion")
        val _columnIndexOfCodigoQr: Int = getColumnIndexOrThrow(_stmt, "codigoQr")
        val _columnIndexOfPuntos: Int = getColumnIndexOrThrow(_stmt, "puntos")
        val _columnIndexOfPublicado: Int = getColumnIndexOrThrow(_stmt, "publicado")
        val _columnIndexOfPuntoNombre: Int = getColumnIndexOrThrow(_stmt, "puntoNombre")
        val _result: MutableList<CapsulaGestion> = mutableListOf()
        while (_stmt.step()) {
          val _item: CapsulaGestion
          val _tmpPuntoNombre: String
          _tmpPuntoNombre = _stmt.getText(_columnIndexOfPuntoNombre)
          val _tmpContenido: ContenidoEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPuntoId: Long
          _tmpPuntoId = _stmt.getLong(_columnIndexOfPuntoId)
          val _tmpTitulo: String
          _tmpTitulo = _stmt.getText(_columnIndexOfTitulo)
          val _tmpTema: String
          _tmpTema = _stmt.getText(_columnIndexOfTema)
          val _tmpAutor: String
          _tmpAutor = _stmt.getText(_columnIndexOfAutor)
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpDuracion: String
          _tmpDuracion = _stmt.getText(_columnIndexOfDuracion)
          val _tmpDescripcion: String
          _tmpDescripcion = _stmt.getText(_columnIndexOfDescripcion)
          val _tmpCodigoQr: String
          _tmpCodigoQr = _stmt.getText(_columnIndexOfCodigoQr)
          val _tmpPuntos: Int
          _tmpPuntos = _stmt.getLong(_columnIndexOfPuntos).toInt()
          val _tmpPublicado: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfPublicado).toInt()
          _tmpPublicado = _tmp != 0
          _tmpContenido = ContenidoEntity(_tmpId,_tmpPuntoId,_tmpTitulo,_tmpTema,_tmpAutor,_tmpUrl,_tmpDuracion,_tmpDescripcion,_tmpCodigoQr,_tmpPuntos,_tmpPublicado)
          _item = CapsulaGestion(_tmpContenido,_tmpPuntoNombre)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observarTotal(): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM contenidos"
    return createFlow(__db, false, arrayOf("contenidos")) { _connection ->
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

  public override fun observarPendientes(): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM contenidos WHERE publicado = 0"
    return createFlow(__db, false, arrayOf("contenidos")) { _connection ->
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

  public override fun observarTotalPublicados(): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM contenidos WHERE publicado = 1"
    return createFlow(__db, false, arrayOf("contenidos")) { _connection ->
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

  public override fun observarPorPuntoConEstado(puntoId: Long, usuarioId: Long): Flow<List<ContenidoConEstado>> {
    val _sql: String = """
        |
        |        SELECT c.*,
        |            EXISTS(SELECT 1 FROM descubrimientos d
        |                   WHERE d.contenidoId = c.id AND d.usuarioId = ?) AS descubierto,
        |            EXISTS(SELECT 1 FROM descubrimientos d
        |                   WHERE d.contenidoId = c.id AND d.usuarioId = ? AND d.visto = 1) AS visto
        |        FROM contenidos c
        |        WHERE c.puntoId = ? AND c.publicado = 1
        |        ORDER BY c.titulo COLLATE NOCASE
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("descubrimientos", "contenidos")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, usuarioId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, usuarioId)
        _argIndex = 3
        _stmt.bindLong(_argIndex, puntoId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPuntoId: Int = getColumnIndexOrThrow(_stmt, "puntoId")
        val _columnIndexOfTitulo: Int = getColumnIndexOrThrow(_stmt, "titulo")
        val _columnIndexOfTema: Int = getColumnIndexOrThrow(_stmt, "tema")
        val _columnIndexOfAutor: Int = getColumnIndexOrThrow(_stmt, "autor")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfDuracion: Int = getColumnIndexOrThrow(_stmt, "duracion")
        val _columnIndexOfDescripcion: Int = getColumnIndexOrThrow(_stmt, "descripcion")
        val _columnIndexOfCodigoQr: Int = getColumnIndexOrThrow(_stmt, "codigoQr")
        val _columnIndexOfPuntos: Int = getColumnIndexOrThrow(_stmt, "puntos")
        val _columnIndexOfPublicado: Int = getColumnIndexOrThrow(_stmt, "publicado")
        val _columnIndexOfDescubierto: Int = getColumnIndexOrThrow(_stmt, "descubierto")
        val _columnIndexOfVisto: Int = getColumnIndexOrThrow(_stmt, "visto")
        val _result: MutableList<ContenidoConEstado> = mutableListOf()
        while (_stmt.step()) {
          val _item: ContenidoConEstado
          val _tmpDescubierto: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDescubierto).toInt()
          _tmpDescubierto = _tmp != 0
          val _tmpVisto: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfVisto).toInt()
          _tmpVisto = _tmp_1 != 0
          val _tmpContenido: ContenidoEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPuntoId: Long
          _tmpPuntoId = _stmt.getLong(_columnIndexOfPuntoId)
          val _tmpTitulo: String
          _tmpTitulo = _stmt.getText(_columnIndexOfTitulo)
          val _tmpTema: String
          _tmpTema = _stmt.getText(_columnIndexOfTema)
          val _tmpAutor: String
          _tmpAutor = _stmt.getText(_columnIndexOfAutor)
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpDuracion: String
          _tmpDuracion = _stmt.getText(_columnIndexOfDuracion)
          val _tmpDescripcion: String
          _tmpDescripcion = _stmt.getText(_columnIndexOfDescripcion)
          val _tmpCodigoQr: String
          _tmpCodigoQr = _stmt.getText(_columnIndexOfCodigoQr)
          val _tmpPuntos: Int
          _tmpPuntos = _stmt.getLong(_columnIndexOfPuntos).toInt()
          val _tmpPublicado: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfPublicado).toInt()
          _tmpPublicado = _tmp_2 != 0
          _tmpContenido = ContenidoEntity(_tmpId,_tmpPuntoId,_tmpTitulo,_tmpTema,_tmpAutor,_tmpUrl,_tmpDuracion,_tmpDescripcion,_tmpCodigoQr,_tmpPuntos,_tmpPublicado)
          _item = ContenidoConEstado(_tmpContenido,_tmpDescubierto,_tmpVisto)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun buscarPorCodigoQr(codigo: String): ContenidoEntity? {
    val _sql: String = "SELECT * FROM contenidos WHERE codigoQr = ? AND publicado = 1 LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, codigo)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPuntoId: Int = getColumnIndexOrThrow(_stmt, "puntoId")
        val _columnIndexOfTitulo: Int = getColumnIndexOrThrow(_stmt, "titulo")
        val _columnIndexOfTema: Int = getColumnIndexOrThrow(_stmt, "tema")
        val _columnIndexOfAutor: Int = getColumnIndexOrThrow(_stmt, "autor")
        val _columnIndexOfUrl: Int = getColumnIndexOrThrow(_stmt, "url")
        val _columnIndexOfDuracion: Int = getColumnIndexOrThrow(_stmt, "duracion")
        val _columnIndexOfDescripcion: Int = getColumnIndexOrThrow(_stmt, "descripcion")
        val _columnIndexOfCodigoQr: Int = getColumnIndexOrThrow(_stmt, "codigoQr")
        val _columnIndexOfPuntos: Int = getColumnIndexOrThrow(_stmt, "puntos")
        val _columnIndexOfPublicado: Int = getColumnIndexOrThrow(_stmt, "publicado")
        val _result: ContenidoEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPuntoId: Long
          _tmpPuntoId = _stmt.getLong(_columnIndexOfPuntoId)
          val _tmpTitulo: String
          _tmpTitulo = _stmt.getText(_columnIndexOfTitulo)
          val _tmpTema: String
          _tmpTema = _stmt.getText(_columnIndexOfTema)
          val _tmpAutor: String
          _tmpAutor = _stmt.getText(_columnIndexOfAutor)
          val _tmpUrl: String
          _tmpUrl = _stmt.getText(_columnIndexOfUrl)
          val _tmpDuracion: String
          _tmpDuracion = _stmt.getText(_columnIndexOfDuracion)
          val _tmpDescripcion: String
          _tmpDescripcion = _stmt.getText(_columnIndexOfDescripcion)
          val _tmpCodigoQr: String
          _tmpCodigoQr = _stmt.getText(_columnIndexOfCodigoQr)
          val _tmpPuntos: Int
          _tmpPuntos = _stmt.getLong(_columnIndexOfPuntos).toInt()
          val _tmpPublicado: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfPublicado).toInt()
          _tmpPublicado = _tmp != 0
          _result = ContenidoEntity(_tmpId,_tmpPuntoId,_tmpTitulo,_tmpTema,_tmpAutor,_tmpUrl,_tmpDuracion,_tmpDescripcion,_tmpCodigoQr,_tmpPuntos,_tmpPublicado)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun contarPorCodigoQr(codigo: String, excluirId: Long): Int {
    val _sql: String = "SELECT COUNT(*) FROM contenidos WHERE codigoQr = ? AND id != ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, codigo)
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

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
