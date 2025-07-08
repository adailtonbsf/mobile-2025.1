package me.daltonbsf.unirun

import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresPermission
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import me.daltonbsf.unirun.data.UserPreferences
import me.daltonbsf.unirun.model.caronaChatList
import me.daltonbsf.unirun.model.userChatList
import me.daltonbsf.unirun.model.userList
import me.daltonbsf.unirun.ui.components.BottomNavigationBar
import me.daltonbsf.unirun.ui.components.TopBar
import me.daltonbsf.unirun.ui.screens.AboutScreen
import me.daltonbsf.unirun.ui.screens.AccountSettingsScreen
import me.daltonbsf.unirun.ui.screens.CaronaChatScreen
import me.daltonbsf.unirun.ui.screens.CaronaDetailsScreen
import me.daltonbsf.unirun.ui.screens.CaronaProfileScreen
import me.daltonbsf.unirun.ui.screens.CaronaScreen
import me.daltonbsf.unirun.ui.screens.ChatScreen
import me.daltonbsf.unirun.ui.screens.ConfigScreen
import me.daltonbsf.unirun.ui.screens.FAQScreen
import me.daltonbsf.unirun.ui.screens.LoginScreen
import me.daltonbsf.unirun.ui.screens.OfferCaronaScreen
import me.daltonbsf.unirun.ui.screens.ProfileScreen
import me.daltonbsf.unirun.ui.screens.RegistrationScreen
import me.daltonbsf.unirun.ui.screens.UserChatScreen
import me.daltonbsf.unirun.ui.theme.UniRunTheme

class MainActivity : ComponentActivity() {
    private lateinit var userPreferences: UserPreferences

    @ExperimentalAnimationApi
    @SuppressLint("ObsoleteSdkInt", "ScheduleExactAlarm")
    @RequiresPermission(POST_NOTIFICATIONS)
    @ExperimentalFoundationApi
    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userPreferences = UserPreferences.getInstance(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(POST_NOTIFICATIONS), 1001)
        }
        setContent {
            val isDarkTheme by userPreferences.getPreference(UserPreferences.THEME_KEY, "true")
                .collectAsState(initial = "true")

            var isLoggedIn = remember { mutableStateOf(false) } // PULAR LOGIN
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            UniRunTheme(darkTheme = isDarkTheme.toBoolean()) {
                val withoutTopBottomBar = listOf(
                    "peopleChat/{chatName}",
                    "caronaChat/{chatName}",
                    "offerCarona",
                    "caronaDetails/{caronaId}",
                    "caronaProfile/{chatName}",
                    "login",
                    "registration"
                )
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.6f)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(32.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .padding(vertical = 24.dp)
                                    .fillMaxWidth()
                            ) {
                                AsyncImage(
                                    model = userList[0].profileImageURL,
                                    contentDescription = "Foto de Perfil",
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(64.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    userList[0].name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    userList[0].bio,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(16.dp))
                            DrawerItem(
                                icon = Icons.Default.Home,
                                label = "Tela Inicial",
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    navController.navigate("chats/people")
                                }
                            )
                            DrawerItem(
                                icon = Icons.Default.Person,
                                label = "Perfil",
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    navController.navigate("profile")
                                }
                            )
                            DrawerItem(
                                icon = Icons.Default.Settings,
                                label = "Configurações",
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    navController.navigate("config")
                                }
                            )
                            DrawerItem(
                                icon = Icons.Default.Info, // Use um ícone apropriado
                                label = "FAQ",
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    navController.navigate("faq")
                                }
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                            DrawerItem(
                                icon = Icons.AutoMirrored.Filled.Logout,
                                label = "Sair",
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                    isLoggedIn.value = false
                                }
                            )
                        }
                    },
                    content = {
                        Scaffold(
                            topBar = {
                                if (!withoutTopBottomBar.contains(currentRoute)) {
                                    TopBar(
                                        onThemeToggle = {
                                            scope.launch {
                                                val newThemeValue = (!isDarkTheme.toBoolean()).toString()
                                                userPreferences.savePreference(UserPreferences.THEME_KEY, newThemeValue)
                                            }
                                        },
                                        onOpenDrawer = { scope.launch { drawerState.open() } },
                                        isDarkTheme.toBoolean()
                                    )
                                }
                            },
                            bottomBar = {
                                if (!withoutTopBottomBar.contains(currentRoute)) {
                                    BottomNavigationBar(navController)
                                }
                            }
                        ) { innerPadding ->
                            NavHost(
                                navController = navController,
                                startDestination = if (isLoggedIn.value) "chats/people" else "login",
                                enterTransition = {
                                    if (isChatSwitch(initialState, targetState)) {
                                        fadeIn(animationSpec = tween(0))
                                    } else {
                                        slideInHorizontally(initialOffsetX = { it })
                                    }
                                },
                                exitTransition = {
                                    if (isChatSwitch(initialState, targetState)) {
                                        fadeOut(animationSpec = tween(0))
                                    } else {
                                        slideOutHorizontally(targetOffsetX = { -it })
                                    }
                                },
                                popEnterTransition = {
                                    if (isChatSwitch(initialState, targetState)) {
                                        fadeIn(animationSpec = tween(0))
                                    } else {
                                        slideInHorizontally(initialOffsetX = { -it })
                                    }
                                },
                                popExitTransition = {
                                    if (isChatSwitch(initialState, targetState)) {
                                        fadeOut(animationSpec = tween(0))
                                    } else {
                                        slideOutHorizontally(targetOffsetX = { it })
                                    }
                                },
                                modifier = Modifier.padding(innerPadding)
                            ) {
                                composable("login") {
                                    LoginScreen(
                                        navController,
                                        {
                                            scope.launch {
                                                val newThemeValue = (!isDarkTheme.toBoolean()).toString()
                                                userPreferences.savePreference(UserPreferences.THEME_KEY, newThemeValue)
                                            }
                                        },
                                        isDarkTheme.toBoolean()
                                    )
                                }
                                composable("registration") {
                                    RegistrationScreen {
                                        isLoggedIn.value = true
                                        navController.navigate("chats/people") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                }
                                composable("faq") { FAQScreen() }
                                composable("chats/people") { UserChatScreen(navController) }
                                composable("chats/carona") { CaronaChatScreen(navController) }
                                composable("carona") { CaronaScreen(navController) }
                                composable("offerCarona") { OfferCaronaScreen(navController) }
                                composable("config") { ConfigScreen(navController) }
                                composable("accountSettings") { AccountSettingsScreen(userList[0], navController) }
                                composable("about") { AboutScreen() }
                                composable("profile") { ProfileScreen(userList[0]) }
                                composable("peopleChat/{chatName}") { navBackStackEntry ->
                                    val chatName = navBackStackEntry.arguments?.getString("chatName")
                                    if (chatName != null) {
                                        ChatScreen(userChatList.first(), navController)
                                    }
                                }
                                composable("caronaChat/{chatName}") { navBackStackEntry ->
                                    val chatName = navBackStackEntry.arguments?.getString("chatName")
                                    if (chatName != null) {
                                        ChatScreen(caronaChatList.first(), navController)
                                    }
                                }
                                composable("caronaProfile/{chatName}") { navBackStackEntry ->
                                    val chatName = navBackStackEntry.arguments?.getString("chatName")
                                    if (chatName != null) {
                                        CaronaProfileScreen(caronaChatList.first(), navController)
                                    }
                                }
                                composable("caronaDetails/{caronaId}") { navBackStackEntry ->
                                    val caronaId = navBackStackEntry.arguments?.getString("caronaId")
                                    if (caronaId != null) {
                                        CaronaDetailsScreen(caronaId, navController)
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    @Composable
    fun DrawerItem(icon: ImageVector, label: String, onClick: () -> Unit) {
        TextButton(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(icon, contentDescription = label)
                Spacer(modifier = Modifier.width(16.dp))
                Text(label, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

fun isChatSwitch(from: NavBackStackEntry, to: NavBackStackEntry): Boolean {
    val fromRoute = from.destination.route
    val toRoute = to.destination.route
    return (fromRoute == "chats/people" && toRoute == "chats/carona") ||
            (fromRoute == "chats/carona" && toRoute == "chats/people")
}