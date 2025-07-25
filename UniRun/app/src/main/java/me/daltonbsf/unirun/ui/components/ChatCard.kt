package me.daltonbsf.unirun.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import me.daltonbsf.unirun.model.Chat
import me.daltonbsf.unirun.model.User
import me.daltonbsf.unirun.viewmodel.AuthViewModel
import me.daltonbsf.unirun.viewmodel.ChatViewModel
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Composable
fun ChatCard(chat: Chat, chatViewModel: ChatViewModel, authViewModel: AuthViewModel) {
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    var otherUser by remember { mutableStateOf<User?>(null) }
    var title by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            kotlinx.coroutines.delay(60000)
        }
    }

    LaunchedEffect(chat) {
        title = chatViewModel.getChatTitle(chat)
        if (chat.type == "private") {
            val currentUserId = authViewModel.user.value?.uid
            val otherUserId = chat.participants.firstOrNull { it != currentUserId }
            if (otherUserId != null) {
                otherUser = authViewModel.getUserData(otherUserId)
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageModifier = Modifier
                .size(56.dp)
                .clip(CircleShape)

            if (chat.type == "group") {
                Image(
                    imageVector = Icons.Default.Group,
                    contentDescription = "Ícone do Grupo",
                    modifier = imageModifier.padding(8.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
                )
            } else {
                Image(
                    painter = rememberAsyncImagePainter(model = otherUser?.profileImageURL),
                    contentDescription = "Foto de Perfil",
                    modifier = imageModifier,
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .horizontalScroll(rememberScrollState())
                    )
                    if (chat.isPinned.value) {
                        Image(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Conversa fixada",
                            modifier = Modifier
                                .size(16.dp)
                                .padding(start = 6.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
                Text(
                    text = chat.lastMessage?.content ?: "Nenhuma mensagem ainda",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            chat.lastMessage?.timestamp?.let { timestamp ->
                val messageDateTime =
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
                val duration = Duration.between(messageDateTime, now)
                Text(
                    text = when {
                        duration.toDays() > 0 -> "${duration.toDays()}d"
                        duration.toHours() > 0 -> "${duration.toHours()}h"
                        duration.toMinutes() > 0 -> "${duration.toMinutes()}m"
                        else -> "agora"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}