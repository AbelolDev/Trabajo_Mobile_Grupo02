package com.example.fororata.navigation

import TopPostsScreen
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fororata.ui.screen.*
import com.example.fororata.ui.screen.admin.AdminDashboardScreen
import com.example.fororata.ui.screen.admin.CreateAdminScreen
import com.example.fororata.ui.screen.admin.LoginAdminScreen
import com.example.fororata.ui.screen.publicaciones.*
import com.example.fororata.ui.screen.usuarios.*
import com.example.fororata.viewmodel.*

// Duración de las animaciones en milisegundos
private const val ANIMATION_DURATION = 400

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // ViewModels compartidos
    val usuarioViewModel: UsuarioViewModel = viewModel()
    val publicacionViewModel: PublicacionViewModel = viewModel()
    val perfilViewModel: PerfilViewModel = viewModel()
    val usuarioDBViewModel: UsuarioDBViewModel = viewModel()
    val postViewModel: PostViewModel = viewModel()
    val adminViewModel: AdminViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {
        // Pantalla principal
        composable(
            route = "inicio",
            enterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) },
            exitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) }
        ) {
            HomeScreen(navController, usuarioViewModel)
        }

        // Registro
        composable(
            route = "registro",
            enterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(ANIMATION_DURATION)) },
            exitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(ANIMATION_DURATION)) }
        ) {
            RegistroScreen(navController, usuarioViewModel)
        }

        // Login
        composable(
            route = "iniciar-sesion",
            enterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) },
            exitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) }
        ) {
            IniciarSesionScreen(
                navController = navController,
                usuarioDBViewModel = usuarioDBViewModel
            )
        }

        // Pantalla para seleccionar foto
        composable(
            route = "foto-usuario",
            enterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(ANIMATION_DURATION)) },
            exitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(ANIMATION_DURATION)) }
        ) {
            FotoUsuarioScreen(
                navController = navController,
                perfilViewModel = perfilViewModel,
                usuarioViewModel = usuarioViewModel
            )
        }

        // Resumen del registro con imagen
        composable(
            route = "resumen",
            enterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) },
            exitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) }
        ) {
            ResumenScreen(
                usuarioViewModel = usuarioViewModel,
                perfilViewModel = perfilViewModel,
                usuarioDBViewModel = usuarioDBViewModel,
                navController = navController
            )
        }

        composable(
            route = "resumenDB",
            enterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) },
            exitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) }
        ) {
            ResumenDBScreen(
                navController = navController,
                usuarioDBViewModel = usuarioDBViewModel,
                perfilViewModel = perfilViewModel
            )
        }

        // Publicaciones
        composable(
            route = "publicaciones",
            enterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) },
            exitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) }
        ) {
            PublicacionesScreen(navController, publicacionViewModel, usuarioDBViewModel)
        }

        // Crear publicación
        composable(
            route = "crear-publicacion",
            enterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(ANIMATION_DURATION)) },
            exitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(ANIMATION_DURATION)) }
        ) {
            CrearEditarPublicacionScreen(
                navController = navController,
                viewModel = publicacionViewModel,
                usuarioDBViewModel = usuarioDBViewModel
            )
        }

        // Editar publicación
        composable(
            route = "editar-publicacion/{publicacionId}",
            arguments = listOf(navArgument("publicacionId") { type = NavType.IntType }),
            enterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(ANIMATION_DURATION)) },
            exitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(ANIMATION_DURATION)) }
        ) { backStackEntry ->
            val publicacionId = backStackEntry.arguments?.getInt("publicacionId") ?: 0
            CrearEditarPublicacionScreen(
                navController = navController,
                viewModel = publicacionViewModel,
                usuarioDBViewModel = usuarioDBViewModel,
                publicacionId = publicacionId
            )
        }

        // Detalle de publicación con comentarios
        composable(
            route = "detalle-publicacion/{publicacionId}",
            arguments = listOf(navArgument("publicacionId") { type = NavType.IntType }),
            enterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(ANIMATION_DURATION)) },
            exitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(ANIMATION_DURATION)) }
        ) { backStackEntry ->
            val publicacionId = backStackEntry.arguments?.getInt("publicacionId") ?: 0
            DetallePublicacionScreen(
                navController = navController,
                viewModel = publicacionViewModel,
                usuarioDBViewModel = usuarioDBViewModel,
                publicacionId = publicacionId
            )
        }

        composable(
            route = "post",
            enterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) },
            exitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) }
        ) {
            PostScreen(postViewModel)
        }

        composable(
            route = "top_posts",
            enterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) },
            exitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) }
        ) {
            TopPostsScreen(navController, postViewModel)
        }

        // Admin routes
        composable(
            route = "admin-login",
            enterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) },
            exitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) }
        ) {
            LoginAdminScreen(navController = navController)
        }

        composable(
            route = "admin-dashboard",
            enterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(ANIMATION_DURATION)) },
            exitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(ANIMATION_DURATION)) }
        ) {
            AdminDashboardScreen(navController = navController)
        }

        composable(
            route = "admin-crear-admin",
            enterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(ANIMATION_DURATION)) },
            exitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(ANIMATION_DURATION)) }
        ) {
            CreateAdminScreen(navController = navController, adminViewModel = adminViewModel)
        }
    }
}