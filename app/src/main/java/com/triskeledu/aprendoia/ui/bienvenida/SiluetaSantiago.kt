package com.triskeledu.aprendoia.ui.bienvenida

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path

/**
 * Silueta de Santiago dibujada con Canvas: Cordillera de los Andes con nieve,
 * la Gran Torre y edificios. Evita depender de una imagen externa.
 */
@Composable
fun SiluetaSantiago(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // --- Cordillera ---
        val picos = listOf(
            0f to 0.46f, 0.12f to 0.20f, 0.23f to 0.34f, 0.37f to 0.10f, 0.50f to 0.30f,
            0.64f to 0.14f, 0.78f to 0.32f, 0.90f to 0.18f, 1f to 0.34f
        )
        val cordillera = Path().apply {
            moveTo(0f, h)
            picos.forEach { (x, y) -> lineTo(w * x, h * y) }
            lineTo(w, h)
            close()
        }
        drawPath(cordillera, Color(0xFFC9DDF5))

        // Nieve en las cumbres más altas
        listOf(0.12f to 0.20f, 0.37f to 0.10f, 0.64f to 0.14f, 0.90f to 0.18f).forEach { (x, y) ->
            val nieve = Path().apply {
                moveTo(w * x, h * y)
                lineTo(w * (x - 0.035f), h * (y + 0.06f))
                lineTo(w * (x - 0.01f), h * (y + 0.05f))
                lineTo(w * (x + 0.012f), h * (y + 0.065f))
                lineTo(w * (x + 0.035f), h * (y + 0.06f))
                close()
            }
            drawPath(nieve, Color.White)
        }

        // --- Edificios del fondo ---
        val fondo = Color(0xFF5B88C4)
        listOf(
            Triple(0.00f, 0.10f, 0.30f), Triple(0.09f, 0.07f, 0.42f), Triple(0.17f, 0.09f, 0.36f),
            Triple(0.27f, 0.06f, 0.50f), Triple(0.34f, 0.10f, 0.40f), Triple(0.45f, 0.08f, 0.55f),
            Triple(0.72f, 0.09f, 0.48f), Triple(0.82f, 0.07f, 0.38f), Triple(0.90f, 0.10f, 0.44f)
        ).forEach { (x, ancho, alto) ->
            drawRect(fondo, topLeft = Offset(w * x, h * (1 - alto)), size = Size(w * ancho, h * alto))
        }

        // --- Gran Torre Santiago ---
        val torre = Path().apply {
            moveTo(w * 0.585f, h)
            lineTo(w * 0.600f, h * 0.12f)
            lineTo(w * 0.640f, h * 0.07f)
            lineTo(w * 0.660f, h * 0.12f)
            lineTo(w * 0.675f, h)
            close()
        }
        drawPath(torre, Color(0xFF2F5F9E))
        // Brillo vertical de la torre
        drawRect(
            Color.White.copy(alpha = 0.25f),
            topLeft = Offset(w * 0.628f, h * 0.10f),
            size = Size(w * 0.008f, h * 0.90f)
        )

        // --- Edificios del frente ---
        val frente = Color(0xFF244C85)
        listOf(
            Triple(0.03f, 0.11f, 0.22f), Triple(0.15f, 0.09f, 0.28f), Triple(0.26f, 0.12f, 0.20f),
            Triple(0.40f, 0.08f, 0.32f), Triple(0.49f, 0.09f, 0.24f), Triple(0.69f, 0.10f, 0.30f),
            Triple(0.80f, 0.12f, 0.22f), Triple(0.92f, 0.08f, 0.27f)
        ).forEach { (x, ancho, alto) ->
            drawRect(frente, topLeft = Offset(w * x, h * (1 - alto)), size = Size(w * ancho, h * alto))
            // Ventanas iluminadas
            var fila = h * (1 - alto) + h * 0.03f
            while (fila < h - h * 0.03f) {
                drawRect(
                    Color(0xFFFFE9A8).copy(alpha = 0.55f),
                    topLeft = Offset(w * (x + ancho * 0.25f), fila),
                    size = Size(w * ancho * 0.15f, h * 0.012f)
                )
                drawRect(
                    Color(0xFFFFE9A8).copy(alpha = 0.35f),
                    topLeft = Offset(w * (x + ancho * 0.60f), fila),
                    size = Size(w * ancho * 0.15f, h * 0.012f)
                )
                fila += h * 0.045f
            }
        }
    }
}
