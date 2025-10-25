package com.example.fororata.ui.screen

import android.annotation.SuppressLint
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fororata.viewmodel.PerfilViewModel
import com.example.fororata.viewmodel.UsuarioViewModel
import com.example.fororata.components.ImagenInteligente.ImagenInteligente

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
    ) { success -> perfilViewModel.actualizarImagenDesdeCamara(success) }

    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> perfilViewModel.actualizarImagenDesdeGaleria(uri) }

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

            ImagenInteligente(
                imageUri = imagenPerfil,
                size = 200.dp,
                borderWidth = 3.dp,
                borderColor = MaterialTheme.colorScheme.primary
            )

            // Primera fila: Cámara y Galería
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        val uri = perfilViewModel.crearImagenTemporal(context)
                        launcherCamara.launch(uri)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cámara")
                }

                Button(
                    onClick = { launcherGaleria.launch("image/*") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Galería")
                }
            }

            // Segunda fila: Continuar y Volver
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { navController.navigate("resumen") },
                    modifier = Modifier.weight(1f),
                    enabled = imagenPerfil != null
                ) {
                    Text("Continuar")
                }

                TextButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Volver")
                }
            }
        }
    }
}


@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FotoUsuarioScreenPreview() {
    // ViewModel de prueba para Preview
    val perfilViewModel = PerfilViewModel(application = androidx.compose.ui.platform.LocalContext.current.applicationContext as android.app.Application)
    val usuarioViewModel = UsuarioViewModel()

    // Opcional: podemos setear una imagen de prueba
    // perfilViewModel.actualizarImagenDesdeGaleria(Uri.parse("https://via.placeholder.com/150"))

    FotoUsuarioScreen(
        navController = androidx.navigation.compose.rememberNavController(),
        perfilViewModel = perfilViewModel,
        usuarioViewModel = usuarioViewModel
    )
}
