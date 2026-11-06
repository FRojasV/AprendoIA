package com.triskeledu.aprendoia

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.triskeledu.aprendoia.data.local.DatosIniciales
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration

/**
 * Clase Application: se crea antes que cualquier pantalla.
 * Aquí se configura osmdroid, se crea el contenedor y se pueblan los datos iniciales.
 * Se registra en el AndroidManifest con android:name=".AprendoIAApp".
 */
class AprendoIAApp : Application() {

    lateinit var contenedor: AppContainer
        private set

    /** Corrutinas que viven mientras viva la app (no dependen de una pantalla). */
    val scopeApp = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        // Igual que en el ejemplo de mapas: OpenStreetMap exige identificar la app.
        Configuration.getInstance().userAgentValue = packageName

        contenedor = AppContainer(this)

        scopeApp.launch {
            DatosIniciales.poblar(contenedor.baseDatos)
        }
    }
}

/**
 * Atajo para obtener la app dentro de un viewModelFactory { initializer { ... } }.
 * Ejemplo: initializer { MiViewModel(aprendoApp().contenedor.usuarioRepository) }
 */
fun CreationExtras.aprendoApp(): AprendoIAApp =
    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as AprendoIAApp
