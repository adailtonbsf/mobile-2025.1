package me.daltonbsf.unirun.model

import java.net.URL

data class CaronaChat(
    val creatorId: Int,
    var groupName: String,
    val participants: MutableList<Int>,
    override val messages: MutableList<Message> = mutableListOf(),
    override var profileImageURL: URL? = null
): ChatInterface
