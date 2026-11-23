package com.triskeledu.aprendoia.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.triskeledu.aprendoia.data.repository.Clima
import com.triskeledu.aprendoia.ui.theme.AzulOscuro
import com.triskeledu.aprendoia.ui.theme.NaranjoClaro

/** Estado del clima en una pantalla: cargando, con datos o con error. */
data class EstadoClima(
    val cargando: Boolean = false,
    val clima: Clima? = null,
    val error: String? = null
)

/**
 * Tarjeta con el clima actual (API externa Open-Meteo).
 * Si la API falla, muestra un aviso pequeño: el resto de la pantalla no se afecta.
 */
@Composable
fun TarjetaClima(
    titulo: String,
    estado: EstadoClima,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFFDCEBFF), Color(0xFFF1F7FF))))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val clima = estado.clima
        when {
            estado.cargando -> {
                CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(12.dp))
                Text("Consultando el clima…", color = AzulOscuro)
            }
            clima != null -> {
                Text(clima.emoji, fontSize = 34.sp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(titulo, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        "${clima.temperatura}°C · ${clima.descripcion}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AzulOscuro
                    )
                    Text(
                        "Sensación ${clima.sensacionTermica}° · Humedad ${clima.humedad}% · Viento ${clima.vientoKmH} km/h",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(clima.consejo, style = MaterialTheme.typography.labelSmall, color = AzulOscuro)
                }
                Text("Open-Meteo", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            else -> {
                Icon(Icons.Filled.CloudOff, contentDescription = null, tint = Color.Gray)
                Spacer(Modifier.width(12.dp))
                Text(
                    estado.error ?: "Clima no disponible",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onReintentar) { Icon(Icons.Filled.Refresh, contentDescription = "Reintentar") }
            }
        }
    }
}

/** Franja que avisa que los microservicios no responden (se muestran los datos guardados). */
@Composable
fun AvisoSinConexion(mensaje: String, onReintentar: () -> Unit, modifier: Modifier = Modifier) {
    Surface(color = NaranjoClaro, shape = RoundedCornerShape(14.dp), modifier = modifier.fillMaxWidth()) {
        Row(Modifier.padding(start = 14.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.CloudOff, contentDescription = null, tint = AzulOscuro, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                mensaje,
                style = MaterialTheme.typography.bodySmall,
                color = AzulOscuro,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 10.dp)
            )
            IconButton(onClick = onReintentar) { Icon(Icons.Filled.Refresh, contentDescription = "Reintentar") }
        }
    }
}
