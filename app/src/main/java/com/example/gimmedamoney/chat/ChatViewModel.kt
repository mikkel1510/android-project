package com.example.gimmedamoney.chat

import java.util.UUID

class ChatViewModel

    sealed interface Message{
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