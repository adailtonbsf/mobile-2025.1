package me.daltonbsf.unirun.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import me.daltonbsf.unirun.model.Carona
import me.daltonbsf.unirun.model.Message
import me.daltonbsf.unirun.model.User
import me.daltonbsf.unirun.viewmodel.AuthViewModel
import me.daltonbsf.unirun.viewmodel.CaronaViewModel
import me.daltonbsf.unirun.viewmodel.ChatViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@ExperimentalMaterial3Api
@Composable
fun ChatScreen(
    chatId: String,
    navController: NavController,
    chatViewModel: ChatViewModel,
    authViewModel: AuthViewModel,
    caronaViewModel: CaronaViewModel
) {
    var messageText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val chatList by chatViewModel.chatList.collectAsState()
    val messages by chatViewModel.messages.collectAsState()
    val currentUser by authViewModel.user.collectAsState()

    val chat = remember(chatList, chatId) {
        chatList.find { it.id == chatId }
    }

    var chatTitle by remember { mutableStateOf("") }
    var carona by remember { mutableStateOf<Carona?>(null) }
    var otherUser by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(chat, currentUser) {
        if (chat != null) {
            chatTitle = chatViewModel.getChatTitle(chat)
            if (chat.type == "group") {
                caronaViewModel.findCaronaByChatId(chat.id) { result ->
                    carona = result
                }
            } else if (chat.type == "private") {
                val otherUserId = chat.participants.firstOrNull { it != currentUser?.uid }
                if (otherUserId != null) {
                    otherUser = authViewModel.getUserData(otherUserId)
                }
            }
        }
    }

    LaunchedEffect(chatId) {
        chatViewModel.loadMessages(chatId)
    }

    LaunchedEffect(messages) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = if (chat?.type == "group" && carona != null) {
                            Modifier.clickable {
                                navController.navigate("caronaProfile/${carona!!.id}")
                            }
                        } else {
                            Modifier
                        }
                    ) {
                        val imageModifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)

                        if (chat?.type == "private" && otherUser != null) {
                            Image(
                                painter = rememberAsyncImagePainter(model = otherUser?.profileImageURL),
                                contentDescription = "Foto de Perfil",
                                modifier = imageModifier,
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                imageVector = Icons.Default.Group,
                                contentDescription = "Ícone do Grupo",
                                modifier = imageModifier.padding(4.dp),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = chatTitle,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Digite uma mensagem...") },
                    shape = RoundedCornerShape(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {
                    if (messageText.isNotBlank()) {
                        coroutineScope.launch {
                            chatViewModel.sendMessage(chatId, messageText)
                            messageText = ""
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Enviar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                reverseLayout = true,
                state = listState
            ) {
                items(messages) { message ->
                    MessageBubble(
                        message = message,
                        isFromCurrentUser = message.senderId == currentUser?.uid,
                        isGroupChat = chat?.type == "group"
                    )
                }
            }
        }
    }
}

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun MessageBubble(message: Message, isFromCurrentUser: Boolean, isGroupChat: Boolean) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isFromCurrentUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isFromCurrentUser) {
            Spacer(modifier = Modifier.width(40.dp))
        }

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isFromCurrentUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.widthIn(max = screenWidth * 0.8f)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (isGroupChat && !isFromCurrentUser) {
                    Text(
                        text = message.senderName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = formatTimestamp(message.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 4.dp)
                )
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val messageDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
    val now = LocalDateTime.now()

    return if (messageDateTime.toLocalDate() == now.toLocalDate()) {
        messageDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    } else {
        messageDateTime.format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))
    }
}