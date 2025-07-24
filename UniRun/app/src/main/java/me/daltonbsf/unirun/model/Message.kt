package me.daltonbsf.unirun.model

data class Message(
    val senderId: String = "",
    val senderName: String = "",
    val timestamp: Long = 0L,
    var content: String = ""
)