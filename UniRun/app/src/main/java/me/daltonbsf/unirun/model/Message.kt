package me.daltonbsf.unirun.model

import java.time.LocalDateTime

data class Message(
    val id: Int,
    val senderId: Int,
    val receiverId: Int,
    val date: LocalDateTime,
    var content: String
)
