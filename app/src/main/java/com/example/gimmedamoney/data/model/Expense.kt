package com.example.gimmedamoney.data.model

import com.google.firebase.firestore.DocumentId

data class Expense(
    @DocumentId val id: String = "",
    val description: String = "",
    val amount: Double = 0.0,
    val paidBy: String = "",
    val splitBetween: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val acceptedBy: List<String> = emptyList(),   // users that pressed "Pay"
    val declinedBy: List<String> = emptyList()    // users that pressed "Decline"
)