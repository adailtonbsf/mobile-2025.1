package me.daltonbsf.unirun.model

import androidx.compose.runtime.MutableState

interface ChatInterface {
    val messages: MutableList<Message>
    var profileImageURL: String
    var unread: MutableState<Boolean>

    fun getLastMessage(): Message? {
        return messages.lastOrNull()
    }
    fun getName(): String
}