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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import me.daltonbsf.unirun.ui.components.BottomNavigationBar
import me.daltonbsf.unirun.ui.screens.LoginScreen
import me.daltonbsf.unirun.ui.screens.PeopleChatScreen
import me.daltonbsf.unirun.ui.components.TopBar
import me.daltonbsf.unirun.ui.screens.ChatScreen
import me.daltonbsf.unirun.ui.theme.UniRunTheme
import androidx.compose.runtime.getValue
import me.daltonbsf.unirun.model.caronaChatList
import me.daltonbsf.unirun.model.peopleChatList
import me.daltonbsf.unirun.ui.screens.CaronaChatScreen

class MainActivity : ComponentActivity() {
    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isLoggedIn = remember { mutableStateOf(true) }
            val navController = rememberNavController()
            val isDarkTheme = remember { mutableStateOf(false) }
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            UniRunTheme (darkTheme = isDarkTheme.value) {
                if (!isLoggedIn.value) {
                    LoginScreen(onLoginSuccess = { isLoggedIn.value = true },  onSignUpClick = { navController.navigate("signup")} )
                } else {
                    Scaffold(
                        topBar = {
                            // Mostrar TopBar principal apenas se não estiver na rota do chat individual
                            if (currentRoute?.startsWith("peopleChat/{chatName}") == false && currentRoute.startsWith("caronaChat/{chatName}") == false) {
                                TopBar(
                                    onThemeToggle = { isDarkTheme.value = !isDarkTheme.value },
                                    onOpenDrawer = { /* TODO: Open drawer */ }
                                )
                            }
                        },
                        bottomBar = {
                            // Mostrar BottomNavigationBar principal apenas se não estiver na rota do chat individual
                            if (currentRoute?.startsWith("peopleChat/{chatName}") == false && currentRoute.startsWith("caronaChat/{chatName}") == false) {
                                BottomNavigationBar(navController) }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "chats/people",
                            Modifier.padding(innerPadding)
                        ) {
                            composable("chats/people") { PeopleChatScreen(navController) }
                            composable("chats/carona") { CaronaChatScreen(navController) }
                            composable("peopleChat/{chatName}") { navBackStackEntry ->
                                val chatName = navBackStackEntry.arguments?.getString("chatName")
                                if (chatName != null) {
                                    ChatScreen(peopleChatList.first(), navController)
                                }
                            }
                            composable("caronaChat/{chatName}") { navBackStackEntry ->
                                val chatName = navBackStackEntry.arguments?.getString("chatName")
                                if (chatName != null) {
                                    ChatScreen(caronaChatList.first(), navController)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}