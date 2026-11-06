package com.triskeledu.aprendoia.ui.bienvenida

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.triskeledu.aprendoia.ui.theme.AzulOscuro
import com.triskeledu.aprendoia.ui.theme.AzulPrimario

private data class Beneficio(val icono: ImageVector, val texto: String)

private val beneficios = listOf(
    Beneficio(Icons.Filled.PlayCircle, "Aprende con videos cortos"),
    Beneficio(Icons.Filled.LocationOn, "Recorre lugares icónicos"),
    Beneficio(Icons.Filled.EmojiEvents, "Gana puntos y desbloquea desafíos")
)


@Composable
fun PantallaBienvenida(
    onComenzar: () -> Unit,
    onTengoCuenta: () -> Unit
) {
    // Se pone en true al entrar para disparar las animaciones de entrada.
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val infinita = rememberInfiniteTransition(label = "bienvenida")
    val flotacion by infinita.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(tween(1600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "flotacion"
    )
    val pulso by infinita.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "pulso"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFA9D2FF), Color(0xFF6FA8EC), Color(0xFF2F6FC6))
                )
            )
    ) {
        // Ciudad de fondo
        SiluetaSantiago(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.42f)
                .align(Alignment.Center)
                .offset(y = 20.dp)
        )
        // Degradado oscuro inferior para que el texto blanco se lea bien
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.45f to Color.Transparent,
                        0.70f to AzulOscuro.copy(alpha = 0.85f),
                        1f to AzulOscuro
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            // Logo
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(700)) + scaleIn(tween(700), initialScale = 0.5f)
            ) {
                Box(
                    modifier = Modifier
                        .offset(y = flotacion.dp)
                        .size(92.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.School,
                        contentDescription = "Logo Triskeledu",
                        tint = AzulOscuro,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Título
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(700, delayMillis = 250)) +
                    slideInVertically(tween(700, delayMillis = 250)) { -it / 2 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Aprendo IA\nJugando en",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = AzulOscuro,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Santiago",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = AzulOscuro
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Descripción
            AnimatedVisibility(visible = visible, enter = fadeIn(tween(600, delayMillis = 500))) {
                Text(
                    "Descubre, aprende y gana puntos\nescaneando códigos QR en la ciudad",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(20.dp))

            // Beneficios, uno tras otro
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                beneficios.forEachIndexed { indice, beneficio ->
                    val retraso = 700 + indice * 200
                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(500, delayMillis = retraso)) +
                            slideInHorizontally(tween(500, delayMillis = retraso)) { -it / 3 }
                    ) {
                        FilaBeneficio(beneficio)
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = onComenzar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .scale(pulso),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario)
            ) {
                Text("Comenzar", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onTengoCuenta,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = AzulPrimario
                )
            ) {
                Text("Ya tengo una cuenta", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FilaBeneficio(beneficio: Beneficio) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(beneficio.icono, contentDescription = null, tint = AzulOscuro, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(14.dp))
        Text(beneficio.texto, color = Color.White, style = MaterialTheme.typography.bodyMedium)
    }
}
