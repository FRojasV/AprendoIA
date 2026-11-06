package com.triskeledu.aprendoia.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.triskeledu.aprendoia.data.local.dao.ContenidoDao
import com.triskeledu.aprendoia.data.local.dao.DescubrimientoDao
import com.triskeledu.aprendoia.data.local.dao.HistorialDao
import com.triskeledu.aprendoia.data.local.dao.NotificacionDao
import com.triskeledu.aprendoia.data.local.dao.PuntoDao
import com.triskeledu.aprendoia.data.local.dao.SelfieDao
import com.triskeledu.aprendoia.data.local.dao.UsuarioDao
import com.triskeledu.aprendoia.data.local.entity.ContenidoEntity
import com.triskeledu.aprendoia.data.local.entity.DescubrimientoEntity
import com.triskeledu.aprendoia.data.local.entity.HistorialEntity
import com.triskeledu.aprendoia.data.local.entity.NotificacionEntity
import com.triskeledu.aprendoia.data.local.entity.PuntoEntity
import com.triskeledu.aprendoia.data.local.entity.SelfieEntity
import com.triskeledu.aprendoia.data.local.entity.UsuarioEntity

/**
 * Base de datos SQLite de la app, administrada por Room.
 * Si cambias alguna entidad durante el desarrollo, sube "version" en 1:
 * fallbackToDestructiveMigration recrea la base (se pierden los datos de prueba).
 */
@Database(
    entities = [
        UsuarioEntity::class,
        PuntoEntity::class,
        ContenidoEntity::class,
        DescubrimientoEntity::class,
        SelfieEntity::class,
        HistorialEntity::class,
        NotificacionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun puntoDao(): PuntoDao
    abstract fun contenidoDao(): ContenidoDao
    abstract fun descubrimientoDao(): DescubrimientoDao
    abstract fun selfieDao(): SelfieDao
    abstract fun historialDao(): HistorialDao
    abstract fun notificacionDao(): NotificacionDao

    companion object {
        const val NOMBRE_BD = "aprendo_ia.db"

        @Volatile
        private var instancia: AppDatabase? = null

        /** Patrón Singleton: una sola conexión a la base para toda la app. */
        fun obtener(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    NOMBRE_BD
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { instancia = it }
            }
    }
}
