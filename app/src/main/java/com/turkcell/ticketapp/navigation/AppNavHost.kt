package com.turkcell.ticketapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.turkcell.core.domain.auth.AuthRepository
import com.turkcell.core.domain.auth.UserRole
import com.turkcell.ticketapp.screen.EventDetailScreen
import com.turkcell.ticketapp.screen.HomeScreen
import com.turkcell.ticketapp.screen.LoginScreen
import com.turkcell.ticketapp.screen.PendingPurchasesScreen
import com.turkcell.ticketapp.screen.RegisterScreen
import com.turkcell.ticketapp.screen.StaffScreen
import com.turkcell.ticketapp.screen.TicketDetailScreen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    authRepository: AuthRepository = koinInject()
) {
    val isLoggedIn by authRepository.isLoggedIn.collectAsStateWithLifecycle(initialValue = null)
    val currentUser by authRepository.currentUser.collectAsStateWithLifecycle(initialValue = null)

    when (isLoggedIn) {
        null -> SplashScreen()
        false -> UnAuthedNavHost(navController)
        true -> {
            if (currentUser == null) {
                SplashScreen()
            } else {
                AuthedNavHost(navController, currentUser!!.role)
            }
        }
    }
}

@Composable
private fun SplashScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun AuthedNavHost(
    navController: NavHostController,
    userRole: UserRole,
    authRepository: AuthRepository = koinInject()
) {
    val scope = rememberCoroutineScope()

    val startDest: Any = when (userRole) {
        UserRole.STAFF -> StaffScreen
        UserRole.ADMIN -> AdminScreen
        else -> Home
    }

    NavHost(navController = navController, startDestination = startDest) {

        composable<Home> {
            HomeScreen(
                onEventClick = { eventId -> navController.navigate(EventDetail(eventId)) },
                onTicketClick = { id, qr -> navController.navigate(TicketDetail(id, qr)) },
                onPendingPurchasesClick = { navController.navigate(PendingPurchases) },
            )
        }

        // PERSONEL EKRANI VE ÇIKIŞ İŞLEMİ
        composable<StaffScreen> {
            StaffScreen(
                onLogoutClick = {
                    scope.launch {
                        authRepository.logout()
                    }
                }
            )
        }

        composable<AdminScreen> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Admin Paneli Yakında Eklenecek")
            }
        }

        composable<TicketDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<TicketDetail>()
            TicketDetailScreen(
                ticketId = args.ticketId,
                qrCode = args.qrCode,
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
    }
}

@Composable
private fun UnAuthedNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Login) {
        composable<Login> {
            LoginScreen(
                onLoginSuccess = {
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