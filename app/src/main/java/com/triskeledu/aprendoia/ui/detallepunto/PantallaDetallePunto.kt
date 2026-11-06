package com.triskeledu.aprendoia.ui.detallepunto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.triskeledu.aprendoia.data.local.entity.PuntoEntity
import com.triskeledu.aprendoia.data.local.relacion.ContenidoConEstado
import com.triskeledu.aprendoia.ui.mapa.Etiqueta
import com.triskeledu.aprendoia.ui.mapa.EtiquetaEstado
import com.triskeledu.aprendoia.ui.theme.AzulClaro
import com.triskeledu.aprendoia.ui.theme.AzulOscuro
import com.triskeledu.aprendoia.ui.theme.AzulPrimario
import com.triskeledu.aprendoia.ui.theme.Morado
import com.triskeledu.aprendoia.ui.theme.Naranjo
import com.triskeledu.aprendoia.ui.theme.NaranjoClaro
import com.triskeledu.aprendoia.ui.theme.VerdeClaro
import com.triskeledu.aprendoia.ui.theme.VerdeExito
import com.triskeledu.aprendoia.util.Coordenada
import com.triskeledu.aprendoia.util.Ubicacion

/**
 * Pantalla 6: Detalle del punto.
 * Muestra información turística, cápsulas de IA (bloqueadas hasta escanear el QR),
 * la bonificación de selfie en hitos y los botones "Cómo llegar" / "Escanear QR".
 */
@Composable
fun PantallaDetallePunto(
    onVolver: () -> Unit,
    onEscanear: () -> Unit,
    onVerVideo: (Long) -> Unit,
    onTomarSelfie: (Long) -> Unit,
    viewModel: DetallePuntoViewModel = viewModel(factory = DetallePuntoViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Última ubicación conocida para mostrar la distancia (si hay permiso).
    LaunchedEffect(Unit) {
        Ubicacion.ultimaConocida(context)?.let(viewModel::onUbicacion)
    }

    val punto = estado.punto
    if (estado.cargando || punto == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (estado.noExiste) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Este punto ya no existe")
                    TextButton(onClick = onVolver) { Text("Volver") }
                }
            } else {
                CircularProgressIndicator()
            }
        }
        return
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            Ubicacion.abrirComoLlegar(context, Coordenada(punto.latitud, punto.longitud), punto.nombre)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Icon(Icons.Filled.Directions, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Cómo llegar")
                    }
                    Button(
                        onClick = onEscanear,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Icon(Icons.Filled.QrCodeScanner, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Escanear QR")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Encabezado(punto = punto, onVolver = onVolver)

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ---------- Título y etiquetas ----------
                Text(
                    punto.nombre,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = AzulOscuro
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    EtiquetaEstado(estado.completado)
                    if (punto.esHitoTuristico) Etiqueta("Hito turístico", NaranjoClaro, AzulOscuro)
                    Spacer(Modifier.weight(1f))
                    estado.distancia?.let {
                        Icon(Icons.Filled.NearMe, contentDescription = null, tint = AzulPrimario, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(2.dp))
                        Text("A $it", style = MaterialTheme.typography.labelLarge, color = AzulPrimario)
                    }
                }

                Text(punto.descripcion, style = MaterialTheme.typography.bodyLarge)

                // ---------- Información turística ----------
                if (punto.infoTuristica.isNotBlank()) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AzulClaro)
                    ) {
                        Row(Modifier.padding(16.dp)) {
                            Icon(Icons.Filled.Info, contentDescription = null, tint = AzulPrimario)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Dato turístico", style = MaterialTheme.typography.titleSmall, color = AzulOscuro)
                                Spacer(Modifier.height(4.dp))
                                Text(punto.infoTuristica, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                // ---------- Cápsulas de IA ----------
                Text("Cápsulas de IA en este punto", style = MaterialTheme.typography.titleMedium, color = AzulOscuro)
                if (estado.contenidos.isEmpty()) {
                    Text(
                        "Aún no hay cápsulas publicadas aquí.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                estado.contenidos.forEach { item ->
                    FilaContenido(item = item, onVer = { onVerVideo(item.contenido.id) })
                }

                // ---------- Bonificación selfie ----------
                if (punto.esHitoTuristico) {
                    TarjetaSelfie(
                        tieneSelfie = estado.tieneSelfie,
                        habilitada = estado.puedeTomarSelfie,
                        onTomarSelfie = { onTomarSelfie(punto.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun Encabezado(punto: PuntoEntity, onVolver: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(Brush.linearGradient(listOf(AzulPrimario, Morado)))
    ) {
        Icon(
            imageVector = if (punto.esHitoTuristico) Icons.Filled.AccountBalance else Icons.Filled.Place,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.9f),
            modifier = Modifier
                .size(96.dp)
                .align(Alignment.Center)
        )
        IconButton(
            onClick = onVolver,
            modifier = Modifier
                .statusBarsPadding()
                .padding(8.dp),
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White.copy(alpha = 0.9f))
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = AzulOscuro)
        }
    }
}

@Composable
private fun FilaContenido(item: ContenidoConEstado, onVer: () -> Unit) {
    val c = item.contenido
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (item.descubierto) VerdeClaro else AzulClaro),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        item.visto -> Icons.Filled.CheckCircle
                        item.descubierto -> Icons.Filled.PlayCircle
                        else -> Icons.Filled.Lock
                    },
                    contentDescription = null,
                    tint = if (item.descubierto) VerdeExito else AzulPrimario
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(c.titulo, style = MaterialTheme.typography.titleSmall, color = AzulOscuro)
                Text(
                    listOf(c.tema, c.duracion).filter { it.isNotBlank() }.joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!item.descubierto) {
                    Text(
                        "Escanea el QR para desbloquear (+${c.puntos} pts)",
                        style = MaterialTheme.typography.labelSmall,
                        color = AzulPrimario
                    )
                }
            }
            if (item.descubierto) {
                TextButton(onClick = onVer) { Text(if (item.visto) "Repetir" else "Ver") }
            }
        }
    }
}

@Composable
private fun TarjetaSelfie(tieneSelfie: Boolean, habilitada: Boolean, onTomarSelfie: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (tieneSelfie) VerdeClaro else NaranjoClaro)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (tieneSelfie) Icons.Filled.CheckCircle else Icons.Filled.CameraAlt,
                contentDescription = null,
                tint = if (tieneSelfie) VerdeExito else Naranjo,
                modifier = Modifier.size(36.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    if (tieneSelfie) "¡Selfie tomada! +100 puntos" else "Selfie turística: +100 puntos",
                    style = MaterialTheme.typography.titleSmall,
                    color = AzulOscuro
                )
                if (!tieneSelfie) {
                    Text(
                        if (habilitada) "Tómate una foto con el QR de fondo"
                        else "Primero escanea el QR de este lugar",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (!tieneSelfie) {
                Button(onClick = onTomarSelfie, enabled = habilitada) { Text("Selfie") }
            }
        }
    }
}
