package com.triskeledu.aprendoia.ui.resultadoqr

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.triskeledu.aprendoia.ui.theme.AzulClaro
import com.triskeledu.aprendoia.ui.theme.AzulOscuro
import com.triskeledu.aprendoia.ui.theme.AzulPrimario
import com.triskeledu.aprendoia.ui.theme.Naranjo
import com.triskeledu.aprendoia.ui.theme.NaranjoClaro
import com.triskeledu.aprendoia.ui.theme.VerdeClaro
import com.triskeledu.aprendoia.ui.theme.VerdeExito
import com.triskeledu.aprendoia.util.conSeparador

/**
 * Pantalla 8: Resultado del escaneo.
 * QR nuevo → ícono con rebote, confeti y "+10 puntos".
 * QR repetido → aviso de que ya estaba en la colección (sin sumar puntos).
 */
@Composable
fun PantallaResultadoQr(
    onVerVideo: (Long) -> Unit,
    onTomarSelfie: (Long) -> Unit,
    onEscanearOtro: () -> Unit,
    onVolverMapa: () -> Unit,
    viewModel: ResultadoQrViewModel = viewModel(factory = ResultadoQrViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    // El botón atrás del teléfono lleva al mapa (no de vuelta al escáner).
    BackHandler(onBack = onVolverMapa)

    val contenido = estado.contenido
    if (estado.cargando || contenido == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    // Rebote del ícono principal
    val escala = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        escala.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
    }
    val puntosAnimados by animateIntAsState(
        targetValue = estado.puntajeTotal,
        animationSpec = tween(1400, delayMillis = 300),
        label = "total"
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(if (estado.esNuevo) VerdeClaro else AzulClaro)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            // ---------- Ícono y título ----------
            Box(
                Modifier
                    .scale(escala.value)
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(if (estado.esNuevo) VerdeExito else AzulPrimario),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (estado.esNuevo) Icons.Filled.CheckCircle else Icons.Filled.Replay,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(72.dp)
                )
            }

            Text(
                if (estado.esNuevo) "¡QR encontrado!" else "Ya tenías este QR",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = AzulOscuro
            )

            if (estado.esNuevo) {
                Text(
                    "+${estado.puntosGanados} puntos",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = VerdeExito,
                    modifier = Modifier.scale(escala.value)
                )
            } else {
                Text(
                    "Esta cápsula ya está en tu colección, así que no suma puntos otra vez.",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                "Puntaje total: ${puntosAnimados.conSeparador()}",
                style = MaterialTheme.typography.titleMedium,
                color = AzulOscuro
            )

            // ---------- Cápsula desbloqueada ----------
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Cápsula desbloqueada", style = MaterialTheme.typography.labelLarge, color = AzulPrimario)
                    Text(contenido.titulo, style = MaterialTheme.typography.titleLarge, color = AzulOscuro)
                    if (contenido.descripcion.isNotBlank()) {
                        Text(contenido.descripcion, style = MaterialTheme.typography.bodyMedium)
                    }
                    estado.punto?.let { punto ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Place, contentDescription = null, tint = Naranjo, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(punto.nombre, style = MaterialTheme.typography.bodyMedium, color = AzulOscuro)
                        }
                    }
                }
            }

            // ---------- Desafío de selfie ----------
            if (estado.ofrecerSelfie) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = NaranjoClaro)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = null, tint = Naranjo, modifier = Modifier.size(32.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "¡Estás en un hito turístico! Tómate una selfie con el QR y gana 100 puntos extra.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AzulOscuro
                        )
                    }
                }
            }

            // ---------- Acciones ----------
            BotonAccion(Icons.Filled.PlayCircle, "Ver cápsula de IA", principal = true) { onVerVideo(contenido.id) }
            if (estado.ofrecerSelfie) {
                BotonAccion(Icons.Filled.CameraAlt, "Tomar selfie (+100)", colorFondo = Naranjo) {
                    estado.punto?.let { onTomarSelfie(it.id) }
                }
            }
            BotonAccion(Icons.Filled.Map, "Volver al mapa", principal = false, onClick = onVolverMapa)
            if (!estado.esNuevo) {
                BotonAccion(Icons.Filled.Replay, "Escanear otro QR", principal = false, onClick = onEscanearOtro)
            }
        }

        // Confeti encima de todo, solo si el QR es nuevo
        if (estado.esNuevo) {
            Confeti(Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun BotonAccion(
    icono: ImageVector,
    texto: String,
    principal: Boolean = true,
    colorFondo: Color = AzulPrimario,
    onClick: () -> Unit
) {
    val modificador = Modifier
        .fillMaxWidth()
        .height(52.dp)
    if (principal) {
        Button(
            onClick = onClick,
            modifier = modificador,
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorFondo)
        ) {
            Icon(icono, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(texto, style = MaterialTheme.typography.titleMedium)
        }
    } else {
        OutlinedButton(onClick = onClick, modifier = modificador, shape = RoundedCornerShape(26.dp)) {
            Icon(icono, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(texto, style = MaterialTheme.typography.titleMedium)
        }
    }
}
