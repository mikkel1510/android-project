package com.example.gimmedamoney

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class MemberViewModel() : ViewModel() {

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