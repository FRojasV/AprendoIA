package com.triskeledu.aprendoia.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.triskeledu.aprendoia.ui.theme.AzulOscuro

/**
 * Campo de texto con el estilo de las vistas del caso:
 * etiqueta pequeña arriba, ícono a la izquierda y campo redondeado.
 * El Composable no guarda estado: recibe el valor y avisa los cambios al ViewModel.
 */
@Composable
fun CampoFormulario(
    valor: String,
    onValorCambia: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    error: String? = null,
    placeholder: String = "",
    tipoTeclado: KeyboardType = KeyboardType.Text,
    accionIme: ImeAction = ImeAction.Next,
    esPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onAlternarPassword: (() -> Unit)? = null,
    habilitado: Boolean = true
) {
    val iconoFinal: (@Composable () -> Unit)? = if (esPassword && onAlternarPassword != null) {
        {
            IconButton(onClick = onAlternarPassword) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                )
            }
        }
    } else null

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 36.dp, bottom = 4.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, contentDescription = null, tint = AzulOscuro, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(12.dp))
            OutlinedTextField(
                value = valor,
                onValueChange = onValorCambia,
                modifier = Modifier.weight(1f),
                enabled = habilitado,
                singleLine = true,
                isError = error != null,
                placeholder = { if (placeholder.isNotEmpty()) Text(placeholder) },
                shape = RoundedCornerShape(28.dp),
                keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado, imeAction = accionIme),
                visualTransformation = if (esPassword && !passwordVisible) PasswordVisualTransformation()
                else VisualTransformation.None,
                trailingIcon = iconoFinal
            )
        }
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 36.dp, top = 4.dp)
            )
        }
    }
}
