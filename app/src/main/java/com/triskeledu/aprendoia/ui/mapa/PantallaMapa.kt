package com.triskeledu.aprendoia.ui.mapa

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.triskeledu.aprendoia.ui.theme.AzulOscuro
import com.triskeledu.aprendoia.ui.theme.AzulPrimario
import com.triskeledu.aprendoia.ui.theme.Naranjo
import com.triskeledu.aprendoia.util.Coordenada
import com.triskeledu.aprendoia.util.Permisos
import com.triskeledu.aprendoia.util.Ubicacion
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

/** Colores de los marcadores (se usan también en la leyenda). */
val ColorPendiente = AzulPrimario
val ColorCompletado = Naranjo

// =====================================================================
// PantallaMapa.kt  —  Pantalla 5
//
//   ┌──────────────────────────────┐
//   │ [🔍 Buscar un punto…     ✕ ] │ ← búsqueda (+ resultados)
//   │ (Todos 8)(Pendientes 6)(...) │ ← filtros
//   │                              │
//   │            MAPA          [+] │
//   │  ● Pendiente             [−] │
//   │  ● Completado            [◎] │
//   │ ┌──────────────────────────┐ │
//   │ │ Plaza de Armas  a 350 m  │ │ ← TarjetaPunto (si hay uno elegido)
//   │ └──────────────────────────┘ │
//   └──────────────────────────────┘
// =====================================================================

@Composable
fun PantallaMapa(
    onVerDetalle: (Long) -> Unit,
    viewModel: MapaViewModel = viewModel(factory = MapaViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // ---------- 1. El mapa y sus capas (una sola vez) ----------
    val mapa = rememberMapView()
    val capaMiUbicacion = remember { mapa.crearCapaMiUbicacion() }
    val iconoPendiente = remember { crearIconoPin(context, ColorPendiente.toArgb()) }
    val iconoCompletado = remember { crearIconoPin(context, ColorCompletado.toArgb()) }
    val marcadores = remember { mutableListOf<Marker>() }

    // ---------- 2. Ubicación ----------
    fun activarUbicacion(centrar: Boolean) {
        capaMiUbicacion.enableMyLocation()
        Ubicacion.ultimaConocida(context)?.let { viewModel.onUbicacion(it) }
        if (!centrar) return
        val conocida = capaMiUbicacion.myLocation
        if (conocida != null) {
            mapa.irA(conocida)
        } else {
            Toast.makeText(context, "Buscando tu ubicación…", Toast.LENGTH_SHORT).show()
            capaMiUbicacion.runOnFirstFix {
                // runOnFirstFix corre en otro hilo: "post" vuelve al hilo principal.
                mapa.post { capaMiUbicacion.myLocation?.let { mapa.irA(it) } }
            }
        }
    }

    val pedirPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { respuestas ->
        if (respuestas.values.any { it }) activarUbicacion(centrar = true)
        else Toast.makeText(context, "Sin permiso de ubicación no puedo mostrar distancias", Toast.LENGTH_LONG).show()
    }

    fun alTocarMiUbicacion() {
        if (Permisos.tieneUbicacion(context)) activarUbicacion(centrar = true)
        else pedirPermiso.launch(Permisos.UBICACION)
    }

    // Al abrir: toques en zonas vacías cierran la tarjeta; si ya hay permiso, se activa el GPS.
    LaunchedEffect(Unit) {
        mapa.alTocarMapa { viewModel.onCerrarSeleccion() }
        if (Permisos.tieneUbicacion(context)) activarUbicacion(centrar = false)
        else pedirPermiso.launch(Permisos.UBICACION)
    }

    // Cada 3 segundos se informa la posición al ViewModel (para las distancias).
    LaunchedEffect(Unit) {
        while (isActive) {
            capaMiUbicacion.myLocation?.let { viewModel.onUbicacion(it.aCoordenada()) }
            delay(3_000)
        }
    }
    DisposableEffect(Unit) { onDispose { capaMiUbicacion.disableMyLocation() } }

    // ---------- 3. Marcadores: se redibujan cuando cambian los puntos o el filtro ----------
    LaunchedEffect(estado.puntosVisibles) {
        marcadores.forEach { mapa.overlays.remove(it) }
        marcadores.clear()
        estado.puntosVisibles.forEach { item ->
            val marcador = Marker(mapa).apply {
                position = GeoPoint(item.punto.latitud, item.punto.longitud)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = if (item.descubierto) iconoCompletado else iconoPendiente
                title = item.punto.nombre
                setOnMarkerClickListener { _, _ ->
                    viewModel.onPuntoSeleccionado(item.punto.id)
                    true // true = no mostrar la burbuja por defecto de osmdroid
                }
            }
            mapa.overlays.add(marcador)
            marcadores.add(marcador)
        }
        mapa.invalidate()
    }

    // Al elegir un punto (marcador o búsqueda), la cámara vuela hasta él.
    LaunchedEffect(estado.seleccionadoId) {
        estado.seleccionado?.let { mapa.irA(GeoPoint(it.punto.latitud, it.punto.longitud)) }
    }

    // ---------- 4. Interfaz ----------
    Box(Modifier.fillMaxSize()) {
        VistaMapa(mapView = mapa, modifier = Modifier.fillMaxSize())

        // Arriba: búsqueda + filtros
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BarraBusquedaPuntos(
                texto = estado.busqueda,
                onTextoCambia = viewModel::onBusquedaChange,
                onLimpiar = viewModel::onLimpiarBusqueda
            )
            if (estado.mostrarResultados) {
                ResultadosBusqueda(
                    estado = estado,
                    onElegir = viewModel::onPuntoSeleccionado
                )
            } else {
                FiltrosMapa(estado = estado, onFiltro = viewModel::onFiltroChange)
            }
        }

        // Abajo: leyenda, botones y tarjeta
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Leyenda()
                    // Crédito obligatorio de OpenStreetMap
                    Text(
                        "© OpenStreetMap",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.DarkGray,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    BotonMapa(Icons.Filled.Add, "Acercar") { mapa.controller.zoomIn() }
                    BotonMapa(Icons.Filled.Remove, "Alejar") { mapa.controller.zoomOut() }
                    BotonMapa(Icons.Filled.MyLocation, "Mi ubicación", destacado = true) { alTocarMiUbicacion() }
                }
            }

            AnimatedVisibility(
                visible = estado.seleccionado != null,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it }
            ) {
                estado.seleccionado?.let { punto ->
                    TarjetaPunto(
                        punto = punto,
                        distancia = estado.distanciaA(punto),
                        onVerDetalle = { onVerDetalle(punto.punto.id) },
                        onComoLlegar = {
                            Ubicacion.abrirComoLlegar(
                                context,
                                Coordenada(punto.punto.latitud, punto.punto.longitud),
                                punto.punto.nombre
                            )
                        },
                        onCerrar = viewModel::onCerrarSeleccion
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------
// Componentes de la pantalla
// ---------------------------------------------------------------------

@Composable
private fun BarraBusquedaPuntos(texto: String, onTextoCambia: (String) -> Unit, onLimpiar: () -> Unit) {
    OutlinedTextField(
        value = texto,
        onValueChange = onTextoCambia,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Buscar un punto en Santiago…") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (texto.isNotEmpty()) {
                IconButton(onClick = onLimpiar) { Icon(Icons.Filled.Clear, contentDescription = "Limpiar") }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
private fun ResultadosBusqueda(estado: MapaUiState, onElegir: (Long) -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        if (estado.resultadosBusqueda.isEmpty()) {
            Text(
                "No hay puntos que coincidan",
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(Modifier.heightIn(max = 260.dp)) {
                items(estado.resultadosBusqueda, key = { it.punto.id }) { item ->
                    ListItem(
                        headlineContent = { Text(item.punto.nombre) },
                        supportingContent = {
                            Text(estado.distanciaA(item)?.let { "A $it" } ?: item.punto.descripcion, maxLines = 1)
                        },
                        leadingContent = { PuntoColor(if (item.descubierto) ColorCompletado else ColorPendiente) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier.clickable { onElegir(item.punto.id) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
private fun FiltrosMapa(estado: MapaUiState, onFiltro: (FiltroMapa) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FiltroMapa.entries.forEach { filtro ->
            FilterChip(
                selected = estado.filtro == filtro,
                onClick = { onFiltro(filtro) },
                label = { Text("${filtro.etiqueta} ${estado.contar(filtro)}") },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White,
                    selectedContainerColor = AzulPrimario,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
private fun Leyenda() {
    Surface(
        color = Color.White.copy(alpha = 0.92f),
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Column(Modifier.padding(horizontal = 10.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            FilaLeyenda(ColorPendiente, "Pendiente")
            FilaLeyenda(ColorCompletado, "Completado")
        }
    }
}

@Composable
private fun FilaLeyenda(color: Color, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        PuntoColor(color)
        Spacer(Modifier.width(6.dp))
        Text(texto, style = MaterialTheme.typography.labelSmall, color = AzulOscuro)
    }
}

@Composable
fun PuntoColor(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(12.dp)
            .background(color, CircleShape)
    )
}

@Composable
private fun BotonMapa(
    icono: ImageVector,
    descripcion: String,
    destacado: Boolean = false,
    onClick: () -> Unit
) {
    SmallFloatingActionButton(
        onClick = onClick,
        containerColor = if (destacado) AzulPrimario else Color.White,
        contentColor = if (destacado) Color.White else AzulOscuro
    ) {
        Icon(icono, contentDescription = descripcion)
    }
}
