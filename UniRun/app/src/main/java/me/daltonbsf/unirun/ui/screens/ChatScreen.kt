package me.daltonbsf.unirun.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import me.daltonbsf.unirun.R
import me.daltonbsf.unirun.model.CaronaChat
import me.daltonbsf.unirun.model.ChatInterface
import me.daltonbsf.unirun.model.Message
import me.daltonbsf.unirun.model.PeopleChat
import me.daltonbsf.unirun.model.peopleChatList
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@ExperimentalMaterial3Api
@Composable
fun ChatScreen(chat: ChatInterface, navController: NavController) {
    var messageText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = chat.profileImageURL,
                            contentDescription = "Profile Image",
                            placeholder = painterResource(R.drawable.placeholder),
                            error = painterResource(R.drawable.error),
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            chat.getName(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
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
                ),
                modifier = Modifier.offset(y = (-54).dp)
            )
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
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp),
                reverseLayout = true
            ) {
                items(chat.messages.toList().reversed()) { message ->
                    if (chat is PeopleChat) {
                        MessageBubble(message)
                    } else if (chat is CaronaChat) {
                        GroupMessageBubble(message, chat.profileImageURL)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    label = { Text("Digite uma mensagem") },
                    modifier = Modifier.weight(1f),
                    maxLines = 4
                )
                IconButton(onClick = {
                    if (messageText.isNotBlank()) {
                        val newMessage = Message(
                            id = (chat.messages.maxOfOrNull { it.id } ?: 0) + 1,
                            sender = "me",
                            receiver = chat.getName(),
                            content = messageText,
                            date = LocalDateTime.now()
                        )
                        chat.messages.add(newMessage)
                        messageText = ""
                    }
                }) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar mensagem")
                }
            }
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.sender == "me") Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            tonalElevation = 1.dp,
            modifier = Modifier.widthIn(max = screenWidth * 0.8f),
            color = if (message.sender == "me") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = message.content)
                Text(
                    text = message.date.format(DateTimeFormatter.ofPattern("HH:mm")),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun GroupMessageBubble(message: Message, model: String) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (message.sender == "me") Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (message.sender != "me") {
            AsyncImage(
                model = when (message.sender) {
                    "Alice" -> peopleChatList[0].profileImageURL
                    "Bob" -> peopleChatList[1].profileImageURL
                    "Charlie" -> peopleChatList[2].profileImageURL
                    else -> ""
                },
                contentDescription = "Sender Image",
                placeholder = painterResource(R.drawable.placeholder),
                error = painterResource(R.drawable.error),
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .padding(end = 4.dp),
                contentScale = ContentScale.Crop
            )
        }
        Surface(
            shape = RoundedCornerShape(8.dp),
            tonalElevation = 1.dp,
            modifier = Modifier.widthIn(max = screenWidth * 0.8f),
            color = if (message.sender == "me") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                if (message.sender != "me") {
                    Text(
                        text = message.sender,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.7f)),
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
                Text(text = message.content)
                Text(
                    text = message.date.format(DateTimeFormatter.ofPattern("HH:mm")),
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
        if (message.sender == "me") {
            AsyncImage(
                model = model,
                contentDescription = "Sender Image",
                placeholder = painterResource(R.drawable.placeholder),
                error = painterResource(R.drawable.error),
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .padding(start = 4.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}