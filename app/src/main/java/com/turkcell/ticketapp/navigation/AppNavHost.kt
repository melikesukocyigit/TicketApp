package com.turkcell.ticketapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.turkcell.core.domain.auth.AuthRepository
import com.turkcell.ticketapp.screen.HomeScreen
import com.turkcell.ticketapp.screen.LoginScreen
import com.turkcell.ticketapp.screen.RegisterScreen
import org.koin.compose.koinInject
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.toRoute
import com.turkcell.ticketapp.screen.CheckInScreen
import com.turkcell.ticketapp.screen.EventDetailScreen
import com.turkcell.ticketapp.screen.PendingPurchasesScreen
import com.turkcell.ticketapp.screen.TicketDetailScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    authRepository: AuthRepository = koinInject()
) {
    val isLoggedIn by authRepository.isLoggedIn.collectAsStateWithLifecycle(initialValue = null)

    when(isLoggedIn) {
        null -> SplashScreen()
        true -> AuthedNavHost(navController)
        false -> UnAuthedNavHost(navController)
    }
}

@Composable
private fun SplashScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
@Composable
private fun AuthedNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Home) {


        composable<Home> {
            HomeScreen(
                onEventClick = { eventId ->
                    navController.navigate(EventDetail(eventId))
                },
                onTicketClick = { ticketId ->
                    navController.navigate(TicketDetail(ticketId))
                },
                onPendingPurchasesClick = {
                    navController.navigate(PendingPurchases)
                },
                onCheckInClick = {
                    navController.navigate(CheckIn)
                }
            )
        }
        composable<TicketDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<TicketDetail>()

            TicketDetailScreen(
                ticketId = args.ticketId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<EventDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<EventDetail>()

            EventDetailScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToTickets = {
                    navController.navigate(Home) {
                        popUpTo(0)
                    }
                }
            )
        }
        composable<PendingPurchases> {
            PendingPurchasesScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable<CheckIn> { CheckInScreen(onBackClick = { navController.popBackStack() })
        }
    }
}

@Composable
private fun UnAuthedNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Login) {
        composable<Login> {
            LoginScreen(
                onLoginSuccess = {
                    // Not: Yeni mimaride giriş başarılı olduğunda authRepository'deki
                    // isLoggedIn state'i true olacağı için uygulama otomatik olarak
                    // AuthedNavHost'a (Home ekranına) geçiş yapacaktır.
                },
                onNavigateToRegister = { navController.navigate(Register) }
            )
        }

        composable<Register> {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Login)
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
    }
}