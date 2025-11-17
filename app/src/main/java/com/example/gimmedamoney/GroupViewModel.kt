package com.example.gimmedamoney

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.gimmedamoney.UserViewModel.User
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class GroupViewModel : ViewModel() {
    data class Group(
        val id: String = UUID.randomUUID().toString(),
        val name: String,
        val imageUri: String? = null,
        val members: List<User>? = null
    )

    private val _groups = mutableStateListOf<Group>()
    val groups: List<Group> get() = _groups

    private val _groupSummaries = mutableStateListOf<GroupSummary>()
    val groupSummaries: List<GroupSummary> get() = _groupSummaries


    private val memberViewModels = ConcurrentHashMap<String, MemberViewModel>()

    fun getMemberViewModel(groupId: String): MemberViewModel {
        return memberViewModels.getOrPut(groupId) { MemberViewModel(groupId) }
    }

    fun addGroupSummary(g: GroupSummary) = _groupSummaries.add(g)
    fun clear() = _groups.clear()

    fun addGroup(name: String, imageUri: String? = null): Group? {
        if (name.isBlank()) return null
        val newGroup = Group(name = name.trim(), imageUri = imageUri)
        _groups.add(newGroup)

        _groupSummaries.add(GroupSummary(id = newGroup.id, name = newGroup.name))
        return newGroup
    }

    fun addGroup(group: Group) {
        _groups.add(group)
    }

    fun removeGroup(id: String) {
        _groups.removeAll { it.id == id }
    }

    fun getGroupById(id: String): Group? {
        return _groups.find { it.id == id }
    }
}