package com.triskeledu.aprendoia.ui.selfie

import android.Manifest
import android.graphics.Bitmap
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CameraFront
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.triskeledu.aprendoia.ui.theme.AzulClaro
import com.triskeledu.aprendoia.ui.theme.AzulOscuro
import com.triskeledu.aprendoia.ui.theme.Naranjo
import com.triskeledu.aprendoia.ui.theme.NaranjoClaro
import com.triskeledu.aprendoia.ui.theme.VerdeClaro
import com.triskeledu.aprendoia.ui.theme.VerdeExito
import com.triskeledu.aprendoia.util.Permisos


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaSelfie(
    onVolver: () -> Unit,
    viewModel: SelfieViewModel = viewModel(factory = SelfieViewModel.Factory)
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // ---------- Cámara (contrato TakePicture) ----------
    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { exito ->
        viewModel.onResultadoCamara(exito)
    }
    fun abrirCamara() = camara.launch(viewModel.prepararFoto())

    // La app declara el permiso CAMERA, así que Android exige concederlo antes
    // de abrir la cámara del sistema. En Android 9 o menos también se pide
    // permiso para escribir en la galería.
    val permisosNecesarios = buildList {
        add(Manifest.permission.CAMERA)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }.toTypedArray()

    val pedirPermisos = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { respuestas ->
        if (respuestas[Manifest.permission.CAMERA] == true) abrirCamara() else viewModel.onPermisoDenegado()
    }

    fun alTocarTomarFoto() {
        if (permisosNecesarios.all { Permisos.tiene(context, it) }) abrirCamara()
        else pedirPermisos.launch(permisosNecesarios)
    }

    // ---------- Interfaz ----------
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Selfie turística", color = AzulOscuro) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = AzulOscuro)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        val punto = estado.punto
        if (estado.cargando || punto == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                punto.nombre,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = AzulOscuro,
                textAlign = TextAlign.Center
            )

            val bloqueo = estado.motivoBloqueo
            when {
                // 1) Selfie guardada: celebración
                estado.puntosGanados != null && estado.fotoTomada != null ->
                    VistaExito(estado.fotoTomada!!, estado.puntosGanados!!, viewModel, onVolver)

                // 2) No se puede tomar
                bloqueo != null -> VistaBloqueada(bloqueo, onVolver)

                // 3) Foto tomada, esperando confirmación
                estado.fotoTomada != null -> {
                    VistaPrevia(estado.fotoTomada!!, viewModel)
                    estado.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = viewModel::onDescartar,
                            enabled = !estado.guardando,
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(26.dp)
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Repetir")
                        }
                        Button(
                            onClick = viewModel::onGuardar,
                            enabled = !estado.guardando,
                            modifier = Modifier.weight(1f).height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VerdeExito)
                        ) {
                            if (estado.guardando) {
                                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                            } else {
                                Text("Guardar +100")
                            }
                        }
                    }
                }

                // 4) Instrucciones antes de tomar la foto
                else -> {
                    Instrucciones()
                    estado.error?.let { Text(it, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center) }
                    Button(
                        onClick = ::alTocarTomarFoto,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(27.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Naranjo)
                    ) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Tomar selfie", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun Instrucciones() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NaranjoClaro),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("¡Gana 100 puntos extra!", style = MaterialTheme.typography.titleLarge, color = AzulOscuro)
            PasoInstruccion(Icons.Filled.CameraFront, "Cambia a la cámara frontal en la app de cámara.")
            PasoInstruccion(Icons.Filled.QrCode2, "Asegúrate de que el código QR del lugar se vea detrás de ti.")
            PasoInstruccion(Icons.Filled.PhotoLibrary, "La foto se guardará en tu galería, en la carpeta AprendoIA.")
        }
    }
}

@Composable
private fun PasoInstruccion(icono: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(36.dp).clip(CircleShape).background(Color.White),
            contentAlignment = Alignment.Center
        ) { Icon(icono, contentDescription = null, tint = Naranjo, modifier = Modifier.size(20.dp)) }
        Spacer(Modifier.width(12.dp))
        Text(texto, style = MaterialTheme.typography.bodyMedium, color = AzulOscuro)
    }
}

/** Muestra la foto (cargada en segundo plano y reducida). */
@Composable
private fun FotoCargada(uri: String, viewModel: SelfieViewModel, modifier: Modifier = Modifier) {
    val bitmap by produceState<Bitmap?>(initialValue = null, uri) {
        value = viewModel.cargarMiniatura(uri)
    }
    Box(
        modifier
            .fillMaxWidth()
            .aspectRatio(3f / 4f)
            .clip(RoundedCornerShape(24.dp))
            .background(AzulClaro),
        contentAlignment = Alignment.Center
    ) {
        val imagen = bitmap
        if (imagen == null) CircularProgressIndicator()
        else Image(
            bitmap = imagen.asImageBitmap(),
            contentDescription = "Selfie",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun VistaPrevia(uri: String, viewModel: SelfieViewModel) {
    Text("¿Se ve bien el código QR?", style = MaterialTheme.typography.titleMedium, color = AzulOscuro)
    FotoCargada(uri, viewModel)
}

@Composable
private fun VistaExito(uri: String, puntos: Int, viewModel: SelfieViewModel, onListo: () -> Unit) {
    val escala = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        escala.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
    }
    Box(contentAlignment = Alignment.TopEnd) {
        FotoCargada(uri, viewModel)
        Box(
            Modifier
                .padding(12.dp)
                .scale(escala.value)
                .clip(RoundedCornerShape(16.dp))
                .background(VerdeExito)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(
                if (puntos > 0) "+$puntos" else "✓",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = VerdeClaro),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = VerdeExito, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text("¡Desafío completado!", style = MaterialTheme.typography.titleMedium, color = AzulOscuro)
                Text(
                    "Tu foto está en Galería › Imágenes › AprendoIA",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
    Button(
        onClick = onListo,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(26.dp)
    ) { Text("Continuar", style = MaterialTheme.typography.titleMedium) }
}

@Composable
private fun VistaBloqueada(motivo: String, onVolver: () -> Unit) {
    Spacer(Modifier.height(24.dp))
    Box(
        Modifier.size(110.dp).clip(CircleShape).background(AzulClaro),
        contentAlignment = Alignment.Center
    ) { Icon(Icons.Filled.Lock, contentDescription = null, tint = AzulOscuro, modifier = Modifier.size(56.dp)) }
    Text(motivo, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge, color = AzulOscuro)
    OutlinedButton(onClick = onVolver, shape = RoundedCornerShape(26.dp)) { Text("Volver") }
}
