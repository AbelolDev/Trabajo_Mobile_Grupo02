import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fororata.components.BottomNavBar
import com.example.fororata.components.PostCard
import com.example.fororata.viewmodel.PostViewModel

@Composable
fun TopPostsScreen(
    navController: NavController,
    viewModel: PostViewModel = viewModel()
) {

    val topPosts by viewModel.topPosts.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // importante: evita que el contenido quede debajo de la barra
                .padding(16.dp)
        ) {

            Text(
                text = "Top 10 publicaciones del día",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "Error desconocido",
                        color = Color.Red
                    )
                }

                else -> {
                    LazyColumn {
                        items(topPosts) { post ->
                            PostCard(post)
                        }
                    }
                }
            }
        }
    }
}
