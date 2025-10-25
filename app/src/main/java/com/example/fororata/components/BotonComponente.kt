package com.example.fororata.components

import android.content.pm.PackageManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Botón reutilizable con animaciones y tamaño compacto.
 *
 * @param text Texto del botón
 * @param onClick Acción a ejecutar al presionar
 * @param modifier Modificador opcional
 * @param height Alto del botón (por defecto 42.dp)
 */
@Composable
fun BotonComponente(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 42.dp,
    enabled: Boolean = true
) {
    // Color animado dependiendo si está habilitado o no
    val colorAnimado by animateColorAsState(
        targetValue = if (enabled) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        animationSpec = tween(400)
    )

    Button(
        onClick = onClick,
        modifier = modifier.height(height),
        enabled = enabled, // <-- aquí se aplica el enabled
        colors = ButtonDefaults.buttonColors(
            containerColor = colorAnimado,
            contentColor = if (enabled) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
