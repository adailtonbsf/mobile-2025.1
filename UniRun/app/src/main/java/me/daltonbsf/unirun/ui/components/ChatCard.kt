package me.daltonbsf.unirun.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.daltonbsf.unirun.R
import me.daltonbsf.unirun.model.Chat
import me.daltonbsf.unirun.viewmodel.ChatViewModel
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Composable
fun ChatCard(chat: Chat, chatViewModel: ChatViewModel) {
    var now by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            kotlinx.coroutines.delay(60000)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.placeholder),
            contentDescription = "Profile Image",
            modifier = Modifier.size(56.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            val lastMessage = chat.lastMessage
            Row(verticalAlignment = Alignment.CenterVertically) {
                var title by remember { mutableStateOf("") }

                LaunchedEffect(chat) {
                    title = chatViewModel.getChatTitle(chat)
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (chat.isPinned.value) {
                    Image(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Conversa fixada",
                        modifier = Modifier
                            .size(16.dp)
                            .padding(start = 4.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                    )
                }
            }
            Text(
                text = lastMessage?.content ?: "Nenhuma mensagem ainda",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(horizontalAlignment = Alignment.End) {
            chat.lastMessage?.timestamp?.let { timestamp ->
                val messageDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
                val duration = Duration.between(messageDateTime, now)
                Text(
                    text = when {
                        duration.toDays() > 0 -> "${duration.toDays()}d"
                        duration.toHours() > 0 -> "${duration.toHours()}h"
                        duration.toMinutes() > 0 -> "${duration.toMinutes()}m"
                        else -> "agora"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}