package com.example.gimmedamoney

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class GroupViewModel : ViewModel() {
    private val _groups = mutableStateListOf<GroupSummary>()
    val groups: List<GroupSummary> get() = _groups

    fun addGroup(g: GroupSummary) = _groups.add(g)
    fun clear() = _groups.clear()

}

