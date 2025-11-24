package com.example.gimmedamoney.data.repository

import android.util.Log
import com.example.gimmedamoney.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects

class UserRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun listenToUsers(onUsersUpdated: (List<User>) -> Unit){
        try {
            db.collection("users")
                .addSnapshotListener { value, error ->
                    if (error != null){
                        return@addSnapshotListener
                    }

                    if (value != null){
                        onUsersUpdated(value.toObjects<User>())
                    }
                }
        } catch (e: Exception) {
            println("Failed to fetch users: ${e.message}")
        }
    }

    fun createUser(user: User, onSuccess: (String) -> Unit){
        db.collection("users")
            .add(user)
            .addOnSuccessListener { docRef ->
                Log.d("UserVM", "User created with id: ${docRef.id}")
                onSuccess(docRef.id)
            }
            .addOnFailureListener { e ->
                Log.e("UserVM", "Error creating user", e)
            }
    }

    fun deleteUser(userID: String, onSuccess: () -> Unit){
        db.collection("users")
            .document(userID)
            .delete()
            .addOnSuccessListener {
                Log.d("UserVM", "Successfully deleted account $userID")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("UserVM", "Failed deleting user", e)
            }
    }
}