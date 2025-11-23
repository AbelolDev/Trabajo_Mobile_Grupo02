package com.example.fororata.ui.screen.usuarios

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.example.fororata.components.ImagenInteligente
import com.example.fororata.viewmodel.UsuarioViewModel
import com.example.fororata.viewmodel.PerfilViewModel
import com.example.fororata.viewmodel.UsuarioDBViewModel
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun ResumenScreen(
    usuarioViewModel: UsuarioViewModel,
    perfilViewModel: PerfilViewModel,
    usuarioDBViewModel: UsuarioDBViewModel,
    navController: NavController
) {
    val estado by usuarioViewModel.estado.collectAsState()
    val imagenPerfil by perfilViewModel.imagenPerfil.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Resumen del Registro",
            style = MaterialTheme.typography.headlineMedium
        )

        // Imagen del usuario
        ImagenInteligente(
            imageUri = imagenPerfil,
            size = 200.dp,
            borderWidth = 3.dp,
            borderColor = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Nombre: ${estado.nombre}", style = MaterialTheme.typography.bodyLarge)
        Text("Correo: ${estado.correo}", style = MaterialTheme.typography.bodyLarge)
        Text("Contraseña: ${"*".repeat(estado.clave.length)}", style = MaterialTheme.typography.bodyLarge)
        Text(
            "Términos: ${if (estado.aceptaTerminos) "Aceptados" else "No aceptados"}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            usuarioDBViewModel.registrar(
                nombre = estado.nombre,
                correo = estado.correo,
                clave = estado.clave
            ) { exito ->
                if (exito) {
                    Toast.makeText(context, "Datos guardados exitosamente", Toast.LENGTH_SHORT).show()
                    navController.navigate("inicio") {
                        popUpTo("resumen") { inclusive = true }
                    }
                } else {
                    Toast.makeText(context, "Error al guardar los datos", Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text("Finalizar registro")
        }

        Button(onClick = {
            navController.navigate(route = "foto-usuario")
        }) { Text("Volver")}
    }
}
