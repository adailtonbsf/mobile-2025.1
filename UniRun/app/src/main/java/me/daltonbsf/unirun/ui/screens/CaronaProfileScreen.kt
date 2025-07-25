package me.daltonbsf.unirun.ui.screens

import android.Manifest.permission.POST_NOTIFICATIONS
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import me.daltonbsf.unirun.R
import me.daltonbsf.unirun.model.User
import me.daltonbsf.unirun.ui.components.UserDetailsCard
import me.daltonbsf.unirun.viewmodel.AuthViewModel
import me.daltonbsf.unirun.viewmodel.CaronaViewModel
import me.daltonbsf.unirun.viewmodel.ChatViewModel

@RequiresPermission(POST_NOTIFICATIONS)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaronaProfileScreen(
    caronaId: String,
    navController: NavController,
    caronaViewModel: CaronaViewModel,
    authViewModel: AuthViewModel,
    chatViewModel: ChatViewModel
) {
    val carona by caronaViewModel.selectedCarona.collectAsState()
    var creator by remember { mutableStateOf<User?>(null) }
    var participants by remember { mutableStateOf<List<User>>(emptyList()) }
    val currentUser by authViewModel.user.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    var initialLoading by remember { mutableStateOf(true) }

    LaunchedEffect(caronaId) {
        caronaViewModel.loadCaronaDetails(caronaId)
    }

    LaunchedEffect(carona) {
        carona?.let {
            creator = authViewModel.getUserData(it.creator)
            val participantUsers = it.participants.mapNotNull { participantId ->
                authViewModel.getUserData(participantId)
            }
            participants = participantUsers
            initialLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            carona?.let { currentCarona ->
                currentUser?.let { user ->
                    val isCreator = user.uid == currentCarona.creator
                    Button(
                        onClick = {
                            isLoading = true
                            if (isCreator) {
                                caronaViewModel.cancelCarona(currentCarona) { success ->
                                    if (success) {
                                        navController.popBackStack("chats/carona", inclusive = true)
                                    }
                                    isLoading = false
                                }
                            } else {
                                caronaViewModel.leaveCarona(currentCarona) { success ->
                                    if (success) {
                                        navController.popBackStack("chats/carona", inclusive = true)
                                    }
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text(if (isCreator) "Cancelar carona" else "Sair da carona")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (initialLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                carona?.let {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${it.originName} ➜ ${it.destinyName}",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(16.dp),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        creator?.let { creatorUser ->
                            AsyncImage(
                                model = creatorUser.profileImageURL,
                                contentDescription = "Rider Profile Image",
                                modifier = Modifier
                                    .size(128.dp)
                                    .clip(CircleShape),
                                error = painterResource(id = R.drawable.error)
                            )
                            Text(
                                "Motorista: ${creatorUser.name}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(8.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Text(
                        "Participantes (${participants.size})",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    LazyColumn {
                        items(participants) { user ->
                            UserDetailsCard(user, navController, chatViewModel)
                        }
                    }
                }
            }
        }
    }
}