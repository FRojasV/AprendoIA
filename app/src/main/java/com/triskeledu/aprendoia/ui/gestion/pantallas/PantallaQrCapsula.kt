package com.triskeledu.aprendoia.ui.gestion.pantallas

import android.graphics.Bitmap
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.triskeledu.aprendoia.ui.gestion.viewmodel.QrCapsulaViewModel
import com.triskeledu.aprendoia.ui.theme.AzulOscuro
import com.triskeledu.aprendoia.util.GeneradorQr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Muestra el código QR de una cápsula, listo para imprimir y pegar en el punto.
 * Debajo se ve el JSON que contiene (como el "estado en vivo" del ejemplo Formularios).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaQrCapsula(
    onVolver: () -> Unit,
    viewModel: QrCapsulaViewModel = viewModel(factory = QrCapsulaViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val corrutinas = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(estado.mensaje) {
        estado.mensaje?.let { snackbar.showSnackbar(it); viewModel.mensajeMostrado() }
    }

    // El cartel se genera en segundo plano (puede tardar unos milisegundos).
    val cartel by produceState<Bitmap?>(initialValue = null, estado.contenidoQr) {
        value = if (estado.contenidoQr.isBlank()) null
        else withContext(Dispatchers.Default) {
            GeneradorQr.crearCartel(estado.contenidoQr, estado.puntoNombre, estado.codigo, estado.titulo)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Código QR", color = AzulOscuro) },
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
    ) { margen ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(margen)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(estado.titulo, style = MaterialTheme.typography.titleLarge, color = AzulOscuro, textAlign = TextAlign.Center)

            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1000f / 1200f),
                    contentAlignment = Alignment.Center
                ) {
                    val imagen = cartel
                    if (imagen == null) CircularProgressIndicator()
                    else Image(imagen.asImageBitmap(), contentDescription = "Código QR ${estado.codigo}", modifier = Modifier.fillMaxSize())
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = {
                        val imagen = cartel ?: return@OutlinedButton
                        corrutinas.launch {
                            val ok = GeneradorQr.guardarEnGaleria(context, imagen, estado.codigo)
                            viewModel.mostrarMensaje(
                                if (ok) "Guardado en Galería › Imágenes › AprendoIA_QR"
                                else "No se pudo guardar. Usa \"Compartir\"."
                            )
                        }
                    },
                    enabled = cartel != null,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Icon(Icons.Filled.Download, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Guardar")
                }
                Button(
                    onClick = {
                        val imagen = cartel ?: return@Button
                        corrutinas.launch { GeneradorQr.compartir(context, imagen, estado.codigo) }
                    },
                    enabled = cartel != null,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Compartir")
                }
            }

            // ---------- Contenido del QR ----------
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Contenido del QR (JSON)", style = MaterialTheme.typography.titleSmall)
                    Text(estado.contenidoQr, fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodySmall)
                }
            }
            Text(
                "Imprime el QR y pégalo en el punto. Los jugadores lo escanean desde la app para ganar puntos.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
