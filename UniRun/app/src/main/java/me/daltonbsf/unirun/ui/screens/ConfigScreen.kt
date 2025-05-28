package me.daltonbsf.unirun.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.daltonbsf.unirun.R

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
            ConfigCard(
                modifier = Modifier.padding(bottom = 16.dp),
                painter = painterResource(R.drawable.notification_icon),
                title = "Notificações",
                clickable = false,
                rightUnit = {
                    var isChecked by remember { mutableStateOf(true) }
                    Switch(
                        checked = isChecked,
                        onCheckedChange = {
                            isChecked = it
                            // Adicionar a lógica para ativar/desativar notificações
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            )
            ConfigCard(
                painter = painterResource(R.drawable.account_icon),
                title = "Conta",
                clickable = true,
                onClick = { navController.navigate("accountSettings") },
                rightUnit = {
                    Image(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Arrow Icon",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(24.dp)
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
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = "Arrow Icon",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(24.dp)
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
                modifier = Modifier.weight(1f)
            )
            rightUnit()
        }
    }
}