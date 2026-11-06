package com.triskeledu.aprendoia.ui.video

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.triskeledu.aprendoia.ui.mapa.Etiqueta
import com.triskeledu.aprendoia.ui.theme.AzulClaro
import com.triskeledu.aprendoia.ui.theme.AzulOscuro
import com.triskeledu.aprendoia.ui.theme.AzulPrimario
import com.triskeledu.aprendoia.ui.theme.Morado
import com.triskeledu.aprendoia.ui.theme.VerdeClaro
import com.triskeledu.aprendoia.ui.theme.VerdeExito

/**
 * Pantalla 9: Video educativo (cápsula de IA).
 * Como en el ejemplo QR, el video se abre con LocalUriHandler (YouTube o navegador).
 * Si la cápsula aún no tiene URL, se muestra "Video aún no disponible".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaVideo(
    onVolver: () -> Unit,
    onIrAlPunto: (Long) -> Unit,
    viewModel: VideoViewModel = viewModel(factory = VideoViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val abridorDeLinks = LocalUriHandler.current
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(estado.mensaje) {
        estado.mensaje?.let {
            snackbar.showSnackbar(it)
            viewModel.onMensajeMostrado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cápsula de IA", color = AzulOscuro) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = AzulOscuro)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        val contenido = estado.contenido
        if (estado.cargando || contenido == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                if (!estado.cargando) Text("La cápsula no existe") else CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ---------- Área del reproductor ----------
            AreaVideo(
                desbloqueado = estado.desbloqueado,
                tieneVideo = estado.tieneVideo,
                onReproducir = {
                    abridorDeLinks.openUri(contenido.url)
                    viewModel.onMarcarVisto()
                }
            )

            // ---------- Datos de la cápsula ----------
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Etiqueta(contenido.tema, AzulClaro, AzulPrimario)
                if (estado.visto) Etiqueta("Vista ✓", VerdeClaro, VerdeExito)
            }
            Text(
                contenido.titulo,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = AzulOscuro
            )
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                if (contenido.autor.isNotBlank()) FilaDato(Icons.Filled.Person, contenido.autor)
                if (contenido.duracion.isNotBlank()) FilaDato(Icons.Filled.AccessTime, contenido.duracion)
                estado.punto?.let { FilaDato(Icons.Filled.Place, it.nombre) }
            }
            if (contenido.descripcion.isNotBlank()) {
                Text(contenido.descripcion, style = MaterialTheme.typography.bodyLarge)
            }

            // ---------- Acciones ----------
            if (estado.desbloqueado) {
                if (estado.visto) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = VerdeExito)
                        Spacer(Modifier.width(8.dp))
                        Text("Ya completaste esta cápsula", color = VerdeExito, fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    Button(
                        onClick = viewModel::onMarcarVisto,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeExito)
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Marcar como vista", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
            estado.punto?.let { punto ->
                OutlinedButton(
                    onClick = { onIrAlPunto(punto.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp)
                ) {
                    Icon(Icons.Filled.Place, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Ver punto: ${punto.nombre}")
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

/** Recuadro 16:9 donde iría el video. Tres estados: bloqueado, sin video, listo. */
@Composable
private fun AreaVideo(desbloqueado: Boolean, tieneVideo: Boolean, onReproducir: () -> Unit) {
    val pulso by rememberInfiniteTransition(label = "play").animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "pulso"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(AzulOscuro, Morado))),
        contentAlignment = Alignment.Center
    ) {
        when {
            !desbloqueado -> MensajeArea(Icons.Filled.Lock, "Escanea el QR del punto para desbloquear esta cápsula")
            !tieneVideo -> MensajeArea(Icons.Filled.VideocamOff, "Video aún no disponible.\nPronto el equipo de contenido lo publicará.")
            else -> IconButton(onClick = onReproducir, modifier = Modifier.size(96.dp).scale(pulso)) {
                Icon(Icons.Filled.PlayCircle, contentDescription = "Reproducir", tint = Color.White, modifier = Modifier.size(88.dp))
            }
        }
    }
}

@Composable
private fun MensajeArea(icono: ImageVector, texto: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
        Icon(icono, contentDescription = null, tint = Color.White.copy(alpha = 0.9f), modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(10.dp))
        Text(texto, color = Color.White, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun FilaDato(icono: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(texto, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
