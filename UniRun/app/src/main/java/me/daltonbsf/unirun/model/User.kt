package me.daltonbsf.unirun.model

import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.serialization.Serializable

@IgnoreExtraProperties
@Serializable
data class User(
    val uid: String = "",
    var email: String? = null,
    var name: String = "",
    var username: String = "",
    var phone: String = "",
    var profileImageURL: String? = null,
    var bio: String = "",
    val registrationDate: String? = null,
    var offeredRidesCount: Int = 0,
    var requestedRidesCount: Int = 0
)