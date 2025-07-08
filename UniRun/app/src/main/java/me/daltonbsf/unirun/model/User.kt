package me.daltonbsf.unirun.model

import java.time.LocalDate

data class User(
    val email: String,
    var name: String,
    var username: String,
    var phone: String? = null,
    var profileImageURL: String? = null,
    val registrationDate: LocalDate,
    var bio: String = "",
    var offeredRidesCount: Int = 0,
    var requestedRidesCount: Int = 0
)

var userList = listOf(
    User("Alice", "Alice", "alice123", "1234567890", "https://i.pinimg.com/736x/19/99/5e/19995e098e2f6f5e029f2c9a79fb62ab.jpg", LocalDate.now(), bio = "Estudante de Engenharia de Software"),
    User("Bob", "Bob", "bob123", "0987654321", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSxEy9UzgJT8kDAPkT0bz8-MxQWKHz1KNRlkQ&s", LocalDate.now()),
    User("Charlie", "Charlie", "charlie123", "1122334455", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSAuUNMtLhNHgbw51Dtfckd_JvggnZkC0fSXg&s", LocalDate.now())
)