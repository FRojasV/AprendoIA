package com.triskeledu.aprendoia.ui.progreso

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.triskeledu.aprendoia.ui.theme.AzulClaro
import com.triskeledu.aprendoia.ui.theme.AzulOscuro
import com.triskeledu.aprendoia.ui.theme.AzulPrimario
import com.triskeledu.aprendoia.ui.theme.Morado
import com.triskeledu.aprendoia.util.conSeparador

/**
 * Anillo de progreso dibujado con Canvas. El arco crece con animación
 * hasta [avance] (0..1) y en el centro se muestra el puntaje.
 */
@Composable
fun AnilloProgreso(
    avance: Float,
    puntaje: Int,
    subtitulo: String,
    tamano: Dp = 200.dp
) {
    val animado = remember { Animatable(0f) }
    LaunchedEffect(avance) {
        animado.animateTo(avance, tween(1400, easing = FastOutSlowInEasing))
    }
    Box(Modifier.size(tamano), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val grosor = 18.dp.toPx()
            val lado = size.minDimension - grosor
            val esquina = Offset((size.width - lado) / 2, (size.height - lado) / 2)
            // Pista gris
            drawArc(
                color = AzulClaro,
                startAngle = 135f, sweepAngle = 270f, useCenter = false,
                topLeft = esquina, size = Size(lado, lado),
                style = Stroke(grosor, cap = StrokeCap.Round)
            )
            // Avance con degradado
            drawArc(
                brush = Brush.sweepGradient(listOf(AzulPrimario, Morado, AzulPrimario)),
                startAngle = 135f, sweepAngle = 270f * animado.value, useCenter = false,
                topLeft = esquina, size = Size(lado, lado),
                style = Stroke(grosor, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                (puntaje * (if (avance == 0f) 1f else animado.value / avance)).toInt().conSeparador(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = AzulOscuro
            )
            Text(subtitulo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/**
 * Gráfico de barras de los puntos de los últimos 7 días (Canvas).
 * Las barras crecen desde abajo al abrir la pantalla.
 */
@Composable
fun GraficoBarras(dias: List<PuntosDia>, modifier: Modifier = Modifier) {
    val crecimiento = remember { Animatable(0f) }
    LaunchedEffect(dias) {
        crecimiento.snapTo(0f)
        crecimiento.animateTo(1f, tween(900, easing = FastOutSlowInEasing))
    }
    val maximo = (dias.maxOfOrNull { it.puntos } ?: 0).coerceAtLeast(10)

    Column(modifier) {
        // Valores sobre cada barra
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            dias.forEach { dia ->
                Text(
                    if (dia.puntos > 0) "${dia.puntos}" else "",
                    style = MaterialTheme.typography.labelSmall,
                    color = AzulPrimario,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            val separacion = size.width / dias.size
            val ancho = separacion * 0.5f
            dias.forEachIndexed { i, dia ->
                val x = separacion * i + (separacion - ancho) / 2
                // Fondo de la barra
                drawRoundRect(
                    color = AzulClaro,
                    topLeft = Offset(x, 0f),
                    size = Size(ancho, size.height),
                    cornerRadius = CornerRadius(10f, 10f)
                )
                // Barra con el valor
                val alto = size.height * (dia.puntos.toFloat() / maximo) * crecimiento.value
                if (alto > 0f) {
                    drawRoundRect(
                        brush = Brush.verticalGradient(listOf(Morado, AzulPrimario)),
                        topLeft = Offset(x, size.height - alto),
                        size = Size(ancho, alto),
                        cornerRadius = CornerRadius(10f, 10f)
                    )
                }
            }
        }
        // Etiquetas de los días
        Row(Modifier.fillMaxWidth()) {
            dias.forEach { dia ->
                Text(
                    dia.etiqueta,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (dia.etiqueta == "Hoy") AzulPrimario else Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
