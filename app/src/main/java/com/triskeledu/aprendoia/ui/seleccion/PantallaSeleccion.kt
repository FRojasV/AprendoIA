package com.triskeledu.aprendoia.ui.seleccion

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.triskeledu.aprendoia.data.local.entity.Rol
import com.triskeledu.aprendoia.data.local.relacion.JugadorResumen
import com.triskeledu.aprendoia.ui.components.AvatarJugador
import com.triskeledu.aprendoia.ui.theme.AzulClaro
import com.triskeledu.aprendoia.ui.theme.AzulOscuro
import com.triskeledu.aprendoia.ui.theme.NaranjoClaro
import com.triskeledu.aprendoia.util.conSeparador

/**
 * Pantalla 3: Selección de Jugador.
 *  - Modo normal: tocar una tarjeta pide la contraseña.
 *  - Modo administrador: muestra el menú ⋮ para editar o eliminar clientes.
 */
@Composable
fun PantallaSeleccion(
    onIngresoCliente: () -> Unit,
    onIngresoGestion: () -> Unit,
    onCrearJugador: () -> Unit,
    onEditarJugador: (Long) -> Unit,
    onVolver: () -> Unit,
    viewModel: SeleccionViewModel = viewModel(factory = SeleccionViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    // Navegación después de un login correcto, según el rol.
    LaunchedEffect(estado.rolIngresado) {
        when (estado.rolIngresado) {
            Rol.CLIENTE -> { viewModel.onNavegacionRealizada(); onIngresoCliente() }
            Rol.ADMIN, Rol.CREADOR -> { viewModel.onNavegacionRealizada(); onIngresoGestion() }
            null -> Unit
        }
    }
    LaunchedEffect(estado.mensaje) {
        estado.mensaje?.let {
            snackbar.showSnackbar(it)
            viewModel.onMensajeMostrado()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Encabezado(modoAdmin = estado.modoAdmin, onVolver = onVolver)
            }

            if (estado.cargando) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }

            items(estado.jugadores, key = { it.usuario.id }) { jugador ->
                TarjetaJugador(
                    jugador = jugador,
                    activo = !estado.modoAdmin && jugador.usuario.id == estado.usuarioActivoId,
                    modoAdmin = estado.modoAdmin,
                    onClick = {
                        if (estado.modoAdmin) onEditarJugador(jugador.usuario.id)
                        else viewModel.onJugadorSeleccionado(jugador)
                    },
                    onEditar = { onEditarJugador(jugador.usuario.id) },
                    onEliminar = { viewModel.onSolicitarEliminar(jugador) },
                    modifier = Modifier.animateItem()
                )
            }

            if (!estado.modoAdmin) {
                item { TarjetaCrearJugador(onClick = onCrearJugador) }
            } else if (!estado.cargando && estado.jugadores.isEmpty()) {
                item {
                    Text(
                        "Aún no hay jugadores registrados.",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // ---------- Diálogos ----------
    estado.jugadorParaLogin?.let { jugador ->
        DialogoLogin(
            jugador = jugador,
            password = estado.password,
            passwordVisible = estado.passwordVisible,
            error = estado.errorPassword,
            validando = estado.validando,
            onPasswordChange = viewModel::onPasswordChange,
            onAlternarPassword = viewModel::onAlternarPassword,
            onIngresar = viewModel::onIngresar,
            onCancelar = viewModel::onCancelarLogin
        )
    }

    estado.jugadorParaEliminar?.let { jugador ->
        AlertDialog(
            onDismissRequest = viewModel::onCancelarEliminar,
            icon = { Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("¿Eliminar a ${jugador.usuario.nombre}?") },
            text = { Text("Se borrarán sus puntos, QR descubiertos, selfies e historial. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = viewModel::onConfirmarEliminar) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = viewModel::onCancelarEliminar) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun Encabezado(modoAdmin: Boolean, onVolver: () -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        if (modoAdmin) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onVolver) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver al panel", tint = AzulOscuro)
                }
                Surface(color = NaranjoClaro, shape = RoundedCornerShape(12.dp)) {
                    Row(
                        Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Modo administrador", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
        Text(
            text = if (modoAdmin) "Gestionar jugadores" else "Selecciona tu jugador",
            style = MaterialTheme.typography.headlineSmall,
            color = AzulOscuro,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )
    }
}

@Composable
private fun TarjetaJugador(
    jugador: JugadorResumen,
    activo: Boolean,
    modoAdmin: Boolean,
    onClick: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val usuario = jugador.usuario
    var menuAbierto by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (activo) AzulClaro else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (activo) 2.dp else 1.dp,
            color = if (activo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (activo) 0.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarJugador(usuario.avatar, tamano = 60.dp)
            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(usuario.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                when (usuario.rol) {
                    Rol.CLIENTE -> {
                        Text(
                            "${jugador.puntaje.conSeparador()} puntos",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "${jugador.qrEncontrados} QR encontrados",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Rol.ADMIN -> EtiquetaRol("Administrador · ${usuario.alias}")
                    Rol.CREADOR -> EtiquetaRol("Creador de contenido · ${usuario.alias}")
                }
            }

            if (activo) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = "Jugador activo",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (modoAdmin) {
                Box {
                    IconButton(onClick = { menuAbierto = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Opciones")
                    }
                    DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                        DropdownMenuItem(
                            text = { Text("Editar") },
                            leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                            onClick = { menuAbierto = false; onEditar() }
                        )
                        DropdownMenuItem(
                            text = { Text("Eliminar", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = {
                                Icon(Icons.Filled.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            },
                            onClick = { menuAbierto = false; onEliminar() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EtiquetaRol(texto: String) {
    Surface(
        color = NaranjoClaro,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(top = 4.dp)
    ) {
        Text(
            texto,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun TarjetaCrearJugador(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = AzulOscuro)
            }
            Spacer(Modifier.width(16.dp))
            Text(
                "Crear nuevo jugador",
                style = MaterialTheme.typography.titleMedium,
                color = AzulOscuro
            )
        }
    }
}
