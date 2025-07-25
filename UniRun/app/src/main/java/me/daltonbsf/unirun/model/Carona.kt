package me.daltonbsf.unirun.model

data class Carona(
    var id: String = "",
    val creator: String = "",
    val chatId: String = "",
    val information: String = "",
    val departureDate: String = "",
    val origin: String = "",
    val destiny: String = "",
    val originName: String = "",
    val destinyName: String = "",
    val seatsAvailable: Int = 4,
    val participants: List<String> = emptyList(),
    var status: String = "active"
)