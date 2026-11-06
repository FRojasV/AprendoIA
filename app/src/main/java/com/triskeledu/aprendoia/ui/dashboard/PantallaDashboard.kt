package com.triskeledu.aprendoia.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.triskeledu.aprendoia.ui.components.AvatarJugador
import com.triskeledu.aprendoia.ui.components.TarjetaContador
import com.triskeledu.aprendoia.ui.theme.AzulClaro
import com.triskeledu.aprendoia.ui.theme.AzulOscuro
import com.triskeledu.aprendoia.ui.theme.AzulPrimario
import com.triskeledu.aprendoia.ui.theme.Morado
import com.triskeledu.aprendoia.ui.theme.Naranjo
import com.triskeledu.aprendoia.ui.theme.NaranjoClaro
import com.triskeledu.aprendoia.ui.theme.VerdeClaro
import com.triskeledu.aprendoia.ui.theme.VerdeExito
import com.triskeledu.aprendoia.util.Ubicacion
import com.triskeledu.aprendoia.util.conSeparador
import androidx.compose.ui.platform.LocalContext
import kotlin.math.roundToInt

/**
 * Pantalla 4: Dashboard del Cliente.
 * Animaciones: secciones que aparecen en cascada, puntaje que cuenta hasta su
 * valor, barra de progreso que se llena y trofeo que se balancea.
 */
@Composable
fun PantallaDashboard(
    onIrMapa: () -> Unit,
    onIrEscaner: () -> Unit,
    onIrColeccion: () -> Unit,
    onIrProgreso: () -> Unit,
    onIrHistorial: () -> Unit,
    onIrNotificaciones: () -> Unit,
    onIrPerfil: () -> Unit,
    onIrDetallePunto: (Long) -> Unit,
    onSinSesion: () -> Unit,
    viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(estado.sinSesion) { if (estado.sinSesion) onSinSesion() }

    if (estado.cargando || estado.usuario == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    val usuario = estado.usuario!!

    // Revisión de "punto cercano" con la última ubicación conocida (si hay permiso).
    val context = LocalContext.current
    LaunchedEffect(usuario.id) {
        Ubicacion.ultimaConocida(context)?.let(viewModel::onUbicacion)
    }

    // Dispara la aparición en cascada de las secciones.
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // ---------- Saludo ----------
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    "¡Hola, ${usuario.alias}!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = AzulOscuro
                )
                Text(
                    "Sigue explorando Santiago",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onIrNotificaciones) {
                BadgedBox(
                    badge = {
                        if (estado.notificacionesNoLeidas > 0) {
                            Badge { Text("${estado.notificacionesNoLeidas}") }
                        }
                    }
                ) {
                    Icon(Icons.Filled.Notifications, contentDescription = "Notificaciones", tint = AzulOscuro)
                }
            }
            AvatarJugador(
                clave = usuario.avatar,
                tamano = 44.dp,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onIrPerfil)
            )
        }

        // ---------- Puntaje total ----------
        Seccion(visible, indice = 0) {
            TarjetaPuntaje(puntaje = estado.puntaje, onClick = onIrProgreso)
        }

        // ---------- Contadores ----------
        Seccion(visible, indice = 1) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TarjetaContador(estado.estadisticas.qrEncontrados, "QR encontrados", Modifier.weight(1f))
                TarjetaContador(estado.estadisticas.videosVistos, "Videos vistos", Modifier.weight(1f))
                TarjetaContador(estado.estadisticas.lugaresVisitados, "Lugares visitados", Modifier.weight(1f))
            }
        }

        // ---------- Progreso ----------
        Seccion(visible, indice = 2) {
            BarraProgresoLugares(
                progreso = estado.progresoLugares,
                descubiertos = estado.estadisticas.lugaresVisitados,
                total = estado.totalLugares
            )
        }

        // ---------- Desafío actual ----------
        Seccion(visible, indice = 3) {
            Text("Desafío actual", style = MaterialTheme.typography.titleMedium, color = AzulOscuro)
            Spacer(Modifier.height(8.dp))
            val desafio = estado.desafio
            if (desafio != null) {
                TarjetaDesafio(
                    titulo = "Visita ${desafio.nombre}",
                    detalle = "Escanea el QR y gana 100 puntos extra con una selfie",
                    onClick = { onIrDetallePunto(desafio.id) }
                )
            } else {
                TarjetaDesafioCompletado()
            }
        }

        // ---------- Accesos rápidos ----------
        Seccion(visible, indice = 4) {
            Text("Accesos rápidos", style = MaterialTheme.typography.titleMedium, color = AzulOscuro)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AccesoRapido(Icons.Filled.Map, "Mapa", onIrMapa, Modifier.weight(1f))
                AccesoRapido(Icons.Filled.QrCodeScanner, "Escanear QR", onIrEscaner, Modifier.weight(1f))
                AccesoRapido(Icons.Filled.CollectionsBookmark, "Colección", onIrColeccion, Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AccesoRapido(Icons.Filled.BarChart, "Mi progreso", onIrProgreso, Modifier.weight(1f))
                AccesoRapido(Icons.Filled.History, "Historial", onIrHistorial, Modifier.weight(1f))
                AccesoRapido(Icons.Filled.Notifications, "Avisos", onIrNotificaciones, Modifier.weight(1f))
            }
        }
    }
}

/** Envoltorio que hace aparecer una sección desde abajo, con retraso según su posición. */
@Composable
private fun Seccion(visible: Boolean, indice: Int, contenido: @Composable ColumnScope.() -> Unit) {
    val retraso = 120 * indice
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(450, delayMillis = retraso)) +
            slideInVertically(tween(450, delayMillis = retraso)) { it / 4 }
    ) {
        Column(content = contenido)
    }
}

@Composable
private fun TarjetaPuntaje(puntaje: Int, onClick: () -> Unit) {
    val puntajeAnimado by animateIntAsState(puntaje, tween(1200), label = "puntaje")
    val balanceo by rememberInfiniteTransition(label = "trofeo").animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label = "balanceo"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.horizontalGradient(listOf(AzulPrimario, Morado)))
            .clickable(onClick = onClick)
            .padding(horizontal = 22.dp, vertical = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Filled.EmojiEvents,
            contentDescription = null,
            tint = Color(0xFFFFC83D),
            modifier = Modifier
                .size(64.dp)
                .rotate(balanceo)
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(
                puntajeAnimado.conSeparador(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text("Puntos totales", color = Color.White.copy(alpha = 0.85f))
        }
    }
}

@Composable
private fun BarraProgresoLugares(progreso: Float, descubiertos: Int, total: Int) {
    val progresoAnimado by animateFloatAsState(progreso, tween(1100), label = "progreso")
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Tu progreso", style = MaterialTheme.typography.titleMedium, color = AzulOscuro, modifier = Modifier.weight(1f))
            Text(
                "${(progresoAnimado * 100).roundToInt()}%",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progresoAnimado },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = AzulClaro,
            strokeCap = StrokeCap.Round
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "$descubiertos de $total lugares descubiertos",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TarjetaDesafio(titulo: String, detalle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = NaranjoClaro),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Naranjo),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Place, contentDescription = null, tint = Color.White)
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(titulo, style = MaterialTheme.typography.titleMedium, color = AzulOscuro)
                Text(detalle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Naranjo)
        }
    }
}

@Composable
private fun TarjetaDesafioCompletado() {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = VerdeClaro),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = VerdeExito, modifier = Modifier.size(40.dp))
            Spacer(Modifier.width(14.dp))
            Text(
                "¡Completaste todos los desafíos turísticos!",
                style = MaterialTheme.typography.titleMedium,
                color = AzulOscuro
            )
        }
    }
}

@Composable
private fun AccesoRapido(icono: ImageVector, texto: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AzulClaro)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icono, contentDescription = null, tint = AzulPrimario, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(6.dp))
            Text(texto, style = MaterialTheme.typography.labelMedium, color = AzulOscuro, textAlign = TextAlign.Center)
        }
    }
}
