package com.triskeledu.aprendoia.`data`.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.triskeledu.aprendoia.`data`.local.dao.ContenidoDao
import com.triskeledu.aprendoia.`data`.local.dao.ContenidoDao_Impl
import com.triskeledu.aprendoia.`data`.local.dao.DescubrimientoDao
import com.triskeledu.aprendoia.`data`.local.dao.DescubrimientoDao_Impl
import com.triskeledu.aprendoia.`data`.local.dao.HistorialDao
import com.triskeledu.aprendoia.`data`.local.dao.HistorialDao_Impl
import com.triskeledu.aprendoia.`data`.local.dao.NotificacionDao
import com.triskeledu.aprendoia.`data`.local.dao.NotificacionDao_Impl
import com.triskeledu.aprendoia.`data`.local.dao.PuntoDao
import com.triskeledu.aprendoia.`data`.local.dao.PuntoDao_Impl
import com.triskeledu.aprendoia.`data`.local.dao.SelfieDao
import com.triskeledu.aprendoia.`data`.local.dao.SelfieDao_Impl
import com.triskeledu.aprendoia.`data`.local.dao.UsuarioDao
import com.triskeledu.aprendoia.`data`.local.dao.UsuarioDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDatabase_Impl : AppDatabase() {
  private val _usuarioDao: Lazy<UsuarioDao> = lazy {
    UsuarioDao_Impl(this)
  }

  private val _puntoDao: Lazy<PuntoDao> = lazy {
    PuntoDao_Impl(this)
  }

  private val _contenidoDao: Lazy<ContenidoDao> = lazy {
    ContenidoDao_Impl(this)
  }

  private val _descubrimientoDao: Lazy<DescubrimientoDao> = lazy {
    DescubrimientoDao_Impl(this)
  }

  private val _selfieDao: Lazy<SelfieDao> = lazy {
    SelfieDao_Impl(this)
  }

  private val _historialDao: Lazy<HistorialDao> = lazy {
    HistorialDao_Impl(this)
  }

  private val _notificacionDao: Lazy<NotificacionDao> = lazy {
    NotificacionDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1, "82a8c526010495ec5b823dad51d53e33", "a8b8533ea4d0b37b944b1b7cf5361185") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `usuarios` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `nombre` TEXT NOT NULL, `alias` TEXT NOT NULL, `correo` TEXT NOT NULL, `telefono` TEXT NOT NULL, `password` TEXT NOT NULL, `direccion` TEXT NOT NULL, `avatar` TEXT NOT NULL, `rol` TEXT NOT NULL, `notificacionesActivas` INTEGER NOT NULL, `fechaRegistro` INTEGER NOT NULL)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_usuarios_correo` ON `usuarios` (`correo`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `puntos` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `nombre` TEXT NOT NULL, `descripcion` TEXT NOT NULL, `infoTuristica` TEXT NOT NULL, `latitud` REAL NOT NULL, `longitud` REAL NOT NULL, `esHitoTuristico` INTEGER NOT NULL, `imagenUrl` TEXT, `fechaCreacion` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `contenidos` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `puntoId` INTEGER NOT NULL, `titulo` TEXT NOT NULL, `tema` TEXT NOT NULL, `autor` TEXT NOT NULL, `url` TEXT NOT NULL, `duracion` TEXT NOT NULL, `descripcion` TEXT NOT NULL, `codigoQr` TEXT NOT NULL, `puntos` INTEGER NOT NULL, `publicado` INTEGER NOT NULL, FOREIGN KEY(`puntoId`) REFERENCES `puntos`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_contenidos_puntoId` ON `contenidos` (`puntoId`)")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_contenidos_codigoQr` ON `contenidos` (`codigoQr`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `descubrimientos` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `usuarioId` INTEGER NOT NULL, `contenidoId` INTEGER NOT NULL, `puntos` INTEGER NOT NULL, `fecha` INTEGER NOT NULL, `visto` INTEGER NOT NULL, `fechaVisto` INTEGER, FOREIGN KEY(`usuarioId`) REFERENCES `usuarios`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`contenidoId`) REFERENCES `contenidos`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_descubrimientos_usuarioId_contenidoId` ON `descubrimientos` (`usuarioId`, `contenidoId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_descubrimientos_contenidoId` ON `descubrimientos` (`contenidoId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `selfies` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `usuarioId` INTEGER NOT NULL, `puntoId` INTEGER NOT NULL, `rutaFoto` TEXT NOT NULL, `puntos` INTEGER NOT NULL, `fecha` INTEGER NOT NULL, FOREIGN KEY(`usuarioId`) REFERENCES `usuarios`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`puntoId`) REFERENCES `puntos`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_selfies_usuarioId_puntoId` ON `selfies` (`usuarioId`, `puntoId`)")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_selfies_puntoId` ON `selfies` (`puntoId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `historial` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `usuarioId` INTEGER NOT NULL, `tipo` TEXT NOT NULL, `titulo` TEXT NOT NULL, `detalle` TEXT NOT NULL, `puntos` INTEGER NOT NULL, `fecha` INTEGER NOT NULL, FOREIGN KEY(`usuarioId`) REFERENCES `usuarios`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_historial_usuarioId` ON `historial` (`usuarioId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `notificaciones` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `usuarioId` INTEGER NOT NULL, `titulo` TEXT NOT NULL, `mensaje` TEXT NOT NULL, `tipo` TEXT NOT NULL, `destino` TEXT, `leida` INTEGER NOT NULL, `fecha` INTEGER NOT NULL, FOREIGN KEY(`usuarioId`) REFERENCES `usuarios`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_notificaciones_usuarioId` ON `notificaciones` (`usuarioId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '82a8c526010495ec5b823dad51d53e33')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `usuarios`")
        connection.execSQL("DROP TABLE IF EXISTS `puntos`")
        connection.execSQL("DROP TABLE IF EXISTS `contenidos`")
        connection.execSQL("DROP TABLE IF EXISTS `descubrimientos`")
        connection.execSQL("DROP TABLE IF EXISTS `selfies`")
        connection.execSQL("DROP TABLE IF EXISTS `historial`")
        connection.execSQL("DROP TABLE IF EXISTS `notificaciones`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        connection.execSQL("PRAGMA foreign_keys = ON")
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsUsuarios: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsUsuarios.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsuarios.put("nombre", TableInfo.Column("nombre", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsuarios.put("alias", TableInfo.Column("alias", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsuarios.put("correo", TableInfo.Column("correo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsuarios.put("telefono", TableInfo.Column("telefono", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsuarios.put("password", TableInfo.Column("password", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsuarios.put("direccion", TableInfo.Column("direccion", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsuarios.put("avatar", TableInfo.Column("avatar", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsuarios.put("rol", TableInfo.Column("rol", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsuarios.put("notificacionesActivas", TableInfo.Column("notificacionesActivas", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsuarios.put("fechaRegistro", TableInfo.Column("fechaRegistro", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysUsuarios: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesUsuarios: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesUsuarios.add(TableInfo.Index("index_usuarios_correo", true, listOf("correo"), listOf("ASC")))
        val _infoUsuarios: TableInfo = TableInfo("usuarios", _columnsUsuarios, _foreignKeysUsuarios, _indicesUsuarios)
        val _existingUsuarios: TableInfo = read(connection, "usuarios")
        if (!_infoUsuarios.equals(_existingUsuarios)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |usuarios(com.triskeledu.aprendoia.data.local.entity.UsuarioEntity).
              | Expected:
              |""".trimMargin() + _infoUsuarios + """
              |
              | Found:
              |""".trimMargin() + _existingUsuarios)
        }
        val _columnsPuntos: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsPuntos.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPuntos.put("nombre", TableInfo.Column("nombre", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPuntos.put("descripcion", TableInfo.Column("descripcion", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPuntos.put("infoTuristica", TableInfo.Column("infoTuristica", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPuntos.put("latitud", TableInfo.Column("latitud", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPuntos.put("longitud", TableInfo.Column("longitud", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPuntos.put("esHitoTuristico", TableInfo.Column("esHitoTuristico", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPuntos.put("imagenUrl", TableInfo.Column("imagenUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPuntos.put("fechaCreacion", TableInfo.Column("fechaCreacion", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPuntos: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesPuntos: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoPuntos: TableInfo = TableInfo("puntos", _columnsPuntos, _foreignKeysPuntos, _indicesPuntos)
        val _existingPuntos: TableInfo = read(connection, "puntos")
        if (!_infoPuntos.equals(_existingPuntos)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |puntos(com.triskeledu.aprendoia.data.local.entity.PuntoEntity).
              | Expected:
              |""".trimMargin() + _infoPuntos + """
              |
              | Found:
              |""".trimMargin() + _existingPuntos)
        }
        val _columnsContenidos: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsContenidos.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContenidos.put("puntoId", TableInfo.Column("puntoId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContenidos.put("titulo", TableInfo.Column("titulo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContenidos.put("tema", TableInfo.Column("tema", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContenidos.put("autor", TableInfo.Column("autor", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContenidos.put("url", TableInfo.Column("url", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContenidos.put("duracion", TableInfo.Column("duracion", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContenidos.put("descripcion", TableInfo.Column("descripcion", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContenidos.put("codigoQr", TableInfo.Column("codigoQr", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContenidos.put("puntos", TableInfo.Column("puntos", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsContenidos.put("publicado", TableInfo.Column("publicado", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysContenidos: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysContenidos.add(TableInfo.ForeignKey("puntos", "CASCADE", "NO ACTION", listOf("puntoId"), listOf("id")))
        val _indicesContenidos: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesContenidos.add(TableInfo.Index("index_contenidos_puntoId", false, listOf("puntoId"), listOf("ASC")))
        _indicesContenidos.add(TableInfo.Index("index_contenidos_codigoQr", true, listOf("codigoQr"), listOf("ASC")))
        val _infoContenidos: TableInfo = TableInfo("contenidos", _columnsContenidos, _foreignKeysContenidos, _indicesContenidos)
        val _existingContenidos: TableInfo = read(connection, "contenidos")
        if (!_infoContenidos.equals(_existingContenidos)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |contenidos(com.triskeledu.aprendoia.data.local.entity.ContenidoEntity).
              | Expected:
              |""".trimMargin() + _infoContenidos + """
              |
              | Found:
              |""".trimMargin() + _existingContenidos)
        }
        val _columnsDescubrimientos: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsDescubrimientos.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDescubrimientos.put("usuarioId", TableInfo.Column("usuarioId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDescubrimientos.put("contenidoId", TableInfo.Column("contenidoId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDescubrimientos.put("puntos", TableInfo.Column("puntos", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDescubrimientos.put("fecha", TableInfo.Column("fecha", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDescubrimientos.put("visto", TableInfo.Column("visto", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsDescubrimientos.put("fechaVisto", TableInfo.Column("fechaVisto", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysDescubrimientos: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysDescubrimientos.add(TableInfo.ForeignKey("usuarios", "CASCADE", "NO ACTION", listOf("usuarioId"), listOf("id")))
        _foreignKeysDescubrimientos.add(TableInfo.ForeignKey("contenidos", "CASCADE", "NO ACTION", listOf("contenidoId"), listOf("id")))
        val _indicesDescubrimientos: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesDescubrimientos.add(TableInfo.Index("index_descubrimientos_usuarioId_contenidoId", true, listOf("usuarioId", "contenidoId"), listOf("ASC", "ASC")))
        _indicesDescubrimientos.add(TableInfo.Index("index_descubrimientos_contenidoId", false, listOf("contenidoId"), listOf("ASC")))
        val _infoDescubrimientos: TableInfo = TableInfo("descubrimientos", _columnsDescubrimientos, _foreignKeysDescubrimientos, _indicesDescubrimientos)
        val _existingDescubrimientos: TableInfo = read(connection, "descubrimientos")
        if (!_infoDescubrimientos.equals(_existingDescubrimientos)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |descubrimientos(com.triskeledu.aprendoia.data.local.entity.DescubrimientoEntity).
              | Expected:
              |""".trimMargin() + _infoDescubrimientos + """
              |
              | Found:
              |""".trimMargin() + _existingDescubrimientos)
        }
        val _columnsSelfies: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSelfies.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSelfies.put("usuarioId", TableInfo.Column("usuarioId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSelfies.put("puntoId", TableInfo.Column("puntoId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSelfies.put("rutaFoto", TableInfo.Column("rutaFoto", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSelfies.put("puntos", TableInfo.Column("puntos", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSelfies.put("fecha", TableInfo.Column("fecha", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSelfies: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysSelfies.add(TableInfo.ForeignKey("usuarios", "CASCADE", "NO ACTION", listOf("usuarioId"), listOf("id")))
        _foreignKeysSelfies.add(TableInfo.ForeignKey("puntos", "CASCADE", "NO ACTION", listOf("puntoId"), listOf("id")))
        val _indicesSelfies: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesSelfies.add(TableInfo.Index("index_selfies_usuarioId_puntoId", true, listOf("usuarioId", "puntoId"), listOf("ASC", "ASC")))
        _indicesSelfies.add(TableInfo.Index("index_selfies_puntoId", false, listOf("puntoId"), listOf("ASC")))
        val _infoSelfies: TableInfo = TableInfo("selfies", _columnsSelfies, _foreignKeysSelfies, _indicesSelfies)
        val _existingSelfies: TableInfo = read(connection, "selfies")
        if (!_infoSelfies.equals(_existingSelfies)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |selfies(com.triskeledu.aprendoia.data.local.entity.SelfieEntity).
              | Expected:
              |""".trimMargin() + _infoSelfies + """
              |
              | Found:
              |""".trimMargin() + _existingSelfies)
        }
        val _columnsHistorial: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsHistorial.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHistorial.put("usuarioId", TableInfo.Column("usuarioId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHistorial.put("tipo", TableInfo.Column("tipo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHistorial.put("titulo", TableInfo.Column("titulo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHistorial.put("detalle", TableInfo.Column("detalle", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHistorial.put("puntos", TableInfo.Column("puntos", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHistorial.put("fecha", TableInfo.Column("fecha", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysHistorial: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysHistorial.add(TableInfo.ForeignKey("usuarios", "CASCADE", "NO ACTION", listOf("usuarioId"), listOf("id")))
        val _indicesHistorial: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesHistorial.add(TableInfo.Index("index_historial_usuarioId", false, listOf("usuarioId"), listOf("ASC")))
        val _infoHistorial: TableInfo = TableInfo("historial", _columnsHistorial, _foreignKeysHistorial, _indicesHistorial)
        val _existingHistorial: TableInfo = read(connection, "historial")
        if (!_infoHistorial.equals(_existingHistorial)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |historial(com.triskeledu.aprendoia.data.local.entity.HistorialEntity).
              | Expected:
              |""".trimMargin() + _infoHistorial + """
              |
              | Found:
              |""".trimMargin() + _existingHistorial)
        }
        val _columnsNotificaciones: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsNotificaciones.put("id", TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNotificaciones.put("usuarioId", TableInfo.Column("usuarioId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNotificaciones.put("titulo", TableInfo.Column("titulo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNotificaciones.put("mensaje", TableInfo.Column("mensaje", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNotificaciones.put("tipo", TableInfo.Column("tipo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNotificaciones.put("destino", TableInfo.Column("destino", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNotificaciones.put("leida", TableInfo.Column("leida", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsNotificaciones.put("fecha", TableInfo.Column("fecha", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysNotificaciones: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysNotificaciones.add(TableInfo.ForeignKey("usuarios", "CASCADE", "NO ACTION", listOf("usuarioId"), listOf("id")))
        val _indicesNotificaciones: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesNotificaciones.add(TableInfo.Index("index_notificaciones_usuarioId", false, listOf("usuarioId"), listOf("ASC")))
        val _infoNotificaciones: TableInfo = TableInfo("notificaciones", _columnsNotificaciones, _foreignKeysNotificaciones, _indicesNotificaciones)
        val _existingNotificaciones: TableInfo = read(connection, "notificaciones")
        if (!_infoNotificaciones.equals(_existingNotificaciones)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |notificaciones(com.triskeledu.aprendoia.data.local.entity.NotificacionEntity).
              | Expected:
              |""".trimMargin() + _infoNotificaciones + """
              |
              | Found:
              |""".trimMargin() + _existingNotificaciones)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "usuarios", "puntos", "contenidos", "descubrimientos", "selfies", "historial", "notificaciones")
  }

  public override fun clearAllTables() {
    super.performClear(true, "usuarios", "puntos", "contenidos", "descubrimientos", "selfies", "historial", "notificaciones")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(UsuarioDao::class, UsuarioDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(PuntoDao::class, PuntoDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ContenidoDao::class, ContenidoDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(DescubrimientoDao::class, DescubrimientoDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(SelfieDao::class, SelfieDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(HistorialDao::class, HistorialDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(NotificacionDao::class, NotificacionDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun usuarioDao(): UsuarioDao = _usuarioDao.value

  public override fun puntoDao(): PuntoDao = _puntoDao.value

  public override fun contenidoDao(): ContenidoDao = _contenidoDao.value

  public override fun descubrimientoDao(): DescubrimientoDao = _descubrimientoDao.value

  public override fun selfieDao(): SelfieDao = _selfieDao.value

  public override fun historialDao(): HistorialDao = _historialDao.value

  public override fun notificacionDao(): NotificacionDao = _notificacionDao.value
}
