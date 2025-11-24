package com.example.gimmedamoney.data.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Group(
    @DocumentId val id: String = "",
    val name: String = "",
    val imageUri: String? = null,
    val creatorID: String = "",
    val memberIDs: List<String> = emptyList(),
    val creationDate: Date = Date()
)