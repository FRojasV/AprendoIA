package com.triskeledu.aprendoia.ui.perfil

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.triskeledu.aprendoia.data.local.entity.Rol
import com.triskeledu.aprendoia.ui.components.AvatarJugador
import com.triskeledu.aprendoia.ui.components.TarjetaContador
import com.triskeledu.aprendoia.ui.theme.AzulOscuro
import com.triskeledu.aprendoia.ui.theme.RojoError
import com.triskeledu.aprendoia.util.Permisos
import com.triskeledu.aprendoia.util.conSeparador

/**
 * Pantalla 15: Perfil y Configuración.
 * Editar perfil abre el formulario de edición (reutilizado de la Etapa 2).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPerfil(
    onVolver: () -> Unit,
    onEditarPerfil: (Long) -> Unit,
    onSesionCerrada: () -> Unit,
    viewModel: PerfilViewModel = viewModel(factory = PerfilViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(estado.sesionCerrada) { if (estado.sesionCerrada) onSesionCerrada() }
    LaunchedEffect(estado.mensaje) {
        estado.mensaje?.let {
            snackbar.showSnackbar(it)
            viewModel.onMensajeMostrado()
        }
    }

    // Al activar notificaciones en Android 13+ primero se pide el permiso.
    val lanzadorNotificaciones = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) viewModel.onNotificacionesChange(true) else viewModel.onPermisoNotificacionDenegado()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", color = AzulOscuro) },
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
    ) { padding ->
        val usuario = estado.usuario
        if (estado.cargando || usuario == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ---------- Encabezado ----------
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarJugador(usuario.avatar, tamano = 84.dp)
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        usuario.nombre,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AzulOscuro
                    )
                    if (usuario.rol == Rol.CLIENTE) {
                        Text(
                            "${estado.puntaje.conSeparador()} puntos",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text(
                            if (usuario.rol == Rol.ADMIN) "Administrador" else "Creador de contenido",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = { onEditarPerfil(usuario.id) }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar perfil", tint = MaterialTheme.colorScheme.primary)
                }
            }

            // ---------- Estadísticas ----------
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TarjetaContador(estado.estadisticas.qrEncontrados, "QR", Modifier.weight(1f))
                TarjetaContador(estado.estadisticas.videosVistos, "Videos", Modifier.weight(1f))
                TarjetaContador(estado.estadisticas.selfies, "Selfies", Modifier.weight(1f))
            }

            // ---------- Opciones ----------
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Opcion(Icons.Filled.Person, "Editar perfil", onClick = { onEditarPerfil(usuario.id) })
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Opcion(Icons.Filled.Face, "Cambiar avatar", onClick = viewModel::onCambiarAvatarClick)
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                ListItem(
                    headlineContent = { Text("Notificaciones") },
                    leadingContent = { Icon(Icons.Filled.Notifications, contentDescription = null, tint = RojoError) },
                    trailingContent = {
                        Switch(
                            checked = usuario.notificacionesActivas,
                            onCheckedChange = { activar ->
                                if (activar && !Permisos.tieneNotificaciones(context)) {
                                    lanzadorNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    viewModel.onNotificacionesChange(activar)
                                }
                            }
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Opcion(
                    Icons.Filled.Security, "Permisos",
                    subtitulo = "Cámara, Ubicación",
                    onClick = { viewModel.onMostrarPermisos(true) }
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Opcion(Icons.Filled.Info, "Ayuda y soporte", onClick = { viewModel.onMostrarAyuda(true) })
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                Opcion(
                    Icons.AutoMirrored.Filled.Logout, "Cerrar sesión",
                    onClick = { viewModel.onMostrarConfirmarCierre(true) }
                )
            }
        }
    }

    // ---------- Diálogos ----------
    if (estado.mostrarDialogoAvatar) {
        DialogoCambiarAvatar(
            seleccionado = estado.avatarTemporal,
            onSeleccionar = viewModel::onAvatarTemporal,
            onGuardar = viewModel::onGuardarAvatar,
            onCancelar = viewModel::onCancelarAvatar
        )
    }
    if (estado.mostrarDialogoPermisos) {
        DialogoPermisos(onCerrar = { viewModel.onMostrarPermisos(false) })
    }
    if (estado.mostrarAyuda) {
        DialogoAyuda(onCerrar = { viewModel.onMostrarAyuda(false) })
    }
    if (estado.mostrarConfirmarCierre) {
        AlertDialog(
            onDismissRequest = { viewModel.onMostrarConfirmarCierre(false) },
            title = { Text("¿Cerrar sesión?") },
            text = { Text("Volverás a la selección de jugador. Tu progreso queda guardado.") },
            confirmButton = { TextButton(onClick = viewModel::onCerrarSesion) { Text("Cerrar sesión") } },
            dismissButton = { TextButton(onClick = { viewModel.onMostrarConfirmarCierre(false) }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun Opcion(
    icono: ImageVector,
    titulo: String,
    subtitulo: String? = null,
    onClick: () -> Unit
) {
    val textoSecundario: (@Composable () -> Unit)? = if (subtitulo != null) {
        { Text(subtitulo) }
    } else null

    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        headlineContent = { Text(titulo) },
        supportingContent = textoSecundario,
        leadingContent = { Icon(icono, contentDescription = null, tint = AzulOscuro) },
        trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}
