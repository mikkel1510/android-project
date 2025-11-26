package com.example.gimmedamoney.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gimmedamoney.data.model.Expense
import com.example.gimmedamoney.data.model.Group
import com.example.gimmedamoney.data.model.User
import com.example.gimmedamoney.data.repository.GroupRepository
import com.example.gimmedamoney.data.sync.SyncType
import com.example.gimmedamoney.data.sync.SyncViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GroupViewModel() : ViewModel() {

    private val groupRepo: GroupRepository = GroupRepository()

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

    fun createGroup(
        name: String,
        imageUri: String? = null,
        creatorID: String,
        syncVM: SyncViewModel
    ) {

        groupRepo.createGroup(
            name = name,
            imageUri = imageUri,
            creatorID = creatorID,
            onLocalWrite = { id ->
                syncVM.reportPending(SyncType.GROUP, id)

                val docRef = groupRepo.groupDocRef(id)
                groupRepo.observeDocumentSync(
                    docRef = docRef,
                    onPending = {},
                    onSynced = {
                        syncVM.reportSynced(SyncType.GROUP, id)
                    },
                    onError = { e ->
                        syncVM.reportError(
                            SyncType.GROUP,
                            id,
                            "Problem syncing group: ${e.message ?: "Unknown error"}"
                        )
                    }
                )
            },
            onError = { e ->
                syncVM.reportError(
                    SyncType.GROUP,
                    id = null,
                    message = "Could not create group: ${e.message ?: "Unknown error"}"
                )
            }
        )
    }

    fun getUserGroups(userID: String){
        groupRepo.listenToUserGroups(
            userID = userID,
            onGroupsUpdated = { newGroups ->
                _groups.value = newGroups
            }
        )
    }

    fun getGroupById(id: String): Group {
        return _groups.value.firstOrNull { it.id == id } ?: Group()
    }

    fun listenToExpenses(groupID: String){
        groupRepo.listenToExpenses(
            groupID = groupID,
            onExpensesUpdated = { expenses ->
                val current = _expensesByGroup.value.toMutableMap()
                current[groupID] = expenses
                _expensesByGroup.value = current
            }
        )
    }

    fun listenToGroup(groupID: String){
        groupRepo.listenToGroup(
            groupID = groupID,
            onGroupUpdated = { updatedGroup ->
                val current = _groups.value.toMutableList()
                val index = current.indexOfFirst { it.id == groupID }
                if (index >= 0){
                    current[index] = updatedGroup
                } else{
                    current.add(updatedGroup)
                }
                _groups.value = current
            }
        )
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

    fun addMembers(groupID: String, userIDs: List<String>, onAdd: () -> Unit){
        groupRepo.addMembers(
            groupID = groupID,
            userIDs = userIDs,
            onAdd = onAdd
        )
    }

    fun removeMember(groupID: String, memberID: String, onDone: () -> Unit) {
        groupRepo.removeMember(
            groupID = groupID,
            memberID = memberID,
            onDone = onDone
        )
    }

    fun getMemberIDsForGroup(groupID: String): List<String> =
        getGroupById(groupID).memberIDs

    fun getMembersForGroup(groupID: String, allUsers: List<User>): List<User>{
        val memberIDs = getMemberIDsForGroup(groupID)
        return allUsers.filter { it.id in memberIDs }
    }

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
        splitBetween: List<String>,
        syncVM: SyncViewModel
    ) {
        groupRepo.addExpense(
            groupId = groupId,
            description = description,
            amount = amount,
            paidBy = paidBy,
            splitBetween = splitBetween,
            onLocalWrite = { expenseId ->
                syncVM.reportPending(SyncType.EXPENSE, expenseId)

                val docRef = groupRepo.expenseDocRef(groupId, expenseId)
                groupRepo.observeDocumentSync(
                    docRef = docRef,
                    onPending = {},
                    onSynced = {
                        syncVM.reportSynced(SyncType.EXPENSE, expenseId)
                    },
                    onError = { e ->
                        syncVM.reportError(
                            SyncType.EXPENSE,
                            expenseId,
                            "Problem syncing expense: ${e.message ?: "Unknown error"}"
                        )
                    }
                )
            },
            onError = { e ->
                syncVM.reportError(
                    SyncType.EXPENSE,
                    id = null,
                    message = "Could not add expense: ${e.message ?: "Unknown error"}"
                )
            }
        )
    }


    // adds the user, who pressed pay in request, to the acceptedBy list in Firestore
    // and removes the user from declinedBy list
    fun markExpensePaid(groupId: String, expenseId: String, userId: String) {
        groupRepo.markExpensePaid(groupId, expenseId, userId)
    }

    // adds the user, who pressed declined in request, to the declinedBy list in Firestore
    // and removes the user from acceptedBY list
    fun markExpenseDeclined(groupId: String, expenseId: String, userId: String) {
        groupRepo.markExpenseDeclined(groupId, expenseId, userId)
    }



    /**
     *
     * Balance calculation
     *
     */

    fun calculateBalances(groupID: String): Map<String, Double> {
        val memberIDs = getMemberIDsForGroup(groupID)

        // Starts all members at 0
        val balances = memberIDs.associateWith {0.0}.toMutableMap()
        val expenses = expensesByGroup.value[groupID] ?: emptyList()
        for (expense in expenses) {
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
    fun getBalanceForUserInGroup(groupID: String, userID: String): Double {
        val balances = calculateBalances(groupID)
        return balances[userID] ?: 0.0
    }

    fun getYouOweAndYouAreOwed(groupID: String, userID: String): Pair<Double, Double> {
        val balance = getBalanceForUserInGroup(groupID, userID)

        return if (balance >= 0) {
            // You are owed money
            0.0 to balance      // (youOwe, youAreOwed)
        } else {
            // You owe money
            (-balance) to 0.0
        }
    }
    fun getTotalForGroup(groupID: String): Double {
        val expenses = expensesByGroup.value[groupID] ?: emptyList()
        return expenses.sumOf { it.amount }
    }


}