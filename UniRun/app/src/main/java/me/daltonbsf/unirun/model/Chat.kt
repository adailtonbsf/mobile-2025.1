package me.daltonbsf.unirun.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Chat(
    @DocumentId val id: String = "",
    val groupName: String? = null,
    val participants: List<String> = emptyList(),
    val type: String = "",
    val lastMessage: Message? = null,
    var isPinned: MutableState<Boolean> = mutableStateOf(false)
)

fun Chat.getName(currentUserId: String): String {
    return if (type == "group") {
        groupName ?: "Grupo"
    } else {
        participants.firstOrNull { it != currentUserId } ?: "Desconhecido"
    }
}

fun Chat.getLastMessageDate(): Date? {
    return this.lastMessage?.timestamp?.let { Date(it) }
}