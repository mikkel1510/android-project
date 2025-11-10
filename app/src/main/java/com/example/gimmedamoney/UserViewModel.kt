package com.example.gimmedamoney

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.util.UUID

class UserViewModel : ViewModel() {
    private val retrofitClient = RetrofitClient()

    init {
        fetchUsers()
    }

    data class User(
        val id: String,
        val name: String
    )

    private val _users = mutableStateListOf<User>()
    val users: List<User> get() = _users

    fun addUser(id: String, name: String){
        _users.add(User(id, name))
    }

    fun fetchUsers(){
        viewModelScope.launch {
            val response = retrofitClient.api.getUsers()
            val mapped = response.map { User(it.id, it.name) }
            _users.clear()
            _users.addAll(mapped)
        }
    }
}