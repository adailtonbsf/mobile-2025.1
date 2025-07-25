package me.daltonbsf.unirun.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.daltonbsf.unirun.R
import me.daltonbsf.unirun.model.User
import me.daltonbsf.unirun.ui.components.UserCard
import me.daltonbsf.unirun.viewmodel.AuthViewModel
import me.daltonbsf.unirun.viewmodel.CaronaViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaronaDetailsScreen(
    caronaId: String,
    navController: NavController,
    caronaViewModel: CaronaViewModel,
    authViewModel: AuthViewModel
) {
    val caronaState by caronaViewModel.selectedCarona.collectAsState()
    var creatorState by remember { mutableStateOf<User?>(null) }
    val currentUser by authViewModel.user.collectAsState()

    LaunchedEffect(caronaId) {
        caronaViewModel.loadCaronaDetails(caronaId)
    }

    LaunchedEffect(caronaState) {
        caronaState?.creator?.let { creatorId ->
            creatorState = authViewModel.getUserData(creatorId)
        }
    }

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
            caronaState?.let { carona ->
                val isUserInCarona = carona.participants.contains(currentUser?.uid)

                Button(
                    onClick = {
                        if (isUserInCarona) {
                            navController.navigate("caronaChat/${carona.chatId}")
                        } else {
                            caronaViewModel.joinCarona(carona) { success ->
                                if (success) {
                                    navController.navigate("caronaChat/${carona.chatId}")
                                }
                                // Opcional: Adicionar um else para mostrar uma mensagem de erro
                            }
                        }
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    enabled = carona.seatsAvailable > 0 || isUserInCarona // Desabilita se não houver vagas e o usuário não estiver na carona
                ) {
                    Text(
                        text = if (isUserInCarona) "ABRIR CHAT DA CARONA" else "ENTRAR NA CARONA",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (caronaState == null || creatorState == null) {
                CircularProgressIndicator()
            } else {
                val carona = caronaState!!
                val creator = creatorState!!
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(R.drawable.location_pin_icon),
                                contentDescription = "Location Icon",
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(64.dp)
                            )
                            Text(
                                text = carona.originName,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .basicMarquee(),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        VerticalDivider(
                            modifier = Modifier
                                .fillMaxHeight(0.08f)
                                .padding(start = 45.dp)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(R.drawable.location_pin_icon),
                                contentDescription = "Location Icon",
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(64.dp)
                            )
                            Text(
                                text = carona.destinyName,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .basicMarquee(),
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                    val departureMillis = carona.departureDate.toLongOrNull()
                    val departureDateTimeText = if (departureMillis != null) {
                        val instant = Instant.ofEpochMilli(departureMillis)
                        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                        dateTime.format(
                            DateTimeFormatter.ofPattern(
                                "HH:mm • d 'de' MMMM 'de' yyyy",
                                Locale("pt", "BR")
                            )
                        )
                    } else {
                        "Data inválida"
                    }

                    Text(
                        departureDateTimeText,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    UserCard(creator, navController)
                }
            }
        }
    }
}