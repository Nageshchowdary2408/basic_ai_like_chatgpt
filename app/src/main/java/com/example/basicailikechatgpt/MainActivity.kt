package com.example.basicailikechatgpt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.basicailikechatgpt.data.repository.AuthRepository
import com.example.basicailikechatgpt.ui.navigation.NavGraph
import com.example.basicailikechatgpt.ui.navigation.Screen
import com.example.basicailikechatgpt.ui.theme.AiAssistantTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Main Activity of the Professional AI Assistant application.
 * Utilizes Jetpack Compose for the UI, Hilt for Dependency Injection,
 * and follows Clean Architecture principles.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enables immersive edge-to-edge display
        enableEdgeToEdge()
        
        setContent {
            // Apply the app-wide Material 3 theme with support for Dark/Light modes
            AiAssistantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    // Route the user based on their authentication status
                    val startDestination = if (authRepository.isUserLoggedIn()) {
                        Screen.Chat.route
                    } else {
                        Screen.Login.route
                    }
                    
                    // Initialize the Navigation Graph with required screens
                    NavGraph(navController = navController, startDestination = startDestination)
                }
            }
        }
    }
}