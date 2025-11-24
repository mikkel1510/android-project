package com.example.gimmedamoney.data.model

import java.util.UUID

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