package com.triskeledu.aprendoia

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import com.triskeledu.aprendoia.ui.navigation.AppNavHost
import com.triskeledu.aprendoia.ui.theme.AprendoIATheme
import com.triskeledu.aprendoia.util.Notificador

/**
 * Única Activity de la app. Todas las pantallas son Composable
 * y se cambian con Navigation Compose (ver ui/navigation/AppNavHost.kt).
 */
class MainActivity : ComponentActivity() {

    /**
     * Pantalla que se pidió abrir desde una notificación del sistema
     * (por ejemplo "notificaciones"). Es un estado observable para que,
     * si la app ya estaba abierta, Compose reaccione en onNewIntent.
     */
    private val destinoExterno = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        destinoExterno.value = intent?.getStringExtra(Notificador.EXTRA_ABRIR)

        setContent {
            AprendoIATheme {
                AppNavHost(
                    destinoExterno = destinoExterno.value,
                    onDestinoConsumido = { destinoExterno.value = null }
                )
            }
        }
    }

    /** La app ya estaba abierta y el usuario tocó una notificación. */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        destinoExterno.value = intent.getStringExtra(Notificador.EXTRA_ABRIR)
    }
}
