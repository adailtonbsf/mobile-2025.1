package me.daltonbsf.unirun.model

import java.time.LocalDateTime

data class Carona(
    val id: String,
    val creator: String,
    val description: String,
    val creationDate: LocalDateTime,
    val departureDate: LocalDateTime,
    val origin: String,
    val destiny: String,
    val seatsAvailable: Int
)