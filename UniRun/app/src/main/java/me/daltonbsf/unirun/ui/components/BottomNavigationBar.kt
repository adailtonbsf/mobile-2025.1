package me.daltonbsf.unirun.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import me.daltonbsf.unirun.R
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        val items = listOf(
            Triple("carona", "Carona", painterResource(R.drawable.carona_icon)),
            Triple("chats/people", "Chat", painterResource(R.drawable.icon_chat)),
            Triple("config", "Config", Icons.Default.Settings)
        )
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { (route, label, icon) ->
            val isChat = route == "chats/people"
            NavigationBarItem(
                icon = {
                    when (icon) {
                        is ImageVector -> Icon(icon, contentDescription = label)
                        else -> Icon(
                            painter = icon as Painter,
                            contentDescription = label,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = { Text(label) },
                selected = if (isChat) currentRoute == "chats/people" || currentRoute == "chats/carona" else currentRoute == route,
                onClick = {
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}