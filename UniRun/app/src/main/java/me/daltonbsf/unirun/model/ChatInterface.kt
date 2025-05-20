package me.daltonbsf.unirun.model

import java.net.URL

interface ChatInterface {
    val messages: MutableList<Message>
    var profileImageURL: URL?

    fun getLastMessage(): Message? {
        return messages.lastOrNull()
    }
}