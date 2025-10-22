package com.example.fororata.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fororata.viewmodel.UsuarioViewModel

@Composable
fun HomeScreenContent(
    onNavigateRegistro: (() -> Unit)? = null,
    onNavigateResumen: (() -> Unit)? = null,
    onNavigatePublicaciones: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Foro Rata",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tu entorno de expresión más amplio",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onNavigateRegistro?.invoke() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ir a Registro")
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onNavigatePublicaciones?.invoke() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ir a Publicaciones")
        }
    }
}

@Composable
fun HomeScreen(navController: NavController, viewModel: UsuarioViewModel) {
    HomeScreenContent(
        onNavigateRegistro = { navController.navigate("registro") },
        onNavigateResumen = { navController.navigate("resumen") },
        onNavigatePublicaciones = { navController.navigate("publicaciones")}
    )
}