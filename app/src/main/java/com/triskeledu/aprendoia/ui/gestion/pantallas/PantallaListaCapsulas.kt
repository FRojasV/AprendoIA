package com.triskeledu.aprendoia.ui.gestion.pantallas

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.triskeledu.aprendoia.data.local.relacion.CapsulaGestion
import com.triskeledu.aprendoia.ui.gestion.model.FiltroCapsulasGestion
import com.triskeledu.aprendoia.ui.gestion.viewmodel.ListaCapsulasViewModel
import com.triskeledu.aprendoia.ui.mapa.Etiqueta
import com.triskeledu.aprendoia.ui.theme.AzulOscuro
import com.triskeledu.aprendoia.ui.theme.Naranjo
import com.triskeledu.aprendoia.ui.theme.NaranjoClaro
import com.triskeledu.aprendoia.ui.theme.VerdeClaro
import com.triskeledu.aprendoia.ui.theme.VerdeExito

/**
 * Lista de cápsulas de IA.
 *  - Administrador: editar, publicar, eliminar y ver QR de todas.
 *  - Creador: crear, editar sus pendientes y ver QR.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaListaCapsulas(
    onVolver: () -> Unit,
    onNueva: () -> Unit,
    onEditar: (Long) -> Unit,
    onVerQr: (Long) -> Unit,
    viewModel: ListaCapsulasViewModel = viewModel(factory = ListaCapsulasViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(estado.mensaje) {
        estado.mensaje?.let { snackbar.showSnackbar(it); viewModel.mensajeMostrado() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cápsulas de IA", color = AzulOscuro) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = AzulOscuro)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNueva,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nueva cápsula") }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = MaterialTheme.colorScheme.background
    ) { margen ->
        if (estado.cargando) {
            Box(Modifier.fillMaxSize().padding(margen), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Scaffold
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(margen),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FiltroCapsulasGestion.entries.forEach { filtro ->
                        FilterChip(
                            selected = estado.filtro == filtro,
                            onClick = { viewModel.cambiarFiltro(filtro) },
                            label = { Text(filtro.etiqueta) }
                        )
                    }
                }
            }
            if (estado.visibles.isEmpty()) {
                item {
                    Text(
                        "No hay cápsulas en este filtro.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
            items(estado.visibles, key = { it.contenido.id }) { capsula ->
                FilaCapsula(
                    capsula = capsula,
                    esAdmin = estado.esAdmin,
                    onEditar = { onEditar(capsula.contenido.id) },
                    onVerQr = { onVerQr(capsula.contenido.id) },
                    onPublicar = { viewModel.publicar(capsula) },
                    onEliminar = { viewModel.solicitarEliminar(capsula) },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }

    estado.capsulaParaEliminar?.let { capsula ->
        AlertDialog(
            onDismissRequest = viewModel::cancelarEliminar,
            title = { Text("¿Eliminar \"${capsula.contenido.titulo}\"?") },
            text = { Text("Los jugadores que ya la descubrieron perderán sus puntos por este QR.") },
            confirmButton = {
                TextButton(onClick = viewModel::confirmarEliminar) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = viewModel::cancelarEliminar) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun FilaCapsula(
    capsula: CapsulaGestion,
    esAdmin: Boolean,
    onEditar: () -> Unit,
    onVerQr: () -> Unit,
    onPublicar: () -> Unit,
    onEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val c = capsula.contenido
    // El Creador solo puede editar cápsulas que aún no están publicadas.
    val puedeEditar = esAdmin || !c.publicado

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(c.titulo, style = MaterialTheme.typography.titleSmall, color = AzulOscuro, modifier = Modifier.weight(1f))
                if (c.publicado) Etiqueta("Publicada", VerdeClaro, VerdeExito)
                else Etiqueta("Pendiente", NaranjoClaro, Naranjo)
            }
            Text(
                "${capsula.puntoNombre} · ${c.tema} · +${c.puntos} pts",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                c.codigoQr + if (c.url.isBlank()) " · sin video aún" else "",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onVerQr) {
                    Icon(Icons.Filled.QrCode2, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Ver QR")
                }
                if (esAdmin && !c.publicado) {
                    TextButton(onClick = onPublicar) {
                        Icon(Icons.Filled.Publish, contentDescription = null, modifier = Modifier.size(18.dp), tint = VerdeExito)
                        Spacer(Modifier.width(4.dp))
                        Text("Publicar", color = VerdeExito)
                    }
                }
                Spacer(Modifier.weight(1f))
                if (puedeEditar) {
                    IconButton(onClick = onEditar) { Icon(Icons.Filled.Edit, contentDescription = "Editar") }
                }
                if (esAdmin) {
                    IconButton(onClick = onEliminar) {
                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
