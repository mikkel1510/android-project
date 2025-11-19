package com.example.gimmedamoney

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class UserViewModel : ViewModel() {
    data class User(
        @DocumentId val id: String = "",
        val name: String = "",
        val email: String = "",
        val phone: String = "",
        val password: String = ""
    )
    private val db = Firebase.firestore



    init {
        getUsers()
    }

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()

    private var _currentUser = MutableStateFlow<String?>(null)
    val currentUser = _currentUser.asStateFlow()

    fun getUsers(){
        db.collection("users")
            .addSnapshotListener { value, error ->
                if (error != null){
                    return@addSnapshotListener
                }

                if (value != null){
                    _users.value = value.toObjects<User>()
                }
            }
    }

    fun login(email: String, password: String): Boolean{
        val user = _users.value.firstOrNull { it.email == email && it.password == password }
        user?.let {
            _currentUser.value = user.id
        }
        return user != null
    }
}