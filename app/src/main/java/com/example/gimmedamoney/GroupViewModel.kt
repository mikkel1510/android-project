package com.example.gimmedamoney

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.util.UUID
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GroupViewModel() : ViewModel() {

    /**
     *
     * Data models
     * Group
     * Expenses
     *
     */
    data class Group(
        @DocumentId val id: String = "",
        val name: String = "",
        val imageUri: String? = null,
        val creatorID: String = "",
        val memberIDs: List<String> = emptyList(),
    )

    data class Expense(
        val id: String = UUID.randomUUID().toString(),
        val description: String,
        val amount: Double,
        val paidBy: String,
        val splitBetween: List<String>
    )
    private val db = Firebase.firestore

    /**
     *
     * Internal state
     *
     */

    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups = _groups.asStateFlow();



    private val _groupSummaries = mutableStateListOf<GroupSummary>()
    val groupSummaries: List<GroupSummary> get() = _groupSummaries


    /**
     *
     * Group management
     *
     */

    /*
    fun addGroupSummary(g: GroupSummary) = _groupSummaries.add(g)
    fun clear() = _groups.clear()
     */

    /*
    fun addGroup(name: String, imageUri: String? = null): Group? {
        if (name.isBlank()) return null
        val newGroup = Group(name = name.trim(), imageUri = imageUri)
        _groups.add(newGroup)
        return newGroup
    }
     */

    fun createGroup(name: String, imageUri: String? = null, creatorID: String){
        val group = Group(
            name = name,
            imageUri = imageUri,
            creatorID = creatorID,
            memberIDs = listOf(creatorID)
        )
        db.collection("groups")
            .add(group)
            .addOnSuccessListener { docRef ->
                Log.d("GroupVM", "Group added with id ${docRef.id}")
            }
            .addOnFailureListener { e ->
                Log.e("GroupVM", "Error adding group", e)
            }

    }

    fun getUserGroups(userID: String){
        db.collection("groups")
            .whereArrayContains("memberIDs", userID)
            .addSnapshotListener { value, error ->
                if (error != null){
                    return@addSnapshotListener
                }

                if (value != null){
                    _groups.value = value.toObjects<Group>()
                }
            }
    }

    /*
    fun addGroup(group: Group){
        _groups.add(group)
    }
    fun removeGroup(id: String) {
        _groups.removeAll {it.id == id}
    }
    fun getGroupById(id: String): Group? {
        return _groups.find { it.id == id}
    }

     */
    /*
    fun updateGroupImage(groupId: String, newUri: String?) {
        getGroupById(groupId)?.imageUri = newUri
    }

     */

    /**
     *
     * Member management
     *
     */

    /*
    fun addMemberToGroup(groupId: String, user: User) {
        val group = getGroupById(groupId) ?: return
        if (!group.members.contains(user)) {
            group.members.add(user)
        }
    }

     */

    /*
    fun addMembersToGroup(groupId: String, users: List<User>) {
        users.forEach { addMemberToGroup(groupId, it) }
    }

     */

    /*
    fun removeMemberFromGroup(groupId: String, userId: String) {
        getGroupById(groupId)?.members?.removeAll { it.id == userId }
    }

     */

    /**
     *
     * Expense management
     *
     */

    /*
    fun addExpense(
        groupId: String,
        description: String,
        amount: Double,
        paidBy: String,
        splitBetween: List<String>
    ) {
        val group = getGroupById(groupId)

        val expense = Expense(
            //Kotlin giver os en Id
            description = description,
            amount = amount,
            paidBy = paidBy,
            splitBetween = splitBetween
        )
        group?.expenses?.add(expense)
    }

     */


    /**
     *
     * Balance calculation
     *
     */
    /*
    fun calculateBalances(group: Group): Map<String, Double> {
        // Starts all members at 0
        val balances = group.members.associate { it.id to 0.0 }.toMutableMap()

        for (expense in group.expenses) {
            val payer = expense.paidBy
            val participants = expense.splitBetween
            val share = expense.amount / participants.size

            for (member in participants) {

                if (member == payer) { // Payer is also paying

                    balances[payer] = balances[payer]!! + (expense.amount - share)

                } else { // Payer is only owed

                    // member owes their share
                    balances[member] = balances[member]!! - share

                    // payer is owed that share
                    balances[payer] = balances[payer]!! + share
                }
            }
        }
        return balances
    }

     */


}