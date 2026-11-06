package com.triskeledu.aprendoia.ui.escaner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.triskeledu.aprendoia.ui.theme.AzulOscuro
import com.triskeledu.aprendoia.ui.theme.AzulPrimario
import com.triskeledu.aprendoia.ui.theme.VerdeExito
import com.triskeledu.aprendoia.util.LectorQr

/**
 * Pantalla 7: Escáner QR.
 * El botón abre el escáner de Google (igual que en el ejemplo del curso);
 * el marco animado de esta pantalla es solo visual.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEscaner(
    onVolver: () -> Unit,
    onResultado: (contenidoId: Long, puntosGanados: Int) -> Unit,
    viewModel: EscanerViewModel = viewModel(factory = EscanerViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(estado.irAResultado) {
        estado.irAResultado?.let {
            viewModel.onNavegacionRealizada()
            onResultado(it.contenidoId, it.puntosGanados)
        }
    }

    fun abrirEscaner() {
        LectorQr.escanear(
            context = context,
            alLeer = viewModel::onTextoLeido,
            alFallar = viewModel::onEscaneoFallido
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Escanear QR", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = AzulOscuro
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(AzulOscuro, Color(0xFF14386F))))
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                "Encuentra el código QR en el punto turístico",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            MarcoEscaneo(modifier = Modifier.size(260.dp))

            Text(
                estado.mensaje,
                color = if (estado.esError) Color(0xFFFFB4AB) else Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = ::abrirEscaner,
                enabled = !estado.procesando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                if (estado.procesando) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                } else {
                    Icon(Icons.Filled.CameraAlt, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Abrir cámara y escanear", style = MaterialTheme.typography.titleMedium)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.QrCode2, contentDescription = null, tint = VerdeExito)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Cada QR nuevo suma +10 puntos y desbloquea una cápsula de IA",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // ---------- Ingreso manual ----------
            TextButton(onClick = viewModel::onAlternarIngresoManual) {
                Icon(Icons.Filled.Keyboard, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(6.dp))
                Text("¿Problemas con la cámara? Ingresa el código", color = Color.White)
            }
            AnimatedVisibility(visible = estado.mostrarIngresoManual) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = estado.codigoManual,
                        onValueChange = viewModel::onCodigoManualChange,
                        placeholder = { Text("Ej: QR-PLAZA-ARMAS") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { viewModel.onConfirmarCodigoManual() }),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                    Button(
                        onClick = viewModel::onConfirmarCodigoManual,
                        enabled = !estado.procesando,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text("Validar código", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

/** Marco con esquinas y una línea que recorre el área (animación de "escaneo"). */
@Composable
private fun MarcoEscaneo(modifier: Modifier = Modifier) {
    val avance by rememberInfiniteTransition(label = "escaneo").animateFloat(
        initialValue = 0.08f,
        targetValue = 0.92f,
        animationSpec = infiniteRepeatable(tween(1800, easing = LinearEasing), RepeatMode.Reverse),
        label = "linea"
    )

    Box(modifier, contentAlignment = Alignment.Center) {
        Icon(
            Icons.Filled.QrCode2,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.18f),
            modifier = Modifier.size(170.dp)
        )
        Canvas(Modifier.fillMaxSize()) {
            val largo = size.minDimension * 0.18f
            val grosor = 6.dp.toPx()
            val esquinas = listOf(
                Offset(0f, 0f) to Pair(1f, 1f),
                Offset(size.width, 0f) to Pair(-1f, 1f),
                Offset(0f, size.height) to Pair(1f, -1f),
                Offset(size.width, size.height) to Pair(-1f, -1f)
            )
            esquinas.forEach { (origen, dir) ->
                drawLine(Color.White, origen, origen + Offset(largo * dir.first, 0f), grosor, StrokeCap.Round)
                drawLine(Color.White, origen, origen + Offset(0f, largo * dir.second), grosor, StrokeCap.Round)
            }
            // Línea de escaneo con brillo
            val y = size.height * avance
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(Color.Transparent, AzulPrimario.copy(alpha = 0.35f)),
                    startY = y - 40.dp.toPx(),
                    endY = y
                ),
                topLeft = Offset(12.dp.toPx(), y - 40.dp.toPx()),
                size = Size(size.width - 24.dp.toPx(), 40.dp.toPx())
            )
            drawLine(
                VerdeExito,
                Offset(12.dp.toPx(), y),
                Offset(size.width - 12.dp.toPx(), y),
                3.dp.toPx(),
                StrokeCap.Round
            )
        }
    }
}
