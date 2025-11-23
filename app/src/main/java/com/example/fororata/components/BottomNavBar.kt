package com.example.fororata.components

import androidx.compose.material.icons.Icons
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
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * Barra de navegación inferior de la app.
 * Permite cambiar entre las pantallas principales: Inicio, Publicaciones y Perfil.
 *
 * @param navController Controlador de navegación para gestionar las rutas.
 */
@Composable
fun BottomNavBar(navController: NavController) {
    // Observa la ruta actual para marcar el ítem seleccionado
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        // --- Botón Inicio ---
        NavigationBarItem(
            selected = currentRoute == "inicio",
            onClick = {
                navController.navigate("inicio") {
                    // Evita duplicar destinos en el stack
                    popUpTo("inicio") { inclusive = false }
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") }
        )

        // --- Botón Publicaciones ---
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

        // --- Botón Perfil / iniciar-sesion ---
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

        // --- Botón Top 10 ---
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
    }
}
