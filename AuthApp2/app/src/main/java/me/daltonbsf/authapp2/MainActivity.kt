package me.daltonbsf.authapp2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.daltonbsf.authapp2.data.AuthRepository
import me.daltonbsf.authapp2.ui.theme.AuthApp2Theme
import me.daltonbsf.authapp2.ui.view.HomeScreen
import me.daltonbsf.authapp2.ui.view.LoginScreen
import me.daltonbsf.authapp2.ui.view.RegisterScreen
import me.daltonbsf.authapp2.ui.view.ResetPasswordScreen
import me.daltonbsf.authapp2.viewmodel.AuthViewModel
import me.daltonbsf.authapp2.viewmodel.AuthViewModelFactory

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuthApp2Theme {
                AppNavigator(authViewModel = authViewModel)
            }
        }
    }
}

@Composable
fun AppNavigator(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val startDestination = if (authViewModel.isUserLogged()) "home" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(navController = navController, authViewModel = authViewModel)
        }
        composable("register") {
            RegisterScreen(navController = navController, authViewModel = authViewModel)
        }
        composable("reset_password") {
            ResetPasswordScreen(navController = navController, authViewModel = authViewModel)
        }
        composable("home") {
            HomeScreen(navController = navController, authViewModel = authViewModel)
        }
    }
}