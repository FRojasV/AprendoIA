package com.triskeledu.aprendoia.ui.seleccion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.triskeledu.aprendoia.data.local.relacion.JugadorResumen
import com.triskeledu.aprendoia.ui.components.AvatarJugador

/** Diálogo que pide la contraseña del jugador seleccionado. */
@Composable
fun DialogoLogin(
    jugador: JugadorResumen,
    password: String,
    passwordVisible: Boolean,
    error: String?,
    validando: Boolean,
    onPasswordChange: (String) -> Unit,
    onAlternarPassword: () -> Unit,
    onIngresar: () -> Unit,
    onCancelar: () -> Unit
) {
    val textoError: (@Composable () -> Unit)? = if (error != null) {
        { Text(error) }
    } else null

    AlertDialog(
        onDismissRequest = onCancelar,
        icon = { AvatarJugador(jugador.usuario.avatar, tamano = 72.dp) },
        title = { Text("Hola, ${jugador.usuario.alias}") },
        text = {
            Column {
                Text(
                    "Ingresa tu contraseña para continuar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text("Contraseña") },
                    singleLine = true,
                    isError = error != null,
                    supportingText = textoError,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = onAlternarPassword) {
                            Icon(
                                if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = "Mostrar u ocultar contraseña"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onIngresar() })
                )
            }
        },
        confirmButton = {
            Button(onClick = onIngresar, enabled = !validando) {
                if (validando) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                } else {
                    Text("Ingresar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}
