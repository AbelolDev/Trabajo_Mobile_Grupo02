package com.example.fororata.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fororata.components.BotonComponente
import com.example.fororata.components.BottomNavBar
import com.example.fororata.viewmodel.UsuarioViewModel
import kotlinx.coroutines.delay

@Composable
fun HomeScreenContent(
    onNavigateRegistro: (() -> Unit)? = null,
    onNavigatePublicaciones: (() -> Unit)? = null
) {
    // Estados de visibilidad
    var showLogo by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(100)
        showLogo = true
        kotlinx.coroutines.delay(400)
        showButtons = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo animado con efecto de zoom y fade
        AnimatedVisibility(
            visible = showLogo,
            enter = fadeIn(animationSpec = tween(800)) +
                    scaleIn(
                        initialScale = 0.5f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Ícono de rata con pulso
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "logo_pulse"
                )

                Text(
                    text = "🐀",
                    fontSize = 80.sp,
                    modifier = Modifier.scale(scale)
                )

                Spacer(modifier = Modifier.height(16.dp))

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
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Botones animados
        AnimatedVisibility(
            visible = showButtons,
            enter = fadeIn(animationSpec = tween(600)) +
                    slideInVertically(
                        initialOffsetY = { 50 },
                        animationSpec = tween(600, easing = FastOutSlowInEasing)
                    )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedHomeButton(
                    text = "Ir a registro",
                    onClick = { onNavigateRegistro?.invoke() },
                    delay = 0
                )

                AnimatedHomeButton(
                    text = "Ir a publicaciones",
                    onClick = { onNavigatePublicaciones?.invoke() },
                    delay = 100
                )
            }
        }
    }
}

@Composable
fun AnimatedHomeButton(
    text: String,
    onClick: () -> Unit,
    delay: Long
) {
    var visible by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(400)) +
                scaleIn(
                    initialScale = 0.8f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                )
    ) {
        BotonComponente(
            text = text,
            onClick = {
                isPressed = true
                onClick()
            },
            enabled = true,
            modifier = Modifier.scale(scale)
        )
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(150)
            isPressed = false
        }
    }
}

@Composable
fun HomeScreen(navController: NavController, viewModel: UsuarioViewModel) {
    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            HomeScreenContent(
                onNavigateRegistro = { navController.navigate("registro") },
                onNavigatePublicaciones = { navController.navigate("publicaciones") }
            )
        }
    }
}