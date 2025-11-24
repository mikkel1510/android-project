package com.example.gimmedamoney.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.gimmedamoney.R
import com.example.gimmedamoney.data.model.User
import com.example.gimmedamoney.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserViewModel(application: Application) : AndroidViewModel(application) {


    private val userRepo: UserRepository = UserRepository()
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users = _users.asStateFlow()

    private var _currentUser = MutableStateFlow<String?>(null)
    val currentUser = _currentUser.asStateFlow()

    init {
        userRepo.listenToUsers(
            onUsersUpdated = { fetchedUsers ->
                _users.value = fetchedUsers
            }
        )
    }

    fun createUser(name: String, phone: String, email: String, password: String): Boolean {
        val emailTaken = users.value.any { it.email.equals(email, ignoreCase = true) }
        val phoneTaken = users.value.any { it.phone == phone }

        if (emailTaken || phoneTaken) return false

        val user = User(
            name = name,
            phone = phone,
            email = email,
            password = password
        )

        userRepo.createUser(
            user = user,
            onSuccess = { newUserId ->
                _currentUser.value = newUserId
            }
        )
        return true
    }

    fun deleteUser(userID: String){
        userRepo.deleteUser(
            userID = userID,
            onSuccess = {
                logOut()
            }
        )
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