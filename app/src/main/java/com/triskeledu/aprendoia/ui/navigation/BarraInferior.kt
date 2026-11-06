package com.triskeledu.aprendoia.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

private data class ItemBarra(val ruta: String, val etiqueta: String, val icono: ImageVector)

private val itemsBarra = listOf(
    ItemBarra(Rutas.DASHBOARD, "Inicio", Icons.Filled.Home),
    ItemBarra(Rutas.MAPA, "Mapa", Icons.Filled.Map),
    ItemBarra(Rutas.ESCANER, "Escanear", Icons.Filled.QrCodeScanner),
    ItemBarra(Rutas.COLECCION, "Colección", Icons.Filled.CollectionsBookmark),
    ItemBarra(Rutas.PERFIL, "Perfil", Icons.Filled.Person)
)

/**
 * Barra inferior de la pantalla 4: Inicio, Mapa, Escáner (botón central
 * destacado), Colección y Perfil.
 */
@Composable
fun BarraInferior(rutaActual: String?, onNavegar: (String) -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        itemsBarra.forEach { item ->
            val esEscaner = item.ruta == Rutas.ESCANER
            NavigationBarItem(
                selected = rutaActual == item.ruta,
                onClick = { onNavegar(item.ruta) },
                icon = {
                    if (esEscaner) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(item.icono, contentDescription = item.etiqueta, tint = Color.White)
                        }
                    } else {
                        Icon(item.icono, contentDescription = item.etiqueta)
                    }
                },
                label = { if (!esEscaner) Text(item.etiqueta) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = if (esEscaner) Color.Transparent
                    else MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}
