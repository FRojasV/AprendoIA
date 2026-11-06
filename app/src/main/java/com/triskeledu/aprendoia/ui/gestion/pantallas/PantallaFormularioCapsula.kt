package com.triskeledu.aprendoia.ui.gestion.pantallas

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
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.triskeledu.aprendoia.data.local.entity.PuntoEntity
import com.triskeledu.aprendoia.ui.gestion.viewmodel.FormularioCapsulaViewModel
import com.triskeledu.aprendoia.ui.theme.AzulOscuro
import com.triskeledu.aprendoia.ui.theme.NaranjoClaro
import com.triskeledu.aprendoia.ui.theme.VerdeClaro

/**
 * Formulario de cápsula de IA (crear o editar). Al guardar se abre su código QR.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFormularioCapsula(
    onVolver: () -> Unit,
    onGuardada: (Long) -> Unit,
    viewModel: FormularioCapsulaViewModel = viewModel(factory = FormularioCapsulaViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(estado.guardadoId) {
        estado.guardadoId?.let {
            viewModel.navegacionRealizada()
            onGuardada(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (estado.esEdicion) "Editar cápsula" else "Nueva cápsula", color = AzulOscuro) },
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SelectorPunto(
                puntos = estado.puntosDisponibles,
                seleccionado = estado.puntoSeleccionado,
                error = estado.errorPunto,
                onElegir = viewModel::elegirPunto
            )
            CampoTexto("Título", estado.titulo, viewModel::cambiarTitulo)
            CampoTexto("Tema", estado.tema, viewModel::cambiarTema, ayuda = "Ej: Machine Learning, Robótica")
            CampoTexto("Autor", estado.autor, viewModel::cambiarAutor)
            CampoTexto(
                "Link del video (opcional)", estado.url, viewModel::cambiarUrl,
                tipoTeclado = KeyboardType.Uri, ayuda = "YouTube o .mp4. Puedes agregarlo después."
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CampoTexto(
                    "Duración", estado.duracion, viewModel::cambiarDuracion,
                    modifier = Modifier.weight(1f), tipoTeclado = KeyboardType.Number, ayuda = "mm:ss"
                )
                CampoTexto(
                    "Puntos del QR", estado.puntosQr, viewModel::cambiarPuntosQr,
                    modifier = Modifier.weight(1f), tipoTeclado = KeyboardType.Number
                )
            }
            CampoTexto("Descripción", estado.descripcion, viewModel::cambiarDescripcion, lineas = 3)
            CampoTexto(
                "Código QR", estado.codigoQr, viewModel::cambiarCodigo,
                mayusculas = true,
                ayuda = "Texto único que irá dentro del QR",
                iconoFinal = {
                    IconButton(onClick = viewModel::generarCodigo) {
                        Icon(Icons.Filled.Autorenew, contentDescription = "Generar código")
                    }
                }
            )

            // ---------- Publicación ----------
            if (estado.esAdmin) {
                Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = VerdeClaro)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Publicada", style = MaterialTheme.typography.titleSmall, color = AzulOscuro)
                            Text(
                                "Visible para los jugadores. Al publicar se les envía un aviso.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(checked = estado.publicado, onCheckedChange = viewModel::cambiarPublicado)
                    }
                }
            } else {
                Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = NaranjoClaro)) {
                    Text(
                        "La cápsula quedará pendiente hasta que el Administrador la publique.",
                        modifier = Modifier.padding(14.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = AzulOscuro
                    )
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
                else Text(if (estado.esEdicion) "Guardar y ver QR" else "Crear y generar QR")
            }
        }
    }
}

/** Lista desplegable para elegir el punto turístico de la cápsula. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorPunto(
    puntos: List<PuntoEntity>,
    seleccionado: PuntoEntity?,
    error: String?,
    onElegir: (Long) -> Unit
) {
    var abierto by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = abierto, onExpandedChange = { abierto = it }) {
        OutlinedTextField(
            value = seleccionado?.nombre ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Punto turístico") },
            placeholder = { Text("Elige un lugar") },
            isError = error != null,
            supportingText = if (error != null) { { Text(error) } } else null,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = abierto) },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            puntos.forEach { punto ->
                DropdownMenuItem(
                    text = { Text(punto.nombre + if (punto.esHitoTuristico) " ⭐" else "") },
                    onClick = {
                        onElegir(punto.id)
                        abierto = false
                    }
                )
            }
        }
    }
}
