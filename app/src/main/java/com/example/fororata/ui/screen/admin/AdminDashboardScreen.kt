package com.example.fororata.ui.screen.admin

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fororata.api.dto.UserDTO
import com.example.fororata.viewmodel.APIviewmodel.UserViewModel
import com.example.fororata.components.admin.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    userViewModel: UserViewModel = hiltViewModel(),
    userRole: String = "Administrador"
) {
    val context = LocalContext.current
    val users by userViewModel.users
    val isLoading by userViewModel.isLoading

    var selectedTab by remember { mutableStateOf(if (userRole == "Moderador") 0 else 0) }
    var showDeleteDialog by remember { mutableStateOf<UserDTO?>(null) }
    var showEditDialog by remember { mutableStateOf<UserDTO?>(null) }

    val isModerador = userRole == "Moderador"

    LaunchedEffect(Unit) {
        if (!isModerador) {
            userViewModel.loadUsers()
        }
    }

    Scaffold(
        topBar = {
            AdminTopBar(
                isModerador = isModerador,
                onBack = { navController.popBackStack() },
                onRefresh = { userViewModel.loadUsers() }
            )
        },
        floatingActionButton = {
            AdminFAB(
                visible = !isModerador && selectedTab == 0,
                onClick = { navController.navigate("admin-crear-admin") }
            )
        }
    ) { innerPadding ->
        AdminContent(
            innerPadding = innerPadding,
            isModerador = isModerador,
            selectedTab = selectedTab,
            users = users,
            isLoading = isLoading,
            onTabSelected = { selectedTab = it },
            onEditUser = { showEditDialog = it },
            onDeleteUser = { showDeleteDialog = it },
            navController = navController
        )
    }

    // Diálogos
    if (!isModerador) {
        DeleteUserDialog(
            user = showDeleteDialog,
            userViewModel = userViewModel,
            onDismiss = { showDeleteDialog = null },
            context = context
        )

        EditUserDialog(
            user = showEditDialog,
            userViewModel = userViewModel,
            onDismiss = { showEditDialog = null },
            context = context
        )
    }
}