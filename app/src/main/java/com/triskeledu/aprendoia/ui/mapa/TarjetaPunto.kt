package com.triskeledu.aprendoia.ui.mapa

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.triskeledu.aprendoia.data.local.relacion.PuntoConEstado
import com.triskeledu.aprendoia.ui.theme.AzulClaro
import com.triskeledu.aprendoia.ui.theme.AzulOscuro
import com.triskeledu.aprendoia.ui.theme.NaranjoClaro

/** Tarjeta inferior del mapa con el punto seleccionado. */
@Composable
fun TarjetaPunto(
    punto: PuntoConEstado,
    distancia: String?,
    onVerDetalle: () -> Unit,
    onComoLlegar: () -> Unit,
    onCerrar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(
                        punto.punto.nombre,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AzulOscuro
                    )
                    Text(
                        punto.punto.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(onClick = onCerrar, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.Close, contentDescription = "Cerrar")
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                EtiquetaEstado(punto.descubierto)
                if (punto.punto.esHitoTuristico) {
                    Etiqueta("Hito turístico · +100", NaranjoClaro, AzulOscuro)
                }
                Spacer(Modifier.weight(1f))
                if (distancia != null) {
                    Icon(Icons.Filled.NearMe, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(2.dp))
                    Text(distancia, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = onComoLlegar, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.Directions, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Cómo llegar")
                }
                Button(onClick = onVerDetalle, modifier = Modifier.weight(1f)) {
                    Text("Ver detalle")
                }
            }
        }
    }
}

/** Chip "Pendiente" (azul) o "Completado" (naranjo), igual que los marcadores. */
@Composable
fun EtiquetaEstado(completado: Boolean) {
    if (completado) Etiqueta("Completado", ColorCompletado, Color.White)
    else Etiqueta("Pendiente", AzulClaro, ColorPendiente)
}

@Composable
fun Etiqueta(texto: String, fondo: Color, colorTexto: Color) {
    Surface(color = fondo, shape = RoundedCornerShape(10.dp)) {
        Text(
            texto,
            style = MaterialTheme.typography.labelMedium,
            color = colorTexto,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
