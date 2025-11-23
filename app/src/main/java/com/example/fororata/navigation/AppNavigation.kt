package com.example.fororata.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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

        // Publicaciones - Lista
        composable(route = "publicaciones") {
            PublicacionesScreen(navController, publicacionViewModel, usuarioDBViewModel)
        }

        // Publicaciones - Detalle con comentarios
        composable(
            route = "detalle-publicacion/{publicacionId}",
            arguments = listOf(
                navArgument("publicacionId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val publicacionId = backStackEntry.arguments?.getInt("publicacionId") ?: 0
            DetallePublicacionScreen(
                navController = navController,
                viewModel = publicacionViewModel,
                usuarioDBViewModel = usuarioDBViewModel,
                publicacionId = publicacionId
            )
        }

        // Publicaciones - Crear nueva
        composable(route = "crear-publicacion") {
            CrearEditarPublicacionScreen(
                navController = navController,
                viewModel = publicacionViewModel,
                usuarioDBViewModel = usuarioDBViewModel,
                publicacionId = null
            )
        }

        // Publicaciones - Editar existente
        composable(
            route = "editar-publicacion/{publicacionId}",
            arguments = listOf(
                navArgument("publicacionId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val publicacionId = backStackEntry.arguments?.getInt("publicacionId")
            CrearEditarPublicacionScreen(
                navController = navController,
                viewModel = publicacionViewModel,
                usuarioDBViewModel = usuarioDBViewModel,
                publicacionId = publicacionId
            )
        }

        // Mantener la ruta antigua por compatibilidad (opcional)
        composable(route = "crear-publicaciones") {
            CrearEditarPublicacionScreen(
                navController = navController,
                viewModel = publicacionViewModel,
                usuarioDBViewModel = usuarioDBViewModel,
                publicacionId = null
            )
        }
    }
}