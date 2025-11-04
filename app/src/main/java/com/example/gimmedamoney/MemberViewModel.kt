package com.example.gimmedamoney

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.util.UUID

class MemberViewModel : ViewModel() {

    data class Member(val id: String = UUID.randomUUID().toString(), val name: String)

    private val _members = mutableStateListOf<Member>()
    val members: List<Member> get() = _members;

    fun addPerson(name: String){
        if (name.isBlank()) return
        _members.add(Member(name = name.trim()))
    }

    fun removePerson(id: String) {
        _members.removeAll { it.id == id }
    }
}