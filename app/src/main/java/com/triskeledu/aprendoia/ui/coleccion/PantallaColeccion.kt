package com.triskeledu.aprendoia.ui.coleccion

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.triskeledu.aprendoia.data.local.relacion.DescubrimientoDetalle
import com.triskeledu.aprendoia.data.local.relacion.SelfieDetalle
import com.triskeledu.aprendoia.ui.theme.AzulClaro
import com.triskeledu.aprendoia.ui.theme.AzulOscuro
import com.triskeledu.aprendoia.ui.theme.AzulPrimario
import com.triskeledu.aprendoia.ui.theme.Morado
import com.triskeledu.aprendoia.ui.theme.Naranjo
import com.triskeledu.aprendoia.ui.theme.VerdeExito
import com.triskeledu.aprendoia.util.comoFecha

/**
 * Pantalla 11: Colección de descubrimientos.
 * Usa LargeTopAppBar (ejemplo de Barras): título grande que se encoge al hacer scroll.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaColeccion(
    onAbrirCapsula: (Long) -> Unit,
    onAbrirPunto: (Long) -> Unit,
    onIrMapa: () -> Unit,
    viewModel: ColeccionViewModel = viewModel(factory = ColeccionViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    // exitUntilCollapsed: la barra grande se achica al bajar y crece al volver arriba.
    val comportamiento = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(comportamiento.nestedScrollConnection),
        // La barra inferior ya ocupa el borde de abajo; la barra superior maneja la de estado.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            LargeTopAppBar(
                title = { Text("Mi Colección", color = AzulOscuro, fontWeight = FontWeight.Bold) },
                scrollBehavior = comportamiento,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (estado.cargando) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Scaffold
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp, bottom = 16.dp,
                top = padding.calculateTopPadding()
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ---------- Encabezado (ocupa las 2 columnas) ----------
            item(span = { GridItemSpan(maxLineSpan) }) {
                ResumenColeccion(estado)
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                TabRow(
                    selectedTabIndex = estado.pestana.ordinal,
                    containerColor = Color.Transparent
                ) {
                    PestanaColeccion.entries.forEach { pestana ->
                        val cantidad = if (pestana == PestanaColeccion.CAPSULAS) estado.capsulas.size else estado.selfies.size
                        Tab(
                            selected = estado.pestana == pestana,
                            onClick = { viewModel.onPestana(pestana) },
                            text = { Text("${pestana.titulo} ($cantidad)") }
                        )
                    }
                }
            }

            when (estado.pestana) {
                PestanaColeccion.CAPSULAS -> {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FiltroCapsulas.entries.forEach { filtro ->
                                FilterChip(
                                    selected = estado.filtro == filtro,
                                    onClick = { viewModel.onFiltro(filtro) },
                                    label = { Text(filtro.etiqueta) }
                                )
                            }
                        }
                    }
                    if (estado.capsulasVisibles.isEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Vacio(
                                Icons.Filled.CollectionsBookmark,
                                if (estado.capsulas.isEmpty()) "Aún no descubres cápsulas.\n¡Escanea tu primer QR!"
                                else "No hay cápsulas en este filtro.",
                                onIrMapa
                            )
                        }
                    }
                    items(estado.capsulasVisibles, key = { it.descubrimientoId }) { capsula ->
                        TarjetaCapsula(capsula, onClick = { onAbrirCapsula(capsula.contenidoId) })
                    }
                }

                PestanaColeccion.SELFIES -> {
                    if (estado.selfies.isEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Vacio(Icons.Filled.CameraAlt, "Toma selfies en los hitos turísticos\ny gana +100 puntos.", onIrMapa)
                        }
                    }
                    items(estado.selfies, key = { it.selfieId }) { selfie ->
                        TarjetaSelfie(selfie, viewModel, onClick = { onAbrirPunto(selfie.puntoId) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ResumenColeccion(estado: ColeccionUiState) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AzulClaro)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                "${estado.capsulas.size} de ${estado.totalCapsulas} cápsulas descubiertas",
                style = MaterialTheme.typography.titleMedium,
                color = AzulOscuro
            )
            LinearProgressIndicator(
                progress = { estado.avance },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                strokeCap = StrokeCap.Round,
                trackColor = Color.White
            )
        }
    }
}

@Composable
private fun TarjetaCapsula(capsula: DescubrimientoDetalle, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 10f)
                .background(Brush.linearGradient(listOf(AzulPrimario, Morado))),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.PlayCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
            if (capsula.visto) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = "Vista",
                    tint = VerdeExito,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.White, RoundedCornerShape(50))
                )
            }
        }
        Column(Modifier.padding(12.dp)) {
            Text(
                capsula.titulo,
                style = MaterialTheme.typography.titleSmall,
                color = AzulOscuro,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                capsula.puntoNombre,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "+${capsula.puntos} pts · ${capsula.fecha.comoFecha()}",
                style = MaterialTheme.typography.labelSmall,
                color = AzulPrimario
            )
        }
    }
}

@Composable
private fun TarjetaSelfie(selfie: SelfieDetalle, viewModel: ColeccionViewModel, onClick: () -> Unit) {
    val miniatura by produceState<Bitmap?>(initialValue = null, selfie.rutaFoto) {
        value = viewModel.cargarMiniatura(selfie.rutaFoto)
    }
    Card(onClick = onClick, shape = RoundedCornerShape(18.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .background(AzulClaro),
            contentAlignment = Alignment.Center
        ) {
            val imagen = miniatura
            if (imagen != null) {
                Image(
                    bitmap = imagen.asImageBitmap(),
                    contentDescription = "Selfie en ${selfie.puntoNombre}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(Icons.Filled.CameraAlt, contentDescription = null, tint = AzulPrimario)
            }
            // Franja inferior con el lugar
            Column(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))))
                    .padding(10.dp)
            ) {
                Text(selfie.puntoNombre, color = Color.White, style = MaterialTheme.typography.titleSmall, maxLines = 1)
                Text("+${selfie.puntos} · ${selfie.fecha.comoFecha()}", color = Naranjo, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun Vacio(icono: ImageVector, texto: String, onIrMapa: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icono, contentDescription = null, tint = AzulPrimario.copy(alpha = 0.5f), modifier = Modifier.size(64.dp))
        Text(texto, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        androidx.compose.material3.OutlinedButton(onClick = onIrMapa) { Text("Ir al mapa") }
    }
}
