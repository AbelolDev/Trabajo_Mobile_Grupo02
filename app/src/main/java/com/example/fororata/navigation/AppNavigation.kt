package com.example.fororata.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fororata.ui.screen.*
import com.example.fororata.viewmodel.PublicacionViewModel
import com.example.fororata.viewmodel.UsuarioViewModel
import com.example.fororata.viewmodel.PerfilViewModel
import com.example.fororata.viewmodel.UsuarioDBViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // ViewModels compartidos
    val usuarioViewModel: UsuarioViewModel = viewModel()
    val publicacionViewModel: PublicacionViewModel = viewModel()
    val perfilViewModel: PerfilViewModel = viewModel()
    val usuarioDBViewModel: UsuarioDBViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {
        // Pantalla principal
        composable(route = "inicio") {
            HomeScreen(navController, usuarioViewModel)
        }

        // Registro
        composable(route = "registro") {
            RegistroScreen(navController, usuarioViewModel)
        }

        // Login
        composable(route = "iniciar-sesion") {
            IniciarSesionScreen(
                navController = navController,
                usuarioDBViewModel = usuarioDBViewModel
            )
        }

        // Pantalla para seleccionar foto
        composable(route = "foto-usuario") {
            FotoUsuarioScreen(
                navController = navController,
                perfilViewModel = perfilViewModel,
                usuarioViewModel = usuarioViewModel
            )
        }

        // Resumen del registro con imagen
        composable(route = "resumen") {
            ResumenScreen(
                usuarioViewModel = usuarioViewModel,
                perfilViewModel = perfilViewModel,
                usuarioDBViewModel = usuarioDBViewModel,
                navController = navController
            )
        }

        composable(route = "resumenDB") {
            ResumenDBScreen(
                navController = navController,
                usuarioDBViewModel = usuarioDBViewModel,
                perfilViewModel = perfilViewModel
            )
        }

        // Publicaciones y creación
        composable(route = "publicaciones") {
            PublicacionesScreen(navController, publicacionViewModel)
        }

        composable(route = "crear-publicaciones") {
            PublicacionesCrearScreen(navController, publicacionViewModel)
        }
    }
}
