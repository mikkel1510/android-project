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
    private val _swapiMembers = mutableStateListOf<SwapiPerson>()
    val members: List<Member> get() = _members

    val swapiMembers: List<SwapiPerson> get() = _swapiMembers

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
            _swapiMembers.clear()
            _swapiMembers.addAll(response.results)
        }
    }
}