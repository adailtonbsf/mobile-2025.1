package me.daltonbsf.unirun.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MarkUnreadChatAlt
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
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import me.daltonbsf.unirun.R
import me.daltonbsf.unirun.models.CaronaChat
import me.daltonbsf.unirun.models.ChatInterface
import me.daltonbsf.unirun.models.UserChat
import java.time.Duration
import java.time.LocalDateTime

@Composable
fun ChatCard(chat: ChatInterface) {
    var now by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            delay(60000)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = chat.profileImageURL,
            contentDescription = "Profile Image",
            placeholder = painterResource(R.drawable.placeholder),
            error = painterResource(R.drawable.error),
            modifier = Modifier
                .size(64.dp)
        )

        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
            val lastMessage = chat.getLastMessage()
            Row {
                var title = ""
                when (chat) {
                    is UserChat -> {
                        title = chat.getName()
                    }
                    is CaronaChat -> {
                        title = chat.groupName
                    }
                }
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if(chat.unread.value) {
                    Image(
                        imageVector = Icons.Default.MarkUnreadChatAlt,
                        contentDescription = "Unread Message",
                        modifier = Modifier
                            .size(24.dp)
                            .padding(4.dp)
                    )
                }
                lastMessage?.date?.let {
                    val duration = Duration.between(it, now)
                    Text(
                        text = when {
                            duration.toDays() > 0 -> "${duration.toDays()}d atrás"
                            duration.toHours() > 0 -> "${duration.toHours()}h atrás"
                            duration.toMinutes() > 0 -> "${duration.toMinutes()}m atrás"
                            else -> "agora mesmo"
                        }
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = lastMessage?.content ?: "Nenhuma mensagem ainda",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (chat is UserChat && chat.isPinned.value) {
                    Image(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Conversa fixada",
                        modifier = Modifier
                            .size(20.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}