package com.example.fororata.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fororata.viewmodel.PerfilViewModel
import com.example.fororata.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FotoUsuarioScreen(
    navController: NavController,
    perfilViewModel: PerfilViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    val context = LocalContext.current
    val imagenPerfil by perfilViewModel.imagenPerfil.collectAsState()

    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        perfilViewModel.actualizarImagenDesdeCamara(success)
    }

    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        perfilViewModel.actualizarImagenDesdeGaleria(uri)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Seleccionar Foto de Perfil") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Elige una imagen para tu perfil",
                style = MaterialTheme.typography.titleMedium
            )

            // Vista previa
            AsyncImage(
                model = imagenPerfil,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(220.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )

            // Botones de selección
            Button(
                onClick = {
                    val uri = perfilViewModel.crearImagenTemporal(context)
                    launcherCamara.launch(uri)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tomar foto con cámara")
            }

            Button(
                onClick = { launcherGaleria.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Elegir desde galería")
            }

            // Botón para continuar
            Button(
                onClick = {
                    navController.navigate("resumen")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                enabled = imagenPerfil != null
            ) {
                Text("Continuar al resumen")
            }

            // Volver
            TextButton(onClick = { navController.popBackStack() }) {
                Text("Volver al formulario")
            }
        }
    }
}
