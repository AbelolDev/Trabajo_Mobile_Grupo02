package com.example.fororata.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fororata.model.PublicacionErrores
import com.example.fororata.components.BottomNavBar
import com.example.fororata.viewmodel.PublicacionViewModel
import com.example.fororata.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicacionesScreenContent(
    navController: NavController,
    viewModel: PublicacionViewModel
) {
    // Lista de publicaciones de ejemplo (en un caso real se cargarían del ViewModel)
    val publicaciones = remember {
        listOf(
            PublicacionErrores(
                id = 1,
                titulo = "¿Vale la pena aprender Kotlin en 2025?",
                estrellas = 4
            ),
            PublicacionErrores(
                id = 2,
                titulo = "Mi experiencia usando Jetpack Compose en producción",
                estrellas = 5
            ),
            PublicacionErrores(
                id = 3,
                titulo = "¿Qué opinan de Android Studio Koala?",
                estrellas = 3
            ),
            PublicacionErrores(
                id = 4,
                titulo = "Tips para optimizar recomposiciones en Compose",
                estrellas = 5
            )
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Publicaciones", fontWeight = FontWeight.Bold) },
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(publicaciones) { publicacion ->
                PublicacionCard(publicacion)
            }
        }
    }
}

/**
 * Tarjeta visual que representa una publicación al estilo Reddit.
 */
@Composable
fun PublicacionCard(publicacion: PublicacionErrores) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = publicacion.titulo ?: "Sin título",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(publicacion.estrellas ?: 0) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Estrella",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                if ((publicacion.estrellas ?: 0) == 0) {
                    Text("Sin calificación", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${publicacion.comentarios.size} comentarios",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PublicacionesScreen(navController: NavController, viewModel: PublicacionViewModel) {
    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            PublicacionesScreenContent(navController = navController, viewModel = viewModel)
        }
    }
}