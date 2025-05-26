package me.daltonbsf.unirun.model

import java.time.LocalDateTime

data class Message(
    val id: Int,
    val sender: String,
    val receiver: String,
    val date: LocalDateTime,
    var content: String
)
