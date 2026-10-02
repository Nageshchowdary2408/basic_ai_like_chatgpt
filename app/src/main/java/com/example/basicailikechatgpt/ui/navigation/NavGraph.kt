package com.example.basicailikechatgpt.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.basicailikechatgpt.ui.auth.LoginScreen
import com.example.basicailikechatgpt.ui.auth.RegisterScreen
import com.example.basicailikechatgpt.ui.chat.ChatScreen
import com.example.basicailikechatgpt.ui.profile.ProfileScreen
import com.example.basicailikechatgpt.ui.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Chat : Screen("chat")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = {
                    navController.navigate(Screen.Chat.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onRegisterSuccess = {
                    navController.navigate(Screen.Chat.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Chat.route) {
            ChatScreen(
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Chat.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}