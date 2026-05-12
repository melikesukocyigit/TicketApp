package com.turkcell.ticketapp.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.turkcell.ticketapp.screen.LoginScreen
import com.turkcell.ticketapp.screen.RegisterScreen


@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    //authRepository: AuthRepository = koinInject()
)
{
    NavHost(navController = navController, startDestination = Login) {
        composable<Login> {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Home) },
                onNavigateToRegister = { navController.navigate(Register) }
            )
        }

        composable<Register> {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Login)
                },
                onNavigateToLogin = {
                    navController.popBackStack() // Geri dön
                }
            )
        }

        composable<Home> {
            Text("Ana Sayfaya Hoş Geldiniz!")
        }
    }
}