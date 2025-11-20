package com.example.gimmedamoney

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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
        try {
            db.collection("users")
                .addSnapshotListener { value, error ->
                    if (error != null){
                        return@addSnapshotListener
                    }

                    if (value != null){
                        _users.value = value.toObjects<User>()
                    }
                }
        } catch (e: Exception) {
            println("Failed to fetch users: ${e.message}")
        }
    }

    fun logOut(){
        _currentUser.value = null
    }

    fun logIn(email: String, password: String): Boolean{
        val user = _users.value.firstOrNull { it.email == email && it.password == password }
        user?.let {
            _currentUser.value = user.id
        }
        return user != null
    }

    fun createAccount(name: String, phone: String, email: String, password: String): Boolean {
        val emailTaken = users.value.any { it.email.equals(email, ignoreCase = true) }
        val phoneTaken = users.value.any { it.phone == phone }

        if (emailTaken){
            Log.w("UserVM", "Email already in use")
            return false
        }

        if (phoneTaken){
            Log.w("UserVM", "Phone already in use")
            return false
        }

        val user = User(
            name = name,
            phone = phone,
            email = email,
            password = password
        )

        db.collection("users")
            .add(user)
            .addOnSuccessListener { docRef ->
                Log.d("UserVM", "User created with id: ${docRef.id}")
                _currentUser.value = docRef.id
            }
            .addOnFailureListener { e ->
                Log.e("UserVM", "Error creating user", e)
            }
        return true
    }
}