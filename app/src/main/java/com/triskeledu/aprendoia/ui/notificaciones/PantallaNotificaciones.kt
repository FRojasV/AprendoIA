package com.triskeledu.aprendoia.ui.notificaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.triskeledu.aprendoia.data.local.entity.NotificacionEntity
import com.triskeledu.aprendoia.data.local.entity.TipoNotificacion
import com.triskeledu.aprendoia.ui.theme.AzulClaro
import com.triskeledu.aprendoia.ui.theme.AzulOscuro
import com.triskeledu.aprendoia.ui.theme.AzulPrimario
import com.triskeledu.aprendoia.ui.theme.Morado
import com.triskeledu.aprendoia.ui.theme.Naranjo
import com.triskeledu.aprendoia.ui.theme.NaranjoClaro
import com.triskeledu.aprendoia.ui.theme.RojoError
import com.triskeledu.aprendoia.ui.theme.VerdeClaro
import com.triskeledu.aprendoia.ui.theme.VerdeExito
import com.triskeledu.aprendoia.util.haceCuanto

/**
 * Pantalla 14: Notificaciones.
 * Usa la TopAppBar básica (ejemplo de Barras) con una acción "Marcar todas".
 * Al tocar un aviso se marca como leído y se abre la pantalla relacionada.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaNotificaciones(
    onVolver: () -> Unit,
    onNavegar: (String) -> Unit,
    viewModel: NotificacionesViewModel = viewModel(factory = NotificacionesViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(estado.irA) {
        estado.irA?.let {
            viewModel.onNavegacionRealizada()
            onNavegar(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Notificaciones", color = AzulOscuro, fontWeight = FontWeight.Bold)
                        if (estado.noLeidas > 0) {
                            Spacer(Modifier.width(8.dp))
                            Badge(containerColor = RojoError) { Text("${estado.noLeidas}") }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = AzulOscuro)
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::onMarcarTodas, enabled = estado.noLeidas > 0) {
                        Icon(Icons.Filled.DoneAll, contentDescription = "Marcar todas como leídas")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (estado.cargando) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FiltroNotificaciones.entries.forEach { filtro ->
                        FilterChip(
                            selected = estado.filtro == filtro,
                            onClick = { viewModel.onFiltro(filtro) },
                            label = {
                                Text(
                                    if (filtro == FiltroNotificaciones.NO_LEIDAS) "${filtro.etiqueta} (${estado.noLeidas})"
                                    else filtro.etiqueta
                                )
                            }
                        )
                    }
                }
            }

            if (estado.visibles.isEmpty()) {
                item { BandejaVacia(estado.filtro) }
            }

            items(estado.visibles, key = { it.id }) { notificacion ->
                TarjetaNotificacion(
                    notificacion = notificacion,
                    onClick = { viewModel.onAbrir(notificacion) },
                    onEliminar = { viewModel.onEliminar(notificacion) },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

/** Ícono y colores según el tipo de aviso. */
private data class EstiloAviso(val icono: ImageVector, val color: Color, val fondo: Color)

private fun estiloDe(tipo: TipoNotificacion) = when (tipo) {
    TipoNotificacion.PUNTO_CERCANO -> EstiloAviso(Icons.Filled.NearMe, AzulPrimario, AzulClaro)
    TipoNotificacion.DESAFIO -> EstiloAviso(Icons.Filled.Flag, Naranjo, NaranjoClaro)
    TipoNotificacion.NUEVO_VIDEO -> EstiloAviso(Icons.Filled.PlayCircle, Morado, Color(0xFFEDE7FF))
    TipoNotificacion.LOGRO -> EstiloAviso(Icons.Filled.EmojiEvents, VerdeExito, VerdeClaro)
    TipoNotificacion.EVENTO -> EstiloAviso(Icons.Filled.Event, AzulOscuro, AzulClaro)
}

@Composable
private fun TarjetaNotificacion(
    notificacion: NotificacionEntity,
    onClick: () -> Unit,
    onEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estilo = estiloDe(notificacion.tipo)
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            // Las no leídas se destacan con fondo celeste
            containerColor = if (notificacion.leida) MaterialTheme.colorScheme.surface else AzulClaro
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (notificacion.leida) 0.dp else 1.dp)
    ) {
        Row(Modifier.padding(start = 14.dp, top = 14.dp, bottom = 14.dp, end = 4.dp), verticalAlignment = Alignment.Top) {
            Box(
                Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (notificacion.leida) estilo.fondo else Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(estilo.icono, contentDescription = null, tint = estilo.color)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        notificacion.titulo,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (notificacion.leida) FontWeight.Medium else FontWeight.Bold,
                        color = AzulOscuro,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (!notificacion.leida) {
                        Spacer(Modifier.width(6.dp))
                        Box(Modifier.size(8.dp).clip(CircleShape).background(AzulPrimario))
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(notificacion.mensaje, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text(notificacion.fecha.haceCuanto(), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            IconButton(onClick = onEliminar, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Filled.Close, contentDescription = "Eliminar", tint = Color.Gray, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun BandejaVacia(filtro: FiltroNotificaciones) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 56.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Filled.NotificationsNone, contentDescription = null, tint = AzulPrimario.copy(alpha = 0.5f), modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(12.dp))
        Text(
            if (filtro == FiltroNotificaciones.NO_LEIDAS) "¡Estás al día! No tienes avisos sin leer."
            else "Aún no tienes notificaciones.\nExplora el mapa y escanea tu primer QR.",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
