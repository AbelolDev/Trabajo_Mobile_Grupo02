package com.example.fororata.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
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
                slideInHorizontally(
                    initialOffsetX = { -50 },
                    animationSpec = tween(400, easing = FastOutSlowInEasing)
                )
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = onPasswordVisibilityChange) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
    }
}