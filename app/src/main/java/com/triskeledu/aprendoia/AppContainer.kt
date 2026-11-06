package com.triskeledu.aprendoia

import android.content.Context
import com.triskeledu.aprendoia.data.local.AppDatabase
import com.triskeledu.aprendoia.data.repository.GestionRepository
import com.triskeledu.aprendoia.data.repository.JuegoRepository
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import com.triskeledu.aprendoia.data.sesion.SesionManager
import com.triskeledu.aprendoia.util.GaleriaFotos
import com.triskeledu.aprendoia.util.Notificador


class AppContainer(context: Context) {
    val baseDatos: AppDatabase = AppDatabase.obtener(context)
    val sesion = SesionManager(context)
    val notificador = Notificador(context.applicationContext)
    val galeria = GaleriaFotos(context.applicationContext)

    val usuarioRepository = UsuarioRepository(baseDatos.usuarioDao(), sesion)
    val juegoRepository = JuegoRepository(baseDatos, notificador)
    val gestionRepository = GestionRepository(baseDatos, notificador)
}
