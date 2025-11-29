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
import com.example.fororata.components.StatCard
import com.example.fororata.components.UserAdminItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val users by userViewModel.users
    val isLoading by userViewModel.isLoading

    LaunchedEffect(Unit) {
        userViewModel.loadUsers()
    }

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
                onClick = { navController.navigate("admin-crear-admin") },
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
                    title = "Admins",
                    value = users.count { it.rol?.nombre_rol?.equals("Administrador", ignoreCase = true) == true }.toString(),
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