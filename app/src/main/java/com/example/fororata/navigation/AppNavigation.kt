package com.example.fororata.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fororata.ui.screen.*
import com.example.fororata.viewmodel.PublicacionViewModel
import com.example.fororata.viewmodel.UsuarioViewModel
import com.example.fororata.ui.screen.PublicacionesCrearScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val usuarioViewModel: UsuarioViewModel = viewModel()

    val publicacionViewModel: PublicacionViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {
        composable(route = "inicio") {
            HomeScreen(navController, usuarioViewModel)
        }
        composable(route = "registro") {
            RegistroScreen(navController, usuarioViewModel)
        }
        composable(route = "resumen") {
            ResumenScreen(usuarioViewModel)
        }
        composable(route = "publicaciones") {
            PublicacionesScreen(navController, publicacionViewModel)
        }
        composable(route = "crear-publicaciones") {
            PublicacionesCrearScreen(navController, publicacionViewModel)
        }
    }
}
