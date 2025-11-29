package com.example.fororata.ui.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fororata.viewmodel.APIviewmodel.UserViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fororata.api.dto.UserDTO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val users by userViewModel.users
    val isLoading by userViewModel.isLoading

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* Acción para crear usuario admin */ },
                icon = { Icon(Icons.Default.Add, contentDescription = "Agregar") },
                text = { Text("Nuevo Admin") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Estadísticas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatCard(
                    title = "Total Usuarios",
                    value = users.size.toString(),
                    icon = Icons.Default.People
                )
                StatCard(
                    title = "Administradores",
                    value = users.count { it.rol?.nombre_rol == "admin" }.toString(),
                    icon = Icons.Default.AdminPanelSettings
                )
            }

            // Lista de usuarios
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(users) { user ->
                            UserAdminItem(user = user)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(
        modifier = Modifier.width(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = title,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun UserAdminItem(user: UserDTO) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (user.rol?.nombre_rol == "admin") Icons.Default.AdminPanelSettings else Icons.Default.Person,
                contentDescription = "Usuario",
                tint = if (user.rol?.nombre_rol == "admin") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.nombre,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = user.correo,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Rol: ${user.rol?.nombre_rol ?: "Usuario"}",
                    fontSize = 12.sp
                )
            }
            IconButton(onClick = { /* Editar usuario */ }) {
                Icon(Icons.Default.Edit, contentDescription = "Editar")
            }
        }
    }
}