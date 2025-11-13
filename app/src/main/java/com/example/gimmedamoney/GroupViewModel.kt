package com.example.gimmedamoney

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.util.UUID

class GroupViewModel : ViewModel() {
    data class Group(
        val id: String = UUID.randomUUID().toString(),
        val name: String,
        val imageUri: String? = null
    )
    private val _groups = mutableStateListOf<Group>()
    val groups: List<Group> get() = _groups;

    fun addGroup(name: String, imageUri: String? = null): Group? {
        if (name.isBlank()) return null
        val newGroup = Group(name = name.trim(), imageUri = imageUri)
        _groups.add(newGroup)
        return newGroup
    }
    fun removeGroup(id: String) {
        _groups.removeAll {it.id == id}
    }
    fun getGroupById(id: String): Group? {
        return _groups.find { it.id == id}
    }
}