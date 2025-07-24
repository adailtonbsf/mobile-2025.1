package me.daltonbsf.unirun.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import me.daltonbsf.unirun.model.User
import me.daltonbsf.unirun.util.SupabaseManager
import java.time.LocalDate

enum class LoginStatus {
    SUCCESS,
    INVALID_CREDENTIALS,
    EMAIL_NOT_VERIFIED
}

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun register(
        email: String, password: String, name: String, username: String, phone: String
    ): Boolean {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid  = result.user?.uid

            if (uid != null) {
                val user = hashMapOf(
                    "uid" to uid,
                    "email" to email,
                    "name" to name,
                    "username" to username,
                    "phone" to phone,
                    "profileImageURL" to "https://zjgmhaovvebivibdvkox.supabase.co/storage/v1/object/public/unirun-profile-pics//default_profile_picture.png",
                    "registrationDate" to LocalDate.now().toString(),
                )
                firestore.collection("users").document(uid).set(user).await()
            } else {
                Log.e("AuthRepository", "User registration failed: UID is null")
                return false
            }
            true
        } catch (e: Exception) {
            Log.e("AuthRepository", "Registration failed: ${e.message}")
            false
        }
    }

    suspend fun login(email: String, password: String): LoginStatus {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            if (result.user?.isEmailVerified == true) {
                LoginStatus.SUCCESS
            } else {
                LoginStatus.EMAIL_NOT_VERIFIED
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login failed: ${e.message}")
            LoginStatus.INVALID_CREDENTIALS
        }
    }

    suspend fun resetPassword(email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            Log.e("AuthRepository", "Password reset failed: ${e.message}")
            false
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    suspend fun getUserName(): String? {
        return try {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                val snapshot = firestore.collection("users").document(uid).get().await()
                snapshot.getString("name")
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to get username: ${e.message}")
            null
        }
    }

    suspend fun getUserUsername(): String? {
        return try {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                val snapshot = firestore.collection("users").document(uid).get().await()
                snapshot.getString("username")
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to get username: ${e.message}")
            null
        }
    }

    suspend fun getUserProfileImage(): String? {
        return try {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                val snapshot = firestore.collection("users").document(uid).get().await()
                snapshot.getString("profileImageURL")
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to get profile image: ${e.message}")
            null
        }
    }

    fun getUserEmail(): String? {
        return try {
            auth.currentUser?.email
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to get user email: ${e.message}")
            null
        }
    }

    suspend fun getUserPhone(): String? {
        return try {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                val snapshot = firestore.collection("users").document(uid).get().await()
                snapshot.getString("phone")
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to get user phone: ${e.message}")
            null
        }
    }

    suspend fun getUserBio(): String? {
        return try {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                val snapshot = firestore.collection("users").document(uid).get().await()
                snapshot.getString("bio")
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to get user bio: ${e.message}")
            null
        }
    }

    suspend fun updateUserProfileImage(imageBytes: ByteArray, fileName: String): Boolean {
        return try {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                val bucket = SupabaseManager.storage["unirun-profile-pics"]
                bucket.upload(fileName, imageBytes)

                val imageUrl = bucket.publicUrl(fileName)

                firestore.collection("users").document(uid).update("profileImageURL", imageUrl).await()
                true
            } else {
                Log.e("AuthRepository", "Update failed: UID is null")
                false
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to update profile image: ${e.message}")
            false
        }
    }

    suspend fun updateUserBio(bio: String): Boolean {
        return try {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                firestore.collection("users").document(uid).update("bio", bio).await()
                true
            } else {
                Log.e("AuthRepository", "Update failed: UID is null")
                false
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to update bio: ${e.message}")
            false
        }
    }

    suspend fun updateUserPhone(phone: String): Boolean {
        return try {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                firestore.collection("users").document(uid).update("phone", phone).await()
                true
            } else {
                Log.e("AuthRepository", "Update failed: UID is null")
                false
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to update phone: ${e.message}")
            false
        }
    }

    suspend fun updateUserName(name: String): Boolean {
        return try {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                firestore.collection("users").document(uid).update("name", name).await()
                true
            } else {
                Log.e("AuthRepository", "Update failed: UID is null")
                false
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to update name: ${e.message}")
            false
        }
    }

    suspend fun updateUserUsername(username: String): Boolean {
        return try {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                firestore.collection("users").document(uid).update("username", username).await()
                true
            } else {
                Log.e("AuthRepository", "Update failed: UID is null")
                false
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to update username: ${e.message}")
            false
        }
    }

    suspend fun getUserRegistrationDate(): String? {
        return try {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                val snapshot = firestore.collection("users").document(uid).get().await()
                snapshot.getString("registrationDate")
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to get registration date: ${e.message}")
            null
        }
    }

    suspend fun getUserOfferedRidesCount(): Int {
        return try {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                val snapshot = firestore.collection("users").document(uid).get().await()
                snapshot.getLong("offeredRidesCount")?.toInt() ?: 0
            } else {
                0
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to get offered rides count: ${e.message}")
            0
        }
    }

    suspend fun getUserRequestedRidesCount(): Int {
        return try {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                val snapshot = firestore.collection("users").document(uid).get().await()
                snapshot.getLong("requestedRidesCount")?.toInt() ?: 0
            } else {
                0
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to get requested rides count: ${e.message}")
            0
        }
    }

    suspend fun updateUserOfferedRidesCount(count: Int): Boolean {
        return try {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                firestore.collection("users").document(uid).update("offeredRidesCount", count).await()
                true
            } else {
                Log.e("AuthRepository", "Update failed: UID is null")
                false
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to update offered rides count: ${e.message}")
            false
        }
    }

    suspend fun updateUserRequestedRidesCount(count: Int): Boolean {
        return try {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                firestore.collection("users").document(uid).update("requestedRidesCount", count).await()
                true
            } else {
                Log.e("AuthRepository", "Update failed: UID is null")
                false
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to update requested rides count: ${e.message}")
            false
        }
    }

    fun logout() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Logout failed: ${e.message}")
        }
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    suspend fun isEmailAlreadyInUse(email: String): Boolean {
        return try {
            val result = auth.fetchSignInMethodsForEmail(email).await()
            result.signInMethods?.isNotEmpty() ?: false
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to check if email is in use: ${e.message}")
            false
        }
    }

    suspend fun isUsernameAlreadyInUse(username: String): Boolean {
        return try {
            val snapshot = firestore.collection("users")
                .whereEqualTo("username", username)
                .get()
                .await()
            !snapshot.isEmpty
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to check if username is in use: ${e.message}")
            false
        }
    }

    fun isUserVerified(): Boolean {
        return try {
            auth.currentUser?.isEmailVerified ?: false
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to check if user is verified: ${e.message}")
            false
        }
    }

    suspend fun sendEmailVerification(): Boolean {
        return try {
            val user = auth.currentUser
            if (user != null) {
                user.sendEmailVerification().await()
                true
            } else {
                Log.e("AuthRepository", "Send email verification failed: User is null")
                false
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to send email verification: ${e.message}")
            false
        }
    }

    suspend fun searchUsers(query: String): List<User> {
        return try {
            if (query.isBlank()) {
                return emptyList()
            }
            val currentUserUid = auth.currentUser?.uid ?: return emptyList()

            val snapshot = firestore.collection("users")
                .orderBy("username")
                .startAt(query)
                .endAt(query + '\uf8ff')
                .limit(10)
                .get()
                .await()

            val users = snapshot.toObjects(User::class.java)
            // Filtra o usuário logado da lista de resultados
            users.filter { it.uid != currentUserUid }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to search users: ${e.message}")
            emptyList()
        }
    }

    suspend fun getUserData(userId: String): User? {
        return try {
            val snapshot = firestore.collection("users").document(userId).get().await()
            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to get user data for $userId: ${e.message}")
            null
        }
    }
}