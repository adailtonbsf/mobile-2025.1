package me.daltonbsf.unirun.models

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import java.time.LocalDateTime

data class UserChat(
    val userName: String,
    override val messages: MutableList<Message> = mutableStateListOf<Message>(),
    override var profileImageURL: String = "",
    override var unread: MutableState<Boolean> = mutableStateOf(true),
    var isPinned: MutableState<Boolean> = mutableStateOf(false)
): ChatInterface {
    override fun getName(): String {
        return userName
    }
}

val aliceMessages = mutableStateListOf<Message>(
    Message(1, "Alice", "Bob", LocalDateTime.now().minusMinutes(10), "Oi Alice! Tudo bem?"),
    Message(2, "Alice", "Bob", LocalDateTime.now().minusMinutes(8), "Oi! Tudo ótimo e você?"),
    Message(3, "Alice", "Bob", LocalDateTime.now().minusMinutes(5), "Estou bem também, obrigado!")
)

val bobMessages = mutableStateListOf(
    Message(4, "Bob", "Charlie", LocalDateTime.now().minusHours(1), "E aí Bob, vai para a aula hoje?"),
    Message(5, "Bob", "Charlie", LocalDateTime.now().minusMinutes(50), "Vou sim! Bora juntos?"),
    Message(6, "Bob", "Charlie", LocalDateTime.now().minusMinutes(45), "Bora! Te encontro na entrada.")
)

val charlieMessages = mutableStateListOf(
    Message(7, "Charlie", "Bob", LocalDateTime.now().minusDays(1), "Charlie, terminou o trabalho?"),
    Message(8, "Charlie", "Bob", LocalDateTime.now().minusHours(20), "Terminei sim, já te enviei por e-mail."),
    Message(9, "Charlie", "Bob", LocalDateTime.now().minusHours(19), "Recebi, obrigado!")
)

var userChatList = mutableStateListOf(
    UserChat(
        "Alice",
        messages = aliceMessages,
        profileImageURL = "https://i.pinimg.com/736x/19/99/5e/19995e098e2f6f5e029f2c9a79fb62ab.jpg"
    ),
    UserChat(
        "Bob",
        messages = bobMessages,
        profileImageURL = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSxEy9UzgJT8kDAPkT0bz8-MxQWKHz1KNRlkQ&s"
    ),
    UserChat(
        "Charlie",
        messages = charlieMessages,
        profileImageURL = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSAuUNMtLhNHgbw51Dtfckd_JvggnZkC0fSXg&s"
    )
)

