package com.example.fororata.ui.screen.usuarios

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.fororata.components.ImagenInteligente
import com.example.fororata.viewmodel.PerfilViewModel
import com.example.fororata.viewmodel.UsuarioViewModel
import com.example.fororata.components.AnimatedContinueButton
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FotoUsuarioScreen(
    navController: NavController,
    perfilViewModel: PerfilViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    val context = LocalContext.current
    val imagenPerfil by perfilViewModel.imagenPerfil.collectAsState()

    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    var showImagePreview by remember { mutableStateOf(false) }

    // Estados de visibilidad
    var showTitle by remember { mutableStateOf(false) }
    var showImage by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }
    var showActionButtons by remember { mutableStateOf(false) }

    // Animación de entrada
    LaunchedEffect(Unit) {
        delay(100)
        showTitle = true
        delay(200)
        showImage = true
        delay(300)
        showButtons = true
        delay(400)
        showActionButtons = true
    }

    // Animación cuando se selecciona imagen
    LaunchedEffect(imagenPerfil) {
        if (imagenPerfil != null) {
            showImagePreview = false
            delay(100)
            showImagePreview = true
        }
    }

    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraUri != null) {
            perfilViewModel.actualizarImagenDesdeCamara(true)
        } else {
            Toast.makeText(context, "No se pudo tomar la foto", Toast.LENGTH_SHORT).show()
        }
    }

    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) perfilViewModel.actualizarImagenDesdeGaleria(uri)
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Foto de Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Título animado
            AnimatedVisibility(
                visible = showTitle,
                enter = fadeIn(animationSpec = tween(600)) +
                        slideInVertically(
                            initialOffsetY = { -40 },
                            animationSpec = tween(600, easing = FastOutSlowInEasing)
                        )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Selecciona tu foto",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Esta será tu imagen de perfil",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp)) // Más espacio antes de la imagen

            // Imagen con animación
            AnimatedVisibility(
                visible = showImage,
                enter = scaleIn(
                    initialScale = 0.5f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(animationSpec = tween(600))
            ) {
                Box(
                    modifier = Modifier.size(220.dp), // Un poco más grande
                    contentAlignment = Alignment.Center
                ) {
                    // Pulso de fondo si no hay imagen
                    if (imagenPerfil == null) {
                        val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                        val scale by infiniteTransition.animateFloat(
                            initialValue = 0.95f,
                            targetValue = 1.05f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1500, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "pulse_scale"
                        )

                        Surface(
                            modifier = Modifier
                                .size(240.dp) // Más grande para el efecto de pulso
                                .scale(scale),
                            shape = MaterialTheme.shapes.extraLarge,
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        ) {}
                    }

                    // Imagen de perfil con transición
                    AnimatedContent(
                        targetState = imagenPerfil,
                        transitionSpec = {
                            fadeIn(tween(400)) + scaleIn(
                                initialScale = 0.8f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy
                                )
                            ) togetherWith fadeOut(tween(200))
                        },
                        label = "image_transition"
                    ) { targetImage ->
                        ImagenInteligente(
                            imageUri = targetImage,
                            size = 220.dp, // Más grande
                            borderWidth = 4.dp,
                            borderColor = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp)) // Más espacio antes de los botones

            // Botones de captura animados - VERSIÓN CENTRADA Y MÁS GRANDE
            AnimatedVisibility(
                visible = showButtons,
                enter = fadeIn(animationSpec = tween(600)) +
                        expandVertically(animationSpec = tween(600))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp) // Más espacio vertical
                ) {
                    // Botones en columnas centradas
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Botón de Cámara - MÁS GRANDE
                        Button(
                            onClick = {
                                val permission = Manifest.permission.CAMERA
                                if (ContextCompat.checkSelfPermission(context, permission)
                                    == PackageManager.PERMISSION_GRANTED
                                ) {
                                    val uri = perfilViewModel.crearImagenTemporal(context)
                                    cameraUri = uri
                                    launcherCamara.launch(uri)
                                } else {
                                    requestPermissionLauncher.launch(permission)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.8f) // 80% del ancho disponible
                                .height(60.dp), // Altura aumentada
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Cámara",
                                modifier = Modifier.size(28.dp) // Icono más grande
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Tomar Foto",
                                style = MaterialTheme.typography.titleMedium, // Texto más grande
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Botón de Galería - MÁS GRANDE
                        Button(
                            onClick = { launcherGaleria.launch("image/*") },
                            modifier = Modifier
                                .fillMaxWidth(0.8f) // 80% del ancho disponible
                                .height(60.dp), // Altura aumentada
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            )
                        ) {
                            Icon(
                                Icons.Default.PhotoLibrary,
                                contentDescription = "Galería",
                                modifier = Modifier.size(28.dp) // Icono más grande
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Elegir de Galería",
                                style = MaterialTheme.typography.titleMedium, // Texto más grande
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Botón para eliminar foto
                    AnimatedVisibility(
                        visible = imagenPerfil != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        OutlinedButton(
                            onClick = { perfilViewModel.limpiarImagenPerfil() },
                            modifier = Modifier
                                .fillMaxWidth(0.8f) // Mismo ancho que los otros botones
                                .height(50.dp), // Un poco menos alto
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                width = 1.5.dp // Borde más grueso
                            )
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Eliminar foto",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Botones de navegación animados
            AnimatedVisibility(
                visible = showActionButtons,
                enter = fadeIn(animationSpec = tween(600)) +
                        slideInVertically(
                            initialOffsetY = { 40 },
                            animationSpec = tween(600)
                        )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.navigate("registro") },
                        modifier = Modifier.weight(4f)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Volver")
                    }

                    AnimatedContinueButton(
                        enabled = imagenPerfil != null,
                        modifier = Modifier.weight(4f),
                        onClick = { navController.navigate("resumen") }
                    )
                }
            }

            // Mensaje informativo
            AnimatedVisibility(
                visible = imagenPerfil == null && showButtons,
                enter = fadeIn(tween(400, delayMillis = 600)),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Selecciona una imagen para continuar",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}