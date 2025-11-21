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
import com.example.gimmedamoney.UserViewModel.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import java.util.Date

class GroupViewModel() : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
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
        val creationDate: Date = Date()
    )
    data class Expense( //TODO: Should also have timestamp
        @DocumentId val id: String = "",
        val description: String = "",
        val amount: Double = 0.0,
        val paidBy: String = "",
        val splitBetween: List<String> = emptyList()
    )


    /**
     *
     * Internal state
     *
     */

    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups = _groups.asStateFlow();

    private val _expensesByGroup = MutableStateFlow<Map<String, List<Expense>>>(emptyMap())
    val expensesByGroup: StateFlow<Map<String, List<Expense>>> = _expensesByGroup

    /**
     *
     * Group management
     *
     */

    fun createGroup(name: String, imageUri: String? = null, creatorID: String, onResult: (String?) -> Unit){
        val group = Group(
            name = name,
            imageUri = imageUri,
            creatorID = creatorID,
            memberIDs = listOf(creatorID),
            creationDate = Date()
        )
        db.collection("groups")
            .add(group)
            .addOnSuccessListener { docRef ->
                Log.d("GroupVM", "Group added with id ${docRef.id}")
                onResult(docRef.id)
            }
            .addOnFailureListener { e ->
                Log.e("GroupVM", "Error adding group", e)
                onResult(null)
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

    fun getGroupById(id: String): Group {
        return _groups.value.firstOrNull { it.id == id } ?: Group()
    }

    fun startListeningToExpenses(groupID: String){
        db.collection("groups")
            .document(groupID)
            .collection("expenses")
            .addSnapshotListener { snapshot, e ->
                if (e != null){
                    Log.e("GroupVM", "Error listening to expenses", e)
                    return@addSnapshotListener
                }
                val expenses = snapshot?.toObjects(Expense::class.java).orEmpty()
                val current = _expensesByGroup.value.toMutableMap()
                current[groupID] = expenses
                _expensesByGroup.value = current
            }
    }


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

    fun addMembers(groupID: String, userIDs: List<String>){
        val groupRef = db.collection("groups").document(groupID)

        groupRef.update(
            "memberIDs",
            FieldValue.arrayUnion(*userIDs.toTypedArray())
        )
            .addOnSuccessListener {
                Log.d("GroupVM", "Added ${userIDs.size} members")
            }
            .addOnFailureListener { e ->
                Log.e("GroupVM", "Error adding members", e)
            }
    }

    fun removeMember(groupID: String, id: String) {}

    fun getMemberIDsForGroup(groupID: String): List<String> =
        getGroupById(groupID).memberIDs

    fun getMembersForGroup(groupID: String, allUsers: List<User>): List<User>{
        val memberIDs = getMemberIDsForGroup(groupID)
        return allUsers.filter { it.id in memberIDs }
    }

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

    fun addExpense(
        groupId: String,
        description: String,
        amount: Double,
        paidBy: String,
        splitBetween: List<String>
    ) {
        val expenseRef = db.collection("groups")
            .document(groupId)
            .collection("expenses")

        val docRef = expenseRef.document()

        val expense = Expense(
            id = docRef.id,
            description = description,
            amount = amount,
            paidBy = paidBy,
            splitBetween = splitBetween
        )

        docRef.set(expense)
            .addOnSuccessListener {
                Log.d("GroupVM", "Expense added with id ${docRef.id}")
            }
            .addOnFailureListener { e ->
                Log.e("GroupVM", "Error adding expense", e)
            }
    }


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