package com.example.gimmedamoney

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val retrofitClient = RetrofitClient()

    init {
        fetchUsers()
    }

    data class User(
        val id: String,
        val name: String,
        val email: String,
        val phone: String,
        val profilePictureResId: Int // This will always hold a valid ID
    )

    private val _users = mutableStateListOf<User>()
    val users: List<User> get() = _users

    fun addUser(id: String, name: String, email: String, phone: String){
        _users.add(User(id, name, email, phone, mapIdToProfilePicture(id)))
    }

    fun addUser(user: User){
        _users.add(user)
    }

    fun fetchUsers(){
        viewModelScope.launch {
            val response = retrofitClient.api.getUsers()
            val mapped = response.map { User(it.id, it.name, it.email, it.phone, mapIdToProfilePicture(it.id)) }
            _users.clear()
            _users.addAll(mapped)
            print(_users)
        }
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