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
            // Cria uma referência de documento com um ID gerado automaticamente
            val newCaronaRef = caronasCollection.document()
            // Atribui o ID gerado ao objeto carona
            carona.id = newCaronaRef.id

            // Salva a carona no Firestore
            newCaronaRef.set(carona).await()

            // Atualiza a contagem de caronas oferecidas pelo usuário
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
                .orderBy("departureDate", Query.Direction.ASCENDING)
                .get()
                .await()
            snapshot.toObjects(Carona::class.java)
        } catch (e: Exception) {
            Log.e("CaronaRepository", "Erro ao buscar caronas", e)
            emptyList()
        }
    }
}