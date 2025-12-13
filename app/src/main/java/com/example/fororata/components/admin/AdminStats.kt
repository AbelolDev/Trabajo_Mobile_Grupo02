package com.example.fororata.components.admin

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fororata.api.dto.UserDTO
import com.example.fororata.components.StatCard
import kotlinx.coroutines.delay

@Composable
fun AnimatedStatsSection(users: List<UserDTO>) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(600)) + expandVertically()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard(
                title = "Total Usuarios",
                value = users.size.toString(),
                icon = androidx.compose.material.icons.Icons.Filled.People
            )
            StatCard(
                title = "Admins",
                value = users.count {
                    it.rol.nombre_rol.equals("Administrador", ignoreCase = true) == true
                }.toString(),
                icon = androidx.compose.material.icons.Icons.Filled.AdminPanelSettings
            )
            StatCard(
                title = "Usuarios",
                value = users.count {
                    it.rol.nombre_rol.equals("Usuario", ignoreCase = true) == true
                }.toString(),
                icon = androidx.compose.material.icons.Icons.Filled.Person
            )
        }
    }
}

@Composable
fun ModeratorMessageCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                androidx.compose.material.icons.Icons.Filled.Shield,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    "Modo Moderador",
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    "Puedes gestionar el contenido de las publicaciones",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}