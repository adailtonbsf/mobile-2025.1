package me.daltonbsf.unirun.model

data class User(
    val email: String,
    var name: String,
    var username: String,
    var phone: String? = null,
    var profileImageURL: String? = null
)