package com.example.gimmedamoney.chat

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.util.UUID

class ChatViewModel : ViewModel() {

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

    private val _messages = mutableStateListOf<Message>()
    val messages: List<Message> get() = _messages

    fun sendTextMessage(senderId: String, text: String) {
        if (text.isBlank()) return
        _messages.add(TextMessage(senderID = senderId, text = text))
    }

    fun sendRequestMessage(senderId: String, amount: Double, message: String) {
        _messages.add(
            RequestMessage(
                senderID = senderId,
                amount = amount,
                text = message
            )
        )
    }


    fun declineRequest(requestId: String) {
        val msg = _messages.find { it.id == requestId }
        if (msg != null) {
            _messages.remove(msg)
            _messages.add(
                SystemMessage(
                    text = "declined the request"
                )
            )
        }
    }

}
