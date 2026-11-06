package com.triskeledu.aprendoia.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Pantalla temporal. Permite probar la navegación de las pantallas que aún
 * no se construyen; en cada etapa se reemplaza por la pantalla real.
 */
@Composable
fun PantallaEnConstruccion(
    titulo: String,
    detalle: String = "",
    acciones: List<Pair<String, () -> Unit>> = emptyList(),
    onVolver: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(titulo, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
        if (detalle.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                detalle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.height(24.dp))
        acciones.forEach { (texto, accion) ->
            Button(onClick = accion, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Text(texto)
            }
        }
        if (onVolver != null) {
            OutlinedButton(onClick = onVolver, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Text("Volver")
            }
        }
    }
}
