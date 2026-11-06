package com.triskeledu.aprendoia.ui.historial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.triskeledu.aprendoia.data.local.entity.HistorialEntity
import com.triskeledu.aprendoia.data.local.entity.TipoActividad
import com.triskeledu.aprendoia.ui.theme.AzulClaro
import com.triskeledu.aprendoia.ui.theme.AzulOscuro
import com.triskeledu.aprendoia.ui.theme.AzulPrimario
import com.triskeledu.aprendoia.ui.theme.Morado
import com.triskeledu.aprendoia.ui.theme.Naranjo
import com.triskeledu.aprendoia.ui.theme.NaranjoClaro
import com.triskeledu.aprendoia.ui.theme.VerdeClaro
import com.triskeledu.aprendoia.ui.theme.VerdeExito
import com.triskeledu.aprendoia.util.comoHora
import com.triskeledu.aprendoia.util.conSeparador

/**
 * Pantalla 13: Historial de actividades.
 * Usa CenterAlignedTopAppBar (ejemplo de Barras): título centrado que cambia
 * de color al hacer scroll (pinnedScrollBehavior).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHistorial(
    onVolver: () -> Unit,
    viewModel: HistorialViewModel = viewModel(factory = HistorialViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val comportamiento = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(comportamiento.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Historial", color = AzulOscuro, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = AzulOscuro)
                    }
                },
                scrollBehavior = comportamiento,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
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

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 4.dp,
                bottom = padding.calculateBottomPadding() + 16.dp,
                start = 20.dp, end = 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ---------- Filtros ----------
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(FiltroHistorial.entries) { filtro ->
                        FilterChip(
                            selected = estado.filtro == filtro,
                            onClick = { viewModel.onFiltro(filtro) },
                            label = { Text(filtro.etiqueta) }
                        )
                    }
                }
            }
            item {
                Text(
                    "Puntos en esta vista: ${estado.totalPuntos.conSeparador()}",
                    style = MaterialTheme.typography.labelLarge,
                    color = AzulPrimario
                )
            }

            if (estado.grupos.isEmpty()) {
                item { SinActividad() }
            }

            // ---------- Grupos por día ----------
            estado.grupos.forEach { grupo ->
                item(key = "titulo_${grupo.titulo}") {
                    Text(
                        grupo.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        color = AzulOscuro,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                items(grupo.actividades, key = { it.id }) { actividad ->
                    FilaActividad(actividad, Modifier.animateItem())
                }
            }
        }
    }
}

/** Ícono y colores según el tipo de actividad. */
private data class EstiloActividad(val icono: ImageVector, val color: Color, val fondo: Color)

private fun estiloDe(tipo: TipoActividad) = when (tipo) {
    TipoActividad.QR -> EstiloActividad(Icons.Filled.QrCode2, AzulPrimario, AzulClaro)
    TipoActividad.VIDEO -> EstiloActividad(Icons.Filled.PlayCircle, Morado, Color(0xFFEDE7FF))
    TipoActividad.SELFIE -> EstiloActividad(Icons.Filled.CameraAlt, Naranjo, NaranjoClaro)
    TipoActividad.VISITA -> EstiloActividad(Icons.Filled.Place, VerdeExito, VerdeClaro)
}

@Composable
private fun FilaActividad(actividad: HistorialEntity, modifier: Modifier = Modifier) {
    val estilo = estiloDe(actividad.tipo)
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(estilo.fondo),
                contentAlignment = Alignment.Center
            ) {
                Icon(estilo.icono, contentDescription = null, tint = estilo.color)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(actividad.titulo, style = MaterialTheme.typography.titleSmall, color = AzulOscuro)
                if (actividad.detalle.isNotBlank()) {
                    Text(
                        actividad.detalle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
                Text(actividad.fecha.comoHora(), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            if (actividad.puntos > 0) {
                Text(
                    "+${actividad.puntos}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = VerdeExito
                )
            }
        }
    }
}

@Composable
private fun SinActividad() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Filled.History, contentDescription = null, tint = AzulPrimario.copy(alpha = 0.5f), modifier = Modifier.size(64.dp))
        Spacer(Modifier.size(12.dp))
        Text(
            "Todavía no hay actividades.\nEscanea un QR para comenzar tu aventura.",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
