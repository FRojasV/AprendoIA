package com.triskeledu.aprendoia.ui.progreso

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.triskeledu.aprendoia.data.local.relacion.PUNTAJE_MAXIMO
import com.triskeledu.aprendoia.ui.components.TarjetaContador
import com.triskeledu.aprendoia.ui.theme.AzulClaro
import com.triskeledu.aprendoia.ui.theme.AzulOscuro
import com.triskeledu.aprendoia.ui.theme.AzulPrimario
import com.triskeledu.aprendoia.ui.theme.Naranjo
import com.triskeledu.aprendoia.ui.theme.NaranjoClaro
import com.triskeledu.aprendoia.util.conSeparador

/**
 * Pantalla 12: Puntaje y Progreso.
 * Usa MediumTopAppBar (ejemplo de Barras) que se achica al hacer scroll.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaProgreso(
    onVolver: () -> Unit,
    onVerHistorial: () -> Unit,
    viewModel: ProgresoViewModel = viewModel(factory = ProgresoViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val comportamiento = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(comportamiento.nestedScrollConnection),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            MediumTopAppBar(
                title = { Text("Mi Progreso", color = AzulOscuro, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = AzulOscuro)
                    }
                },
                scrollBehavior = comportamiento,
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (estado.cargando) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ---------- Anillo con el puntaje total ----------
            AnilloProgreso(
                avance = estado.avanceTotal,
                puntaje = estado.puntaje,
                subtitulo = "de ${PUNTAJE_MAXIMO.conSeparador()} puntos"
            )

            // ---------- Nivel ----------
            TarjetaNivel(estado)

            // ---------- Desglose ----------
            Tarjeta("¿De dónde vienen tus puntos?") {
                FilaDesglose(Icons.Filled.QrCode2, "Códigos QR", estado.estadisticas.puntosQr, estado.puntaje, AzulPrimario)
                FilaDesglose(Icons.Filled.CameraAlt, "Selfies en hitos", estado.estadisticas.puntosSelfies, estado.puntaje, Naranjo)
            }

            // ---------- Gráfico semanal ----------
            Tarjeta("Puntos de los últimos 7 días") {
                GraficoBarras(estado.puntosUltimos7Dias, Modifier.fillMaxWidth())
            }

            // ---------- Contadores ----------
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TarjetaContador(estado.estadisticas.qrEncontrados, "de ${estado.totalCapsulas} QR", Modifier.weight(1f))
                TarjetaContador(estado.estadisticas.lugaresVisitados, "de ${estado.totalLugares} lugares", Modifier.weight(1f))
                TarjetaContador(estado.estadisticas.videosVistos, "videos vistos", Modifier.weight(1f))
            }

            // ---------- Logros ----------
            Tarjeta("Logros (${estado.logros.count { it.desbloqueado }}/${estado.logros.size})") {
                Logros(estado.logros)
            }

            androidx.compose.material3.TextButton(onClick = onVerHistorial) {
                Text("Ver historial de actividades")
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun TarjetaNivel(estado: ProgresoUiState) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NaranjoClaro),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = Naranjo, modifier = Modifier.size(32.dp))
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Nivel actual", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(estado.nivel.nombre, style = MaterialTheme.typography.titleLarge, color = AzulOscuro)
                }
            }
            LinearProgressIndicator(
                progress = { estado.avanceNivel },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = Naranjo,
                trackColor = Color.White,
                strokeCap = StrokeCap.Round
            )
            val siguiente = estado.siguienteNivel
            Text(
                if (siguiente == null) "¡Alcanzaste el nivel máximo!"
                else "Te faltan ${(siguiente.desde - estado.puntaje).conSeparador()} puntos para ${siguiente.nombre}",
                style = MaterialTheme.typography.bodySmall,
                color = AzulOscuro
            )
        }
    }
}

@Composable
private fun Tarjeta(titulo: String, contenido: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium, color = AzulOscuro)
            contenido()
        }
    }
}

@Composable
private fun FilaDesglose(icono: ImageVector, texto: String, puntos: Int, total: Int, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(texto, modifier = Modifier.weight(1f))
            Text("${puntos.conSeparador()} pts", fontWeight = FontWeight.SemiBold, color = color)
        }
        LinearProgressIndicator(
            progress = { if (total == 0) 0f else puntos.toFloat() / total },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = AzulClaro,
            strokeCap = StrokeCap.Round
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Logros(logros: List<Logro>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        maxItemsInEachRow = 3
    ) {
        logros.forEach { logro ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(if (logro.desbloqueado) Naranjo else AzulClaro),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (logro.desbloqueado) Icons.Filled.EmojiEvents else Icons.Filled.Lock,
                        contentDescription = null,
                        tint = if (logro.desbloqueado) Color.White else AzulPrimario.copy(alpha = 0.5f)
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    logro.titulo,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                    color = if (logro.desbloqueado) AzulOscuro else Color.Gray
                )
                Text(
                    logro.descripcion,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
