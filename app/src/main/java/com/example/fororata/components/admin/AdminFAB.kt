package com.example.fororata.components.admin

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun AdminFAB(
    visible: Boolean,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
        exit = scaleOut()
    ) {
        ExtendedFloatingActionButton(
            onClick = onClick,
            icon = { Icon(Icons.Filled.Add, contentDescription = "Agregar") },
            text = { Text("Nuevo Usuario") }
        )
    }
}