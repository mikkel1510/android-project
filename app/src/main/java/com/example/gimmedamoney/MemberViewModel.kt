package com.example.gimmedamoney

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.UUID

class MemberViewModel() : ViewModel() {
    data class Member(
        val id: String = UUID.randomUUID().toString(),
        val name: String
    )
    private val retrofitClient = RetrofitClient()

    private val _members = mutableStateListOf<Member>()
    val members: List<Member> get() = _members

    init {
        fetchMembers()
    }

    fun addPerson(name: String){
        if (name.isBlank()) return
        _members.add(Member(name = name.trim()))
    }

    fun removePerson(id: String) {
        _members.removeAll { it.id == id }
    }

    fun fetchMembers(){
        viewModelScope.launch {
            val response = retrofitClient.api.getPeople()
            val mapped = response.map { Member(it.id, it.name) }
            _members.clear()
            _members.addAll(mapped)
        }
    }
}