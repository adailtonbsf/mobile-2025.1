package me.daltonbsf.unirun

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.daltonbsf.unirun.ui.BottomNavigationBar
import me.daltonbsf.unirun.ui.LoginScreen
import me.daltonbsf.unirun.ui.PeopleChatScreen
import me.daltonbsf.unirun.ui.TopBar
import me.daltonbsf.unirun.ui.theme.UniRunTheme

class MainActivity : ComponentActivity() {
    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isLoggedIn = remember { mutableStateOf(false) }
            val navController = rememberNavController()
            val isDarkTheme = remember { mutableStateOf(false) }
            UniRunTheme {
                if (!isLoggedIn.value) {
                    LoginScreen(onLoginSuccess = { isLoggedIn.value = true },  onSignUpClick = { navController.navigate("signup")} )
                } else {
                    Scaffold(
                        topBar = {
                            TopBar(
                                onThemeToggle = { isDarkTheme.value = !isDarkTheme.value },
                                onOpenDrawer = { /* TODO: Open drawer */ }
                            )
                        },
                        bottomBar = { BottomNavigationBar(navController) }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "chats/people",
                            Modifier.padding(innerPadding)
                        ) {
                            composable("chats/people") { PeopleChatScreen() }
                        }
                    }
                }
            }
        }
    }
}