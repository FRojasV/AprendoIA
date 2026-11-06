package com.triskeledu.aprendoia.ui.gestion.pantallas

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.triskeledu.aprendoia.ui.gestion.model.CampoFormulario

/**
 * Componente reutilizable del ejemplo "Formularios": muestra el valor del
 * CampoFormulario y su error debajo (supportingText). No guarda estado propio.
 */
@Composable
fun CampoTexto(
    etiqueta: String,
    campo: CampoFormulario,
    alCambiar: (String) -> Unit,
    modifier: Modifier = Modifier,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    lineas: Int = 1,
    ayuda: String? = null,
    mayusculas: Boolean = false,
    iconoFinal: (@Composable () -> Unit)? = null
) {
    val mensajeError = campo.error
    val textoInferior = mensajeError ?: ayuda
    OutlinedTextField(
        value = campo.valor,
        onValueChange = alCambiar,
        label = { Text(etiqueta) },
        isError = mensajeError != null,
        supportingText = if (textoInferior != null) {
            { Text(textoInferior) }
        } else {
            null
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = tipoTeclado,
            capitalization = if (mayusculas) KeyboardCapitalization.Characters else KeyboardCapitalization.Sentences
        ),
        singleLine = lineas == 1,
        minLines = lineas,
        trailingIcon = iconoFinal,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.fillMaxWidth()
    )
}
