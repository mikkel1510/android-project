package com.example.gimmedamoney.data.repository

import com.example.gimmedamoney.data.model.Message
import com.example.gimmedamoney.data.model.RequestMessage
import com.example.gimmedamoney.data.model.SystemMessage
import com.example.gimmedamoney.data.model.TextMessage
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges

class ChatRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun listenToMessages(
        groupID: String,
        onResult: (List<Message>) -> Unit,
    ){
        db.collection("groups")
            .document(groupID)
            .collection("messages")
            .orderBy("createdAt")  // correct sorting in Firestore
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                val docs = snapshot?.documents ?: emptyList()

                val newMessages = docs.mapNotNull { doc ->
                    val timestamp = doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L

                    when (doc.getString("type")) {
                        "TEXT" -> TextMessage(
                            id = doc.id,
                            senderID = doc.getString("senderId")!!,
                            text = doc.getString("text")!!,
                            timestamp = timestamp
                        )

                        "REQUEST" -> RequestMessage(
                            id = doc.id,
                            senderID = doc.getString("senderId")!!,
                            text = doc.getString("text")!!,
                            amount = doc.getDouble("amount") ?: 0.0,
                            timestamp = timestamp
                        )

                        "SYSTEM" -> SystemMessage(
                            id = doc.id,
                            senderID = doc.getString("senderId") ?: "system",
                            text = doc.getString("text")!!,
                            timestamp = timestamp
                        )

                        else -> null
                    }
                }

               onResult(newMessages)
            }
    }

    fun sendTextMessage(
        groupID: String,
        senderId: String,
        text: String,
        onError: (Throwable) -> Unit
    ) {
        val messagesRef = db.collection("groups")
            .document(groupID)
            .collection("messages")

        val docRef = messagesRef.document()

        val data = mapOf(
            "type" to "TEXT",
            "senderId" to senderId,
            "text" to text,
            "createdAt" to FieldValue.serverTimestamp()
        )

        docRef.set(data)
            .addOnFailureListener { e -> onError(e) }
    }

}