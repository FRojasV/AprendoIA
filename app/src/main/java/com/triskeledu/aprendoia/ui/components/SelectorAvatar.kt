package com.triskeledu.aprendoia.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Selector de avatar de la pantalla 2: muestra 3 avatares y un botón "+".
 * "Más avatares" despliega la lista completa.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectorAvatar(
    seleccionado: String,
    mostrarTodos: Boolean,
    onSeleccionar: (String) -> Unit,
    onAlternarMas: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            "Selecciona tu avatar",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        val visibles = if (mostrarTodos) Avatares.paraJugadores else Avatares.paraJugadores.take(3)

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            visibles.forEach { opcion ->
                AvatarJugador(
                    clave = opcion.clave,
                    tamano = 60.dp,
                    seleccionado = opcion.clave == seleccionado,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onSeleccionar(opcion.clave) }
                )
            }
            if (!mostrarTodos) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .clickable(onClick = onAlternarMas),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Más avatares", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
        TextButton(onClick = onAlternarMas, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(if (mostrarTodos) "Ver menos" else "Más avatares")
        }
    }
}
