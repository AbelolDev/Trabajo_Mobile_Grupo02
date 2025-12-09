package com.example.fororata.ui.screen.usuarios

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fororata.components.BottomNavBar
import com.example.fororata.viewmodel.PerfilViewModel
import com.example.fororata.viewmodel.UsuarioViewModel
import kotlinx.coroutines.delay
import com.example.fororata.components.AnimatedPasswordField
import com.example.fororata.components.AnimatedTextField
import com.example.fororata.components.AnimatedCheckbox
import com.example.fororata.components.AnimatedButton

@Composable
fun RegistroScreenContent(
    navController: NavController,
    viewModel: UsuarioViewModel,
    perfilViewModel: PerfilViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()
    var showError by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    // Estados de visibilidad para animaciones escalonadas
    var showTitle by remember { mutableStateOf(false) }
    var showForm by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        showTitle = true
        delay(200)
        showForm = true
        delay(300)
        showButton = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Título animado
        AnimatedVisibility(
            visible = showTitle,
            enter = fadeIn(animationSpec = tween(600)) +
                    slideInVertically(
                        initialOffsetY = { -40 },
                        animationSpec = tween(600, easing = FastOutSlowInEasing)
                    )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Crear cuenta",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Únete a la comunidad Foro Rata",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Formulario animado
        AnimatedVisibility(
            visible = showForm,
            enter = fadeIn(animationSpec = tween(600)) +
                    expandVertically(animationSpec = tween(600))
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Campo Nombre
                AnimatedTextField(
                    value = estado.nombre,
                    onValueChange = viewModel::onNombreChange,
                    label = "Nombre",
                    icon = Icons.Default.Person,
                    isError = estado.errores.nombre != null,
                    errorMessage = estado.errores.nombre,
                    delay = 0
                )

                // Campo Correo
                AnimatedTextField(
                    value = estado.correo,
                    onValueChange = viewModel::onCorreoChange,
                    label = "Correo electrónico",
                    icon = Icons.Default.Email,
                    isError = estado.errores.correo != null,
                    errorMessage = estado.errores.correo,
                    delay = 100
                )

                // Campo Contraseña
                AnimatedPasswordField(
                    value = estado.clave,
                    onValueChange = viewModel::onClaveChange,
                    passwordVisible = passwordVisible,
                    onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
                    isError = estado.errores.clave != null,
                    errorMessage = estado.errores.clave,
                    delay = 200
                )

                // Checkbox con animación
                var checkboxVisible by remember { mutableStateOf(false) }

                LaunchedEffect(showForm) {
                    if (showForm) {
                        delay(300)
                        checkboxVisible = true
                    }
                }

                AnimatedVisibility(
                    visible = checkboxVisible,
                    enter = fadeIn(animationSpec = tween(400)) +
                            slideInVertically(
                                initialOffsetY = { 20 },
                                animationSpec = tween(400)
                            )
                ) {
                    AnimatedCheckbox(
                        checked = estado.aceptaTerminos,
                        onCheckedChange = viewModel::onAceptarTerminosChange,
                        errorMessage = estado.errores.aceptaTerminos
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón animado
        AnimatedVisibility(
            visible = showButton,
            enter = fadeIn(animationSpec = tween(600)) +
                    scaleIn(
                        initialScale = 0.8f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
        ) {
            AnimatedButton(
                text = "Continuar",
                onClick = {
                    if (viewModel.validarFormulario()) {
                        navController.navigate("foto-usuario")
                    } else {
                        showError = "Por favor, completa los campos correctamente"
                    }
                }
            )
        }

        // Mensaje de error animado
        AnimatedVisibility(
            visible = showError != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = showError ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        LaunchedEffect(showError) {
            if (showError != null) {
                delay(3000)
                showError = null
            }
        }
    }
}

@Composable
fun RegistroScreen(navController: NavController, viewModel: UsuarioViewModel) {
    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            RegistroScreenContent(navController = navController, viewModel = viewModel)
        }
    }
}