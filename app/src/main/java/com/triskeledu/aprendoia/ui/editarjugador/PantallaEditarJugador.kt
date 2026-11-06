package com.triskeledu.aprendoia.ui.editarjugador

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.triskeledu.aprendoia.ui.components.CampoFormulario
import com.triskeledu.aprendoia.ui.components.SelectorAvatar
import com.triskeledu.aprendoia.ui.theme.AzulOscuro

/** Formulario sencillo para editar un jugador. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEditarJugador(
    onVolver: () -> Unit,
    titulo: String = "Editar jugador",
    viewModel: EditarJugadorViewModel = viewModel(factory = EditarJugadorViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(estado.guardado) { if (estado.guardado) onVolver() }
    LaunchedEffect(estado.mensajeError) {
        estado.mensajeError?.let {
            snackbar.showSnackbar(it)
            viewModel.onMensajeMostrado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo, style = MaterialTheme.typography.titleLarge, color = AzulOscuro) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = AzulOscuro)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding ->
        if (estado.cargando) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CampoFormulario(
                valor = estado.nombre,
                onValorCambia = viewModel::onNombreChange,
                etiqueta = "Nombre completo",
                icono = Icons.Filled.Person,
                error = estado.errores.nombre
            )
            CampoFormulario(
                valor = estado.correo,
                onValorCambia = viewModel::onCorreoChange,
                etiqueta = "Correo electrónico",
                icono = Icons.Filled.Email,
                error = estado.errores.correo,
                tipoTeclado = KeyboardType.Email
            )
            CampoFormulario(
                valor = estado.telefono,
                onValorCambia = viewModel::onTelefonoChange,
                etiqueta = "Teléfono",
                icono = Icons.Filled.Phone,
                error = estado.errores.telefono,
                tipoTeclado = KeyboardType.Phone
            )
            CampoFormulario(
                valor = estado.direccion,
                onValorCambia = viewModel::onDireccionChange,
                etiqueta = "Dirección (opcional)",
                icono = Icons.Filled.LocationOn
            )
            CampoFormulario(
                valor = estado.nuevaPassword,
                onValorCambia = viewModel::onPasswordChange,
                etiqueta = "Nueva contraseña (dejar vacía para no cambiarla)",
                icono = Icons.Filled.Lock,
                error = estado.errores.password,
                tipoTeclado = KeyboardType.Password,
                accionIme = ImeAction.Done,
                esPassword = true,
                passwordVisible = estado.passwordVisible,
                onAlternarPassword = viewModel::onAlternarPassword
            )

            SelectorAvatar(
                seleccionado = estado.avatar,
                mostrarTodos = estado.mostrarTodosAvatares,
                onSeleccionar = viewModel::onAvatarSeleccionado,
                onAlternarMas = viewModel::onAlternarAvatares
            )

            Button(
                onClick = viewModel::onGuardar,
                enabled = !estado.guardando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(bottom = 0.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                if (estado.guardando) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                } else {
                    Text("Guardar cambios", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
