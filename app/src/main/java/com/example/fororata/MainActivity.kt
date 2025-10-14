package com.example.fororata
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.fororata.navigation.AppNavigation
import com.example.fororata.ui.theme.ForoRataTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ForoRataTheme {
                Scaffold { innerPadding ->
                    Box(modifier = Modifier.padding(paddingValues = innerPadding)) {
                        AppNavigation()
                    }
                }
            }
        }
    }
}
