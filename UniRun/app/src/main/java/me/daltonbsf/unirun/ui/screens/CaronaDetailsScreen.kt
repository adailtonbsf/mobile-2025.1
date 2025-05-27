package me.daltonbsf.unirun.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.daltonbsf.unirun.R
import me.daltonbsf.unirun.model.caronaList
import me.daltonbsf.unirun.model.userList
import me.daltonbsf.unirun.ui.components.UserCard
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun VerticalDivider(modifier: Modifier = Modifier, color: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)) {
    Box(
        modifier
            .fillMaxHeight(0.2f)
            .width(5.dp)
            .background(color = color)
    )
}

@ExperimentalMaterial3Api
@Composable
fun CaronaDetailsScreen(
    caronaId: String,
    navController: NavController
) {
    val carona = caronaList[caronaId.toInt()-1]
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes da carona") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            Button(
                onClick = { /* TODO: Implement join carona logic */ },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
            ) {
                Text(
                    "Entrar na Carona",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.location_pin_icon),
                        contentDescription = "Location Icon",
                        modifier = Modifier.padding(16.dp).size(64.dp)
                    )
                    Text(
                        text = carona.origin,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp).basicMarquee(),
                        fontWeight = FontWeight.Bold
                    )
                }
                VerticalDivider(modifier = Modifier.fillMaxHeight(0.08f).padding(start = 45.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.location_pin_icon),
                        contentDescription = "Location Icon",
                        modifier = Modifier.padding(16.dp).size(64.dp)
                    )
                    Text(
                        text = carona.destiny,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp).basicMarquee(),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            Text(
                carona.departureDate.format(DateTimeFormatter.ofPattern("HH:mm • d 'de' MMMM 'de' yyyy", Locale("pt", "BR"))),
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
            UserCard(userList[0])
        }
    }
}