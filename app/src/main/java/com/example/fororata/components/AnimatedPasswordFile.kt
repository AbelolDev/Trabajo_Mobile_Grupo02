package com.example.fororata.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
    isError: Boolean,
    errorMessage: String?,
    delay: Long
) {
    var visible by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(400)) +
                slideInVertically(
                    initialOffsetY = { 30 },
                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                )
    ) {
        Column {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text("Contraseña") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = onPasswordVisibilityChange) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar contraseña"
                            else "Mostrar contraseña"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                isError = isError,
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(scale),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            AnimatedVisibility(
                visible = isError && errorMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }
    }
}