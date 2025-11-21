package com.example.gimmedamoney

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.Date

class MemberViewModel(val groupId: String) : ViewModel() {

    val creationDate: Date = Date()

    private val _members = mutableStateListOf<UserViewModel.User>()
    val members: List<UserViewModel.User> get() = _members

    fun addMember(user: UserViewModel.User){
        if (!_members.contains(user)){
            _members.add(user)
        }
    }

    fun addMembers(members: List<UserViewModel.User>){
        members.forEach { user ->
            addMember(user)
        }
    }

    fun removeMember(user: UserViewModel.User) {
        _members.remove(user)
    }
}

class MemberViewModelFactory(private val groupId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MemberViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MemberViewModel(groupId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}