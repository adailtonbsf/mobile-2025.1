package me.daltonbsf.unirun.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import me.daltonbsf.unirun.model.Carona

class CaronaRepository() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val caronasCollection = firestore.collection("caronas")

    suspend fun createCarona(carona: Carona): Boolean {
        return try {
            val newCaronaRef = caronasCollection.document()
            carona.id = newCaronaRef.id

            newCaronaRef.set(carona).await()

            val userId = auth.currentUser?.uid
            if (userId != null) {
                val userDocRef = firestore.collection("users").document(userId)
                userDocRef.update("offeredRidesCount", FieldValue.increment(1)).await()
            }
            true
        } catch (e: Exception) {
            Log.e("CaronaRepository", "Erro ao criar carona", e)
            false
        }
    }

    suspend fun getAllCaronas(): List<Carona> {
        return try {
            val snapshot = caronasCollection
                .whereEqualTo("status", "active")
                .orderBy("departureDate", Query.Direction.ASCENDING)
                .get()
                .await()
            snapshot.toObjects(Carona::class.java)
        } catch (e: Exception) {
            Log.e("CaronaRepository", "Erro ao buscar caronas", e)
            emptyList()
        }
    }

    suspend fun getCaronaById(caronaId: String): Carona? {
        return try {
            val snapshot = caronasCollection.document(caronaId).get().await()
            snapshot.toObject(Carona::class.java)
        } catch (e: Exception) {
            Log.e("CaronaRepository", "Erro ao buscar carona por ID", e)
            null
        }
    }

    suspend fun joinCarona(caronaId: String, userId: String): Boolean {
        val caronaRef = caronasCollection.document(caronaId)
        return try {
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(caronaRef)
                val currentSeats = snapshot.getLong("seatsAvailable")?.toInt() ?: 0
                val participants = snapshot.get("participants") as? List<*>

                if (currentSeats > 0 && participants?.contains(userId) == false) {
                    transaction.update(caronaRef, "seatsAvailable", FieldValue.increment(-1))
                    transaction.update(caronaRef, "participants", FieldValue.arrayUnion(userId))
                    true
                } else {
                    false
                }
            }.await()
        } catch (e: Exception) {
            Log.e("CaronaRepository", "Erro ao entrar na carona", e)
            false
        }
    }

    suspend fun getCaronaByChatId(chatId: String): Carona? {
        return try {
            val snapshot = caronasCollection
                .whereEqualTo("chatId", chatId)
                .limit(1)
                .get()
                .await()
            snapshot.documents.firstOrNull()?.toObject(Carona::class.java)
        } catch (e: Exception) {
            Log.e("CaronaRepository", "Erro ao buscar carona por chatId", e)
            null
        }
    }

    suspend fun leaveCarona(caronaId: String, userId: String): Boolean {
        val caronaRef = caronasCollection.document(caronaId)
        return try {
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(caronaRef)
                val participants = snapshot.get("participants") as? List<*>

                if (participants?.contains(userId) == true) {
                    transaction.update(caronaRef, "seatsAvailable", FieldValue.increment(1))
                    transaction.update(caronaRef, "participants", FieldValue.arrayRemove(userId))
                    true
                } else {
                    false
                }
            }.await()
        } catch (e: Exception) {
            Log.e("CaronaRepository", "Erro ao sair da carona", e)
            false
        }
    }

    suspend fun cancelCarona(caronaId: String): Boolean {
        val caronaRef = caronasCollection.document(caronaId)
        return try {
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(caronaRef)
                val creatorId = snapshot.getString("creator")

                if (creatorId != null) {
                    val userDocRef = firestore.collection("users").document(creatorId)
                    transaction.update(userDocRef, "offeredRidesCount", FieldValue.increment(-1))
                }

                transaction.delete(caronaRef)
                null
            }.await()
            true
        } catch (e: Exception) {
            Log.e("CaronaRepository", "Erro ao cancelar carona", e)
            false
        }
    }

    suspend fun finishPastCaronas() {
        try {
            val now = System.currentTimeMillis().toString()
            val snapshot = caronasCollection
                .whereEqualTo("status", "active")
                .whereLessThan("departureDate", now)
                .get()
                .await()

            val batch = firestore.batch()
            for (document in snapshot.documents) {
                val caronaRef = document.reference
                batch.update(caronaRef, "status", "finished")

                val participants = document.get("participants") as? List<*>
                participants?.forEach { userId ->
                    if (userId is String) {
                        val userRef = firestore.collection("users").document(userId)
                        batch.update(userRef, "requestedRidesCount", FieldValue.increment(1))
                    }
                }
            }
            batch.commit().await()
        } catch (e: Exception) {
            Log.e("CaronaRepository", "Erro ao finalizar caronas passadas", e)
        }
    }
}