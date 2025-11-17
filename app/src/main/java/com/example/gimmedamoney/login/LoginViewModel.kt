package com.example.gimmedamoney.login

import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    fun login(username: String, password: String): Boolean {
        // Hardcoded credentials for simplicity
        return username == "user" && password == "password"
    }
}
