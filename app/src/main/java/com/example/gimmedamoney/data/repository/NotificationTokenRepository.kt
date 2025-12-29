package com.example.gimmedamoney.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class NotificationTokenRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun saveUserToken(userId: String, token: String){
        db.collection("users")
            .document(userId)
            .collection("fcmTokens")
            .document(token)
            .set(
                mapOf(
                    "createdAt" to FieldValue.serverTimestamp(),
                    "platform" to "android"
                )
            )
    }
}