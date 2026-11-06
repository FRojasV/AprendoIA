package com.triskeledu.aprendoia.ui.gestion.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.EditLocationAlt
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.triskeledu.aprendoia.ui.components.AvatarJugador
import com.triskeledu.aprendoia.ui.components.TarjetaContador
import com.triskeledu.aprendoia.ui.gestion.viewmodel.PanelGestionViewModel
import com.triskeledu.aprendoia.ui.theme.AzulClaro
import com.triskeledu.aprendoia.ui.theme.AzulOscuro
import com.triskeledu.aprendoia.ui.theme.AzulPrimario
import com.triskeledu.aprendoia.ui.theme.Naranjo
import com.triskeledu.aprendoia.ui.theme.NaranjoClaro

/**
 * Panel de gestión para Administrador y Creador de Contenido.
 *  - Administrador: puntos, cápsulas (publicar), jugadores.
 *  - Creador: cápsulas (quedan pendientes hasta que el Administrador las publique).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPanelGestion(
    onPuntos: () -> Unit,
    onCapsulas: () -> Unit,
    onJugadores: () -> Unit,
    onVerMapa: () -> Unit,
    onSesionCerrada: () -> Unit,
    viewModel: PanelGestionViewModel = viewModel(factory = PanelGestionViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(estado.sesionCerrada) { if (estado.sesionCerrada) onSesionCerrada() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de gestión", color = AzulOscuro, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = viewModel::cerrarSesion) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión", tint = AzulOscuro)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { margen ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(margen)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ---------- Usuario conectado ----------
            estado.usuario?.let { usuario ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AvatarJugador(usuario.avatar, tamano = 56.dp)
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Hola, ${usuario.alias}", style = MaterialTheme.typography.titleLarge, color = AzulOscuro)
                        Text(
                            if (estado.esAdmin) "Administrador" else "Creador de contenido",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ---------- Resumen ----------
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TarjetaContador(estado.resumen.puntos, "Puntos", Modifier.weight(1f))
                TarjetaContador(estado.resumen.capsulas, "Cápsulas", Modifier.weight(1f))
                TarjetaContador(estado.resumen.jugadores, "Jugadores", Modifier.weight(1f))
            }
            if (estado.resumen.pendientes > 0) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NaranjoClaro),
                    onClick = onCapsulas
                ) {
                    Text(
                        if (estado.esAdmin) "Tienes ${estado.resumen.pendientes} cápsula(s) esperando publicación"
                        else "${estado.resumen.pendientes} cápsula(s) esperan revisión del Administrador",
                        modifier = Modifier.padding(16.dp),
                        color = AzulOscuro
                    )
                }
            }

            // ---------- Opciones ----------
            if (estado.esAdmin) {
                OpcionPanel(Icons.Filled.EditLocationAlt, "Puntos turísticos", "Crear, editar y eliminar lugares del mapa", AzulPrimario, onPuntos)
            }
            OpcionPanel(
                Icons.Filled.VideoLibrary, "Cápsulas de IA",
                if (estado.esAdmin) "Crear, publicar y generar códigos QR" else "Subir cápsulas y ver sus códigos QR",
                Naranjo, onCapsulas
            )
            if (estado.esAdmin) {
                OpcionPanel(Icons.Filled.Group, "Jugadores", "Editar o eliminar perfiles", AzulOscuro, onJugadores)
            }
            OpcionPanel(Icons.Filled.Map, "Ver mapa", "Revisar cómo ven los puntos los jugadores", Color(0xFF22A45D), onVerMapa)
        }
    }
}

@Composable
private fun OpcionPanel(icono: ImageVector, titulo: String, detalle: String, color: Color, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AzulClaro)
            ) {
                Icon(icono, contentDescription = null, tint = color, modifier = Modifier.padding(12.dp).size(28.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(titulo, style = MaterialTheme.typography.titleMedium, color = AzulOscuro)
                Text(detalle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}
