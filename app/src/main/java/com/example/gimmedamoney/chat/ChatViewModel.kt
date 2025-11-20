package com.example.gimmedamoney.chat

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class ChatViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    sealed interface Message {
        val id: String
        val senderID: String
        val text: String
    }

    data class TextMessage(
        override val id: String = UUID.randomUUID().toString(),
        override val senderID: String,
        override val text: String
    ) : Message

    data class RequestMessage(
        override val id: String = UUID.randomUUID().toString(),
        override val senderID: String,
        override val text: String,
        val amount: Double
    ) : Message

    data class SystemMessage(
        override val id: String = UUID.randomUUID().toString(),
        override val senderID: String = "system",
        override val text: String
    ) : Message

    private val db = FirebaseFirestore.getInstance()

    private val groupID: String = checkNotNull(savedStateHandle["groupID"]){
        "ChatViewModel requires groupID in SavedStateHandle"
    }

    private val _messages = mutableStateListOf<Message>()
    val messages: List<Message> get() = _messages

    init {
        db.collection("groups")
            .document(groupID)
            .collection("messages")
            .orderBy("createdAt")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                val docs = snapshot?.documents ?: emptyList()

                val newMessages = docs.mapNotNull { doc ->
                    when (doc.getString("type")) {
                        "TEXT" -> {
                            TextMessage(
                                id = doc.id,
                                senderID = doc.getString("senderId")!!,
                                text = doc.getString("text")!!
                            )
                        }
                        "SYSTEM" -> {
                            SystemMessage(
                                id = doc.id,
                                senderID = doc.getString("senderId")!!,
                                text = doc.getString("text")!!
                            )
                        }
                        else -> null
                    }
                }

                _messages.clear()
                _messages.addAll(newMessages)

            }
    }

    fun sendTextMessage(senderId: String, text: String) {
        if (text.isBlank()) return

        val messagesRef = db.collection("groups")
            .document(groupID)
            .collection("messages")

        val data = mapOf(
            "type" to "TEXT",
            "senderId" to senderId,
            "text" to text,
            "createdAt" to FieldValue.serverTimestamp()
        )

        messagesRef.add(data)
    }

    fun declineRequest(requestId: String) {
        //TODO: Should handle decline, not just delete message
        /*val msg = _messages.find { it.id == requestId }
        if (msg != null) {
            _messages.remove(msg)
            _messages.add(
                SystemMessage(
                    text = "declined the request"
                )
            )
        }
        */
    }

}
