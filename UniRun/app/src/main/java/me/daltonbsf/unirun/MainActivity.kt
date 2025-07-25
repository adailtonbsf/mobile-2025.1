package me.daltonbsf.unirun

import android.Manifest.permission.POST_NOTIFICATIONS
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.launch
import me.daltonbsf.unirun.data.AuthRepository
import me.daltonbsf.unirun.data.CaronaRepository
import me.daltonbsf.unirun.data.ChatRepository
import me.daltonbsf.unirun.data.UserPreferences
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
import me.daltonbsf.unirun.viewmodel.AuthViewModel
import me.daltonbsf.unirun.viewmodel.AuthViewModelFactory
import me.daltonbsf.unirun.viewmodel.CaronaViewModel
import me.daltonbsf.unirun.viewmodel.CaronaViewModelFactory
import me.daltonbsf.unirun.viewmodel.ChatViewModel
import me.daltonbsf.unirun.viewmodel.ChatViewModelFactory
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var userPreferences: UserPreferences

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository(), userPreferences)
    }

    private val chatViewModel: ChatViewModel by viewModels {
        ChatViewModelFactory(ChatRepository(), AuthRepository())
    }

    private val caronaViewModel: CaronaViewModel by viewModels {
        CaronaViewModelFactory(CaronaRepository(), AuthRepository(), ChatRepository())
    }

    @ExperimentalAnimationApi
    @SuppressLint("ObsoleteSdkInt", "ScheduleExactAlarm")
    @RequiresPermission(POST_NOTIFICATIONS)
    @ExperimentalFoundationApi
    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userPreferences = UserPreferences.getInstance(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(POST_NOTIFICATIONS), 1001)
        }
        try {
            val apiKey = BuildConfig.MAPS_API_KEY
            if (!Places.isInitialized()) {
                Places.initialize(applicationContext, apiKey, Locale("pt"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        setContent {
            val isDarkTheme by userPreferences.getPreference(UserPreferences.THEME_KEY, "true")
                .collectAsState(initial = "true")

            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val currentUser by authViewModel.user.collectAsState()
            LaunchedEffect(authViewModel.isUserLoggedIn()) {
                if (authViewModel.isUserLoggedIn()) {
                    chatViewModel.loadUserChats(applicationContext)
                }
            }
            UniRunTheme(darkTheme = isDarkTheme.toBoolean()) {
                val withoutTopBottomBar = listOf(
                    "peopleChat/{chatId}",
                    "caronaChat/{chatId}",
                    "offerCarona",
                    "caronaDetails/{caronaId}",
                    "caronaProfile/{caronaId}",
                    "login",
                    "registration"
                )
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    gesturesEnabled = !withoutTopBottomBar.contains(currentRoute),
                    drawerContent = {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.6f)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(32.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            currentUser?.let { user ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .padding(vertical = 24.dp)
                                        .fillMaxWidth()
                                ) {
                                    AsyncImage(
                                        model = user.profileImageURL,
                                        contentDescription = "Foto de Perfil",
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .size(64.dp)
                                            .clip(CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        user.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        user.bio,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Center
                                    )
                                }
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
                                icon = Icons.Default.Info,
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
                                    authViewModel.logout()
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
                                startDestination = if (authViewModel.isUserLoggedIn()) "chats/people" else "login",
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
                                        authViewModel,
                                        onThemeToggle = {
                                            scope.launch {
                                                val newThemeValue = (!isDarkTheme.toBoolean()).toString()
                                                userPreferences.savePreference(UserPreferences.THEME_KEY, newThemeValue)
                                            }
                                        },
                                        isDarkTheme.toBoolean()
                                    )
                                }
                                composable("registration") {
                                    RegistrationScreen(
                                        authViewModel,
                                        backToLogin = {
                                            navController.navigate("login") {
                                                popUpTo(0) { inclusive = true }
                                            }
                                        }
                                    )
                                }
                                composable("faq") { FAQScreen() }
                                composable("chats/people") { UserChatScreen(navController, chatViewModel, authViewModel) }
                                composable("chats/carona") { CaronaChatScreen(navController, authViewModel, chatViewModel) }
                                composable("carona") { CaronaScreen(navController, caronaViewModel) }
                                composable("offerCarona") { OfferCaronaScreen(navController, caronaViewModel) }
                                composable("config") { ConfigScreen(navController) }
                                composable("accountSettings") { AccountSettingsScreen(currentUser!!, navController) }
                                composable("about") { AboutScreen() }
                                composable("profile") { ProfileScreen(currentUser!!) }
                                composable("peopleChat/{chatId}") { navBackStackEntry ->
                                    val chatId = navBackStackEntry.arguments?.getString("chatId")
                                    if (chatId != null) {
                                        ChatScreen(chatId, navController, chatViewModel, authViewModel, caronaViewModel)
                                    }
                                }
                                composable("caronaChat/{chatId}") { navBackStackEntry ->
                                    val chatId = navBackStackEntry.arguments?.getString("chatId")
                                    if (chatId != null) {
                                        ChatScreen(chatId, navController, chatViewModel, authViewModel, caronaViewModel)
                                    }
                                }
                                composable("caronaProfile/{caronaId}") { navBackStackEntry ->
                                    val caronaId = navBackStackEntry.arguments?.getString("caronaId")
                                    if (caronaId != null) {
                                        CaronaProfileScreen(
                                            caronaId = caronaId,
                                            navController = navController,
                                            caronaViewModel = caronaViewModel,
                                            authViewModel = authViewModel
                                        )
                                    }
                                }
                                composable("caronaDetails/{caronaId}") { navBackStackEntry ->
                                    val caronaId = navBackStackEntry.arguments?.getString("caronaId")
                                    if (caronaId != null) {
                                        CaronaDetailsScreen(caronaId, navController, caronaViewModel, authViewModel)
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