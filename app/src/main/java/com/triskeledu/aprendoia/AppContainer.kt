package com.triskeledu.aprendoia

import android.content.Context
import com.triskeledu.aprendoia.data.local.AppDatabase
import com.triskeledu.aprendoia.data.local.TransaccionRoom
import com.triskeledu.aprendoia.data.remote.ServiciosRemotos
import com.triskeledu.aprendoia.data.repository.ClimaRepository
import com.triskeledu.aprendoia.data.repository.GestionRepository
import com.triskeledu.aprendoia.data.repository.JuegoRepository
import com.triskeledu.aprendoia.data.repository.SincronizacionRepository
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import com.triskeledu.aprendoia.data.sesion.SesionManager
import com.triskeledu.aprendoia.util.GaleriaFotos
import com.triskeledu.aprendoia.util.Notificador

/**
 * Contenedor de dependencias manual (sin Hilt): crea una sola vez la base local,
 * los clientes Retrofit y los repositorios, y los comparte con todos los ViewModel.
 */
class AppContainer(context: Context) {
    val baseDatos: AppDatabase = AppDatabase.obtener(context)
    val sesion = SesionManager(context)
    val notificador = Notificador(context.applicationContext)
    val galeria = GaleriaFotos(context.applicationContext)

    /** Retrofit: 3 microservicios propios + API externa del clima. */
    val remotos = ServiciosRemotos()
    private val transaccion = TransaccionRoom(baseDatos)

    val sincronizacionRepository = SincronizacionRepository(baseDatos, remotos.contenidos, remotos.juego, transaccion)
    val usuarioRepository = UsuarioRepository(baseDatos.usuarioDao(), sesion, remotos.usuarios, remotos.juego)
    val juegoRepository = JuegoRepository(baseDatos, notificador, remotos.juego, sincronizacionRepository, transaccion)
    val gestionRepository = GestionRepository(baseDatos, notificador, remotos.contenidos, sincronizacionRepository)
    val climaRepository = ClimaRepository(remotos.clima)
}
