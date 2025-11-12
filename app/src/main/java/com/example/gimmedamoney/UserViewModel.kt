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
        val name: String,
        val email: String,
        val phone: String
    )

    private val _users = mutableStateListOf<User>()
    val users: List<User> get() = _users

    fun addUser(id: String, name: String, email: String, phone: String){
        _users.add(User(id, name, email, phone))
    }

    fun addUser(user: User){
        _users.add(user)
    }

    fun fetchUsers(){
        viewModelScope.launch {
            val response = retrofitClient.api.getUsers()
            val mapped = response.map { User(it.id, it.name, it.email, it.phone) }
            _users.clear()
            _users.addAll(mapped)
            print(_users)
        }
    }
}