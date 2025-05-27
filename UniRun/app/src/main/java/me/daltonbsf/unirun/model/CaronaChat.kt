package me.daltonbsf.unirun.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import java.time.LocalDateTime

data class CaronaChat(
    val creator: String,
    var groupName: String,
    val participants: MutableList<User>,
    override val messages: MutableList<Message> = mutableStateListOf<Message>(),
    override var profileImageURL: String = "",
    override var unread: MutableState<Boolean> = mutableStateOf(true)
): ChatInterface {
    override fun getName(): String {
        return groupName
    }
}

val caronaChatList = listOf(
    CaronaChat(
        "Alice",
        "Carona para a aula de Algoritmos",
        userList as MutableList<User>,
        messages = mutableStateListOf(
            Message(1, "Alice", "Bob", LocalDateTime.now().minusMinutes(10), "Oi Alice! Tudo bem?"),
            Message(2, "Bob", "Alice", LocalDateTime.now().minusMinutes(8), "Oi! Tudo ótimo e você?"),
            Message(3, "Charlie", "Bob", LocalDateTime.now().minusMinutes(5), "Estou bem também, obrigado!aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
        ),
        profileImageURL = "https://i.pinimg.com/736x/19/99/5e/19995e098e2f6f5e029f2c9a79fb62ab.jpg",
    ),
    CaronaChat(
        "Bob",
        "Carona para a aula de Estruturas de Dados",
        userList as MutableList<User>,
        messages = mutableStateListOf(
            Message(4, "Bob", "Charlie", LocalDateTime.now().minusHours(1), "E aí Bob, vai para a aula hoje?"),
            Message(5, "Bob", "Charlie", LocalDateTime.now().minusMinutes(50), "Vou sim! Bora juntos?"),
            Message(6, "Bob", "Charlie", LocalDateTime.now().minusMinutes(45), "Bora! Te encontro na entrada.")
        ),
        profileImageURL = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSxEy9UzgJT8kDAPkT0bz8-MxQWKHz1KNRlkQ&s",
    )
)
