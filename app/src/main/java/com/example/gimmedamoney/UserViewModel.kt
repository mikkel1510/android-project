package com.example.gimmedamoney

import android.util.Log
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.app.Application
import androidx.lifecycle.AndroidViewModel

class UserViewModel(application: Application) : AndroidViewModel(application) {
    data class User(
        @DocumentId val id: String = "",
        val name: String = "",
        val email: String = "",
        val phone: String = "",
        val password: String = "",
        val profilePictureURL: String = ""
    )
    private val db = FirebaseFirestore.getInstance()



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

    fun deleteAccount(userID: String){
        db.collection("users")
            .document(userID)
            .delete()
            .addOnSuccessListener {
                Log.d("UserVM", "Successfully deleted account $userID")
                logOut()
            }
            .addOnFailureListener { e ->
                Log.e("UserVM", "Failed deleting user", e)
            }
    }

    fun logIn(email: String, password: String): Boolean{
        val user = _users.value.firstOrNull { it.email == email && it.password == password }
        user?.let {
            _currentUser.value = user.id
        }
        return user != null
    }
    fun logOut(){
        _currentUser.value = null
    }


    private fun mapIdToProfilePicture(id: String): Int {
        val context = getApplication<Application>().applicationContext
        val drawableName = when (id) {
            "1" -> "luke_skywalker"
            "2" -> "leia_organa"
            "3" -> "han_solo"
            "4" -> "chewbacca"
            "5" -> "obi_wan_kenobi"
            else -> null
        }

        if (drawableName != null) {
            // Safely check for the drawable's existence at runtime
            val resourceId = context.resources.getIdentifier(drawableName, "drawable", context.packageName)
            if (resourceId != 0) {
                return resourceId
            }
        }

        // If no specific drawable is found, return the default icon
        return R.drawable.user_icon
    }
}