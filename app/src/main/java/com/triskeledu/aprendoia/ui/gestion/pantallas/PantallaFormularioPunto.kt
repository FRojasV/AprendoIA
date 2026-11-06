package com.triskeledu.aprendoia.ui.gestion.pantallas

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.triskeledu.aprendoia.ui.gestion.viewmodel.FormularioPuntoViewModel
import com.triskeledu.aprendoia.ui.mapa.VistaMapa
import com.triskeledu.aprendoia.ui.mapa.aCoordenada
import com.triskeledu.aprendoia.ui.mapa.crearIconoPin
import com.triskeledu.aprendoia.ui.mapa.rememberMapView
import com.triskeledu.aprendoia.ui.theme.AzulOscuro
import com.triskeledu.aprendoia.ui.theme.Naranjo
import com.triskeledu.aprendoia.ui.theme.NaranjoClaro
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

/**
 * Formulario de punto turístico (crear o editar).
 * Las coordenadas se escriben a mano o se eligen TOCANDO el mini mapa.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFormularioPunto(
    onVolver: () -> Unit,
    viewModel: FormularioPuntoViewModel = viewModel(factory = FormularioPuntoViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(estado.guardado) { if (estado.guardado) onVolver() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (estado.esEdicion) "Editar punto" else "Nuevo punto", color = AzulOscuro) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = AzulOscuro)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { margen ->
        if (estado.cargando) {
            Box(Modifier.fillMaxSize().padding(margen), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(margen)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CampoTexto("Nombre del lugar", estado.nombre, viewModel::cambiarNombre)
            CampoTexto("Descripción", estado.descripcion, viewModel::cambiarDescripcion, lineas = 2)
            CampoTexto(
                "Información turística (opcional)", estado.infoTuristica, viewModel::cambiarInfoTuristica,
                lineas = 3, ayuda = "Historia o datos curiosos del lugar"
            )

            // ---------- Ubicación ----------
            Text("Ubicación", style = MaterialTheme.typography.titleMedium, color = AzulOscuro)
            Text(
                "Toca el mapa para marcar el lugar exacto del código QR.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            MiniMapaSelector(
                lat = estado.coordenada?.latitud,
                lon = estado.coordenada?.longitud,
                onElegir = { viewModel.elegirEnMapa(it.aCoordenada()) }
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CampoTexto(
                    "Latitud", estado.latitud, viewModel::cambiarLatitud,
                    modifier = Modifier.weight(1f), tipoTeclado = KeyboardType.Decimal
                )
                CampoTexto(
                    "Longitud", estado.longitud, viewModel::cambiarLongitud,
                    modifier = Modifier.weight(1f), tipoTeclado = KeyboardType.Decimal
                )
            }

            // ---------- Hito turístico ----------
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = NaranjoClaro)) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Hito turístico", style = MaterialTheme.typography.titleSmall, color = AzulOscuro)
                        Text(
                            "Permite ganar +100 puntos con una selfie",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(checked = estado.esHitoTuristico, onCheckedChange = viewModel::cambiarHito)
                }
            }

            if (estado.intentoEnviar && !estado.formularioValido) {
                Text("Revisa los campos marcados en rojo.", color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = viewModel::enviar,
                enabled = !estado.guardando,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(26.dp)
            ) {
                if (estado.guardando) CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                else Text(if (estado.esEdicion) "Guardar cambios" else "Crear punto")
            }
        }
    }
}

/**
 * Mapa pequeño de osmdroid: un toque mueve el marcador naranjo y avisa la coordenada.
 * requestDisallowInterceptTouchEvent evita que el scroll de la pantalla "robe" los gestos del mapa.
 */
@SuppressLint("ClickableViewAccessibility")
@Composable
private fun MiniMapaSelector(lat: Double?, lon: Double?, onElegir: (GeoPoint) -> Unit) {
    val context = LocalContext.current
    val mapa = rememberMapView()
    val marcador = remember {
        Marker(mapa).apply {
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = crearIconoPin(context, Naranjo.toArgb())
            setInfoWindow(null)
        }
    }

    LaunchedEffect(Unit) {
        mapa.controller.setZoom(15.0)
        mapa.overlays.add(MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                p?.let(onElegir)
                return true
            }
            override fun longPressHelper(p: GeoPoint?): Boolean = false
        }))
        mapa.setOnTouchListener { vista, _ ->
            vista.parent?.requestDisallowInterceptTouchEvent(true)
            false
        }
    }

    // Cada vez que cambian las coordenadas, se mueve el marcador.
    LaunchedEffect(lat, lon) {
        if (lat != null && lon != null && lat in -90.0..90.0 && lon in -180.0..180.0) {
            val punto = GeoPoint(lat, lon)
            marcador.position = punto
            if (!mapa.overlays.contains(marcador)) mapa.overlays.add(marcador)
            mapa.controller.animateTo(punto)
            mapa.invalidate()
        }
    }

    VistaMapa(
        mapView = mapa,
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(RoundedCornerShape(16.dp))
    )
}
