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

val caronaList = listOf(
    Carona(
        id = "1",
        creator = "Alice",
        description = "Carona para a aula de Algoritmos",
        creationDate = LocalDateTime.now(),
        departureDate = LocalDateTime.now().plusHours(1),
        origin = "Campus A",
        destiny = "Campus B",
        seatsAvailable = 3
    ),
    Carona(
        id = "2",
        creator = "Bob",
        description = "Carona para a aula de Estruturas de Dados",
        creationDate = LocalDateTime.now(),
        departureDate = LocalDateTime.now().plusHours(2),
        origin = "Campus B",
        destiny = "Campus C",
        seatsAvailable = 2
    )
)