package com.example.fororata.components.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.fororata.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTopBar(
    isModerador: Boolean,
    onBack: () -> Unit,
    onRefresh: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = if (isModerador) "Panel de Moderación" else "Panel de Administración",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (isModerador) {
                    Text(
                        text = "Gestión de Contenido",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Atrás"
                )
            }
        },
        actions = {
            if (!isModerador) {
                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Recargar"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = if (isModerador)
                MaterialTheme.colorScheme.secondaryContainer
            else
                MaterialTheme.colorScheme.primaryContainer
        )
    )
}