package com.triskeledu.aprendoia.ui.resultadoqr

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

private data class Papelito(
    val x: Float,          // posición horizontal inicial (0..1)
    val velocidad: Float,  // qué tan rápido cae
    val vaiven: Float,     // amplitud del movimiento lateral
    val giro: Float,       // grados de rotación por vuelta
    val color: Color,
    val ancho: Float,
    val retraso: Float     // para que no caigan todos a la vez
)

private val coloresConfeti = listOf(
    Color(0xFF1E6FE8), Color(0xFF22A45D), Color(0xFFF59E0B),
    Color(0xFF6D4AE8), Color(0xFFE94B6A), Color(0xFFFFC83D)
)

/**
 * Lluvia de confeti dibujada con Canvas. Se reproduce una vez (3 segundos).
 * Solo se muestra cuando el QR es nuevo.
 */
@Composable
fun Confeti(modifier: Modifier = Modifier, cantidad: Int = 70) {
    val papelitos = remember {
        List(cantidad) {
            Papelito(
                x = Random.nextFloat(),
                velocidad = 0.6f + Random.nextFloat() * 0.8f,
                vaiven = 10f + Random.nextFloat() * 30f,
                giro = 180f + Random.nextFloat() * 540f,
                color = coloresConfeti.random(),
                ancho = 8f + Random.nextFloat() * 10f,
                retraso = Random.nextFloat() * 0.35f
            )
        }
    }
    val progreso = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progreso.animateTo(1f, tween(durationMillis = 3000, easing = LinearEasing))
    }

    Canvas(modifier) {
        val t = progreso.value
        papelitos.forEach { p ->
            val avance = ((t - p.retraso) / (1f - p.retraso)).coerceIn(0f, 1f)
            if (avance <= 0f || avance >= 1f) return@forEach
            val y = -40f + avance * p.velocidad * (size.height + 80f)
            val x = p.x * size.width + kotlin.math.sin(avance * 12f) * p.vaiven
            val alfa = if (avance > 0.8f) (1f - avance) / 0.2f else 1f
            rotate(degrees = avance * p.giro, pivot = Offset(x, y)) {
                drawRect(
                    color = p.color.copy(alpha = alfa),
                    topLeft = Offset(x - p.ancho / 2, y - p.ancho / 4),
                    size = Size(p.ancho, p.ancho / 2)
                )
            }
        }
    }
}
