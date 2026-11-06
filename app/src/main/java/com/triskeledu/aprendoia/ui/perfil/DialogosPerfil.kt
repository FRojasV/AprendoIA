package com.triskeledu.aprendoia.ui.perfil

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.triskeledu.aprendoia.ui.components.SelectorAvatar
import com.triskeledu.aprendoia.ui.theme.VerdeExito
import com.triskeledu.aprendoia.util.Permisos

/** Diálogo para elegir un nuevo avatar. */
@Composable
fun DialogoCambiarAvatar(
    seleccionado: String,
    onSeleccionar: (String) -> Unit,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Cambiar avatar") },
        text = {
            SelectorAvatar(
                seleccionado = seleccionado,
                mostrarTodos = true,
                onSeleccionar = onSeleccionar,
                onAlternarMas = {}
            )
        },
        confirmButton = { TextButton(onClick = onGuardar) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}

/**
 * Muestra si la app tiene permiso de cámara, ubicación y notificaciones,
 * y permite solicitarlos o abrir Ajustes si el usuario los negó.
 */
@Composable
fun DialogoPermisos(onCerrar: () -> Unit) {
    val context = LocalContext.current
    var estado by remember { mutableStateOf(Permisos.leer(context)) }

    val lanzador = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { estado = Permisos.leer(context) }

    val faltan = Permisos.pendientes(context).isNotEmpty()

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Permisos") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                FilaPermiso(Icons.Filled.CameraAlt, "Cámara", "Escanear QR y tomar selfies", estado.camara)
                FilaPermiso(Icons.Filled.LocationOn, "Ubicación", "Distancia a los puntos del mapa", estado.ubicacion)
                if (Permisos.requiereNotificaciones) {
                    FilaPermiso(Icons.Filled.Notifications, "Notificaciones", "Avisos de puntos y desafíos", estado.notificaciones)
                }
                if (faltan) {
                    Text(
                        "Si rechazaste un permiso más de una vez, actívalo desde Ajustes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            if (faltan) {
                TextButton(onClick = { lanzador.launch(Permisos.pendientes(context)) }) { Text("Solicitar") }
            } else {
                TextButton(onClick = onCerrar) { Text("Listo") }
            }
        },
        dismissButton = {
            TextButton(onClick = { Permisos.abrirAjustes(context) }) { Text("Abrir ajustes") }
        }
    )
}

@Composable
private fun FilaPermiso(icono: ImageVector, titulo: String, detalle: String, concedido: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.titleSmall)
            Text(detalle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(
            if (concedido) Icons.Filled.CheckCircle else Icons.Filled.Error,
            contentDescription = if (concedido) "Concedido" else "No concedido",
            tint = if (concedido) VerdeExito else MaterialTheme.colorScheme.error
        )
    }
}

/** Explica brevemente cómo se juega. */
@Composable
fun DialogoAyuda(onCerrar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("¿Cómo se juega?") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("1. Abre el Mapa y elige un punto de Santiago.")
                Text("2. Ve al lugar y escanea el código QR: ganas 10 puntos.")
                Text("3. Mira la cápsula de IA que desbloqueaste.")
                Text("4. En hitos turísticos, toma una selfie con el QR: +100 puntos.")
                Text(
                    "Soporte: contacto@triskeledu.cl",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = { TextButton(onClick = onCerrar) { Text("Entendido") } }
    )
}
