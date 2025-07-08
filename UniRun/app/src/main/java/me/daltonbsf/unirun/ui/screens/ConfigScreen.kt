package me.daltonbsf.unirun.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import me.daltonbsf.unirun.R
import me.daltonbsf.unirun.data.UserPreferences

@Composable
fun ConfigScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp).padding(bottom = 16.dp)
            .fillMaxSize()
    ) {
        Text(
            "Configurações",
            modifier = Modifier.padding(bottom = 32.dp),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            NotificationConfigCard()
            ConfigCard(
                painter = painterResource(R.drawable.account_icon),
                title = "Conta",
                clickable = true,
                onClick = { navController.navigate("accountSettings") },
                rightUnit = {
                    Image(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Arrow Icon",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(48.dp)
                    )
                }
            )
            ConfigCard(
                painter = painterResource(R.drawable.about_icon),
                title = "Sobre",
                clickable = true,
                onClick = { navController.navigate("about") },
                rightUnit = {
                    Image(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Arrow Icon",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(48.dp)
                    )
                }
            )
        }
    }
}

@Composable
fun ConfigCard(
    modifier: Modifier = Modifier,
    painter: Painter,
    title: String,
    clickable: Boolean = false,
    rightUnit: @Composable () -> Unit,
    onClick: (() -> Unit)? = null
) {
    val cardModifier = if (clickable && onClick != null) {
        modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    } else {
        modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    }

    Card(modifier = cardModifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = painter,
                contentDescription = "$title Icon",
                modifier = Modifier.padding(horizontal = 8.dp).size(36.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface
            )
            rightUnit()
        }
    }
}

@Composable
fun NotificationConfigCard() {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences.getInstance(context) }
    val scope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }

    val allNotifications by userPreferences.getPreference(UserPreferences.ALL_NOTIFICATIONS_KEY, "true").collectAsState(initial = "true")
    val userNotifications by userPreferences.getPreference(UserPreferences.USER_NOTIFICATIONS_KEY, "true").collectAsState(initial = "true")
    val caronaNotifications by userPreferences.getPreference(UserPreferences.CARONA_NOTIFICATIONS_KEY, "true").collectAsState(initial = "true")
    val rideLeavingNotification by userPreferences.getPreference(UserPreferences.RIDE_LEAVING_NOTIFICATIONS_KEY, "true").collectAsState(initial = "true")
    val groupEntryExitNotification by userPreferences.getPreference(UserPreferences.GROUP_ENTRY_EXIT_NOTIFICATIONS_KEY, "true").collectAsState(initial = "true")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 350,
                    easing = FastOutSlowInEasing
                )
            )
    ) {
        ConfigCard(
            painter = painterResource(R.drawable.notification_icon),
            title = "Notificações",
            clickable = true,
            onClick = { expanded = !expanded },
            rightUnit = {
                Image(
                    imageVector = if (expanded)
                        Icons.Filled.ExpandLess
                    else
                        Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) "Recolher" else "Expandir",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(48.dp)
                )
            }
        )

        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        "Todas as notificações",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = allNotifications.toBoolean(),
                        onCheckedChange = { newValue ->
                            scope.launch {
                                userPreferences.savePreference(UserPreferences.ALL_NOTIFICATIONS_KEY, newValue.toString())
                            }
                        }
                    )
                }
                HorizontalDivider(
                    thickness = 4.dp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp).padding(top = 8.dp, bottom = 4.dp)
                ) {
                    Text(
                        "Mensagens de Pessoas",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = userNotifications.toBoolean(),
                        enabled = allNotifications.toBoolean(),
                        onCheckedChange = { newValue ->
                            scope.launch {
                                userPreferences.savePreference(UserPreferences.USER_NOTIFICATIONS_KEY, newValue.toString())
                            }
                        }
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        "Mensagens de Caronas",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = caronaNotifications.toBoolean(),
                        enabled = allNotifications.toBoolean(),
                        onCheckedChange = { newValue ->
                            scope.launch {
                                userPreferences.savePreference(UserPreferences.CARONA_NOTIFICATIONS_KEY, newValue.toString())
                            }
                        }
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        "Carona saindo para viagem",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = rideLeavingNotification.toBoolean(),
                        enabled = allNotifications.toBoolean(),
                        onCheckedChange = { newValue ->
                            scope.launch {
                                userPreferences.savePreference(UserPreferences.RIDE_LEAVING_NOTIFICATIONS_KEY, newValue.toString())
                            }
                        }
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp).padding(top = 4.dp, bottom = 8.dp)
                ) {
                    Text(
                        "Entrada/saída de pessoas em caronas",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = groupEntryExitNotification.toBoolean(),
                        enabled = allNotifications.toBoolean(),
                        onCheckedChange = { newValue ->
                            scope.launch {
                                userPreferences.savePreference(UserPreferences.GROUP_ENTRY_EXIT_NOTIFICATIONS_KEY, newValue.toString())
                            }
                        }
                    )
                }
            }
        }
    }
}