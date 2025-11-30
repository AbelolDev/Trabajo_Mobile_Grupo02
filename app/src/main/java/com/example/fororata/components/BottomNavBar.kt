package com.example.fororata.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.delay

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Estado de visibilidad para animación inicial
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(),
        exit = slideOutVertically() + fadeOut()
    ) {
        NavigationBar {
            // Botón Inicio
            NavigationBarItem(
                selected = currentRoute == "inicio",
                onClick = {
                    navController.navigate("inicio") {
                        popUpTo("inicio") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                label = { Text("Inicio") }
            )

            // Botón Publicaciones
            NavigationBarItem(
                selected = currentRoute == "publicaciones",
                onClick = {
                    navController.navigate("publicaciones") {
                        popUpTo("inicio") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                icon = { Icon(Icons.Default.Description, contentDescription = "Publicaciones") },
                label = { Text("Publicaciones") }
            )

            // Botón Perfil
            NavigationBarItem(
                selected = currentRoute == "iniciar-sesion",
                onClick = {
                    navController.navigate("iniciar-sesion") {
                        popUpTo("inicio") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                label = { Text("Perfil") }
            )

            // Botón Top 10
            NavigationBarItem(
                selected = currentRoute == "top_posts",
                onClick = {
                    navController.navigate("top_posts") {
                        popUpTo("inicio") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                icon = { Icon(Icons.Default.Star, contentDescription = "Top 10") },
                label = { Text("Top 10") }
            )

            // Botón Admin
            NavigationBarItem(
                selected = currentRoute == "admin-login",
                onClick = {
                    navController.navigate("admin-login") {
                        popUpTo("inicio") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        Icons.Default.AdminPanelSettings,
                        contentDescription = "Administración"
                    )
                },
                label = { Text("Admin") }
            )
        }
    }
}