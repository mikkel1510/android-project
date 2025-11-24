package com.example.gimmedamoney.chat

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
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
        val timestamp: Long
    }

    data class TextMessage(
        override val id: String = UUID.randomUUID().toString(),
        override val senderID: String,
        override val text: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : Message

    data class RequestMessage(
        override val id: String = UUID.randomUUID().toString(),
        override val senderID: String,
        override val text: String,
        val amount: Double,
        val acceptedBy: List<String> = emptyList(),
        val declinedBy: List<String> = emptyList(),
        val splitBetween: List<String> = emptyList(),
        override val timestamp: Long = System.currentTimeMillis()
    ) : Message


    data class SystemMessage(
        override val id: String = UUID.randomUUID().toString(),
        override val senderID: String = "system",
        override val text: String,
        override val timestamp: Long = System.currentTimeMillis()
    ) : Message

    private val db = FirebaseFirestore.getInstance()

    private val groupID: String = checkNotNull(savedStateHandle["groupID"]) {
        "ChatViewModel requires groupID in SavedStateHandle"
    }

    private val _messages = mutableStateListOf<Message>()
    val messages: List<Message> get() = _messages

    init {
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

    enum class RequestStatus { PENDING, PAID, DECLINED}

    private val _requestStatuses = mutableStateMapOf<String, RequestStatus>()
    val requestStatuses: Map<String, RequestStatus> get() = _requestStatuses

    fun getRequestStatus(requestId: String): RequestStatus = _requestStatuses[requestId] ?: RequestStatus.PENDING

    fun payRequest(requestId: String) {
        _requestStatuses[requestId] = RequestStatus.PAID
        _messages.add(
            SystemMessage(
                text = "You paid request $requestId"
            )
        )
    }

    fun declineRequest(requestId: String) {
        _requestStatuses[requestId] = RequestStatus.DECLINED

        _messages.add(
            SystemMessage(
                text = "You declined request $requestId"
            )
        )
    }
}
