package com.triskeledu.aprendoia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Un avatar = una clave (lo que se guarda en SQLite) + emoji + color de fondo. */
data class OpcionAvatar(val clave: String, val emoji: String, val fondo: Color)

object Avatares {
    val paraJugadores = listOf(
        OpcionAvatar("avatar_1", "👩", Color(0xFFFFE0D6)),
        OpcionAvatar("avatar_2", "🧑", Color(0xFFD6F0E8)),
        OpcionAvatar("avatar_3", "👨", Color(0xFFFFE4E1)),
        OpcionAvatar("avatar_4", "👧", Color(0xFFFCE7F3)),
        OpcionAvatar("avatar_5", "👦", Color(0xFFE0ECFF)),
        OpcionAvatar("avatar_6", "👩‍🎓", Color(0xFFEDE7FF)),
        OpcionAvatar("avatar_7", "🧑‍💻", Color(0xFFE0F7FA)),
        OpcionAvatar("avatar_8", "🤖", Color(0xFFFFF4DE))
    )

    private val sistema = listOf(
        OpcionAvatar("avatar_admin", "🛡️", Color(0xFFE7F0FD)),
        OpcionAvatar("avatar_creador", "🎬", Color(0xFFFFF4DE))
    )

    fun buscar(clave: String): OpcionAvatar =
        (paraJugadores + sistema).firstOrNull { it.clave == clave } ?: paraJugadores.first()
}

/** Círculo con el avatar del jugador. Se usa en selección, perfil, dashboard, etc. */
@Composable
fun AvatarJugador(
    clave: String,
    modifier: Modifier = Modifier,
    tamano: Dp = 56.dp,
    seleccionado: Boolean = false
) {
    val opcion = Avatares.buscar(clave)
    Box(
        modifier = modifier
            .size(tamano)
            .clip(CircleShape)
            .background(opcion.fondo)
            .then(
                if (seleccionado) Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text = opcion.emoji, fontSize = (tamano.value * 0.5f).sp)
    }
}
