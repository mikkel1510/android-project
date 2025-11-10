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

    private val _members = mutableStateListOf<UserViewModel.User>()
    val members: List<UserViewModel.User> get() = _members

    init {
        fetchMembers()
    }

    fun addPerson(user: UserViewModel.User){
        _members.add(user)
    }

    fun addMembers(members: List<UserViewModel.User>){
        _members.addAll(members)
    }

    fun removePerson(user: UserViewModel.User) {
        _members.remove(user)
    }

    fun fetchMembers(){
        viewModelScope.launch {
            val response = retrofitClient.api.getUsers()
            val mapped = response.map { UserViewModel.User(it.id, it.name) }
            _members.clear()
            _members.addAll(mapped)
        }
    }
}