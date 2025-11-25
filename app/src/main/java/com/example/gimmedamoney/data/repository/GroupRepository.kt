package com.example.gimmedamoney.data.repository

import android.util.Log
import androidx.compose.runtime.snapshotFlow
import com.example.gimmedamoney.data.model.Expense
import com.example.gimmedamoney.data.model.Group
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.toObjects
import java.util.Date

class GroupRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun createGroup(
        name: String,
        imageUri: String? = null,
        creatorID: String,
        onLocalWrite: (String) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val group = Group(
            name = name,
            imageUri = imageUri,
            creatorID = creatorID,
            memberIDs = listOf(creatorID),
            creationDate = Date()
        )

        val docRef = db.collection("groups").document()

        onLocalWrite(docRef.id)

        docRef.set(group)
            .addOnFailureListener { e ->
                onError(e)
            }
    }

    fun observeGroupSync(
        groupID: String,
        onPending: () -> Unit,
        onSynced: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val docRef = db.collection("groups").document(groupID)

        var registration: ListenerRegistration? = null

        registration = docRef.addSnapshotListener(
            MetadataChanges.INCLUDE
        ) { snapshot, e ->
            if (e != null) {
                onError(e)
                registration?.remove()
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val pending = snapshot.metadata.hasPendingWrites()

                if (pending) {
                    onPending()
                } else {
                    onSynced()
                    registration?.remove()
                }
            }
        }
    }


fun listenToUserGroups(
        userID: String,
        onGroupsUpdated: (List<Group>) -> Unit
    ){
        db.collection("groups")
            .whereArrayContains("memberIDs", userID)
            .addSnapshotListener { value, error ->
                if (error != null){
                    return@addSnapshotListener
                }

                if (value != null){
                    val groups = value.toObjects<Group>()
                    onGroupsUpdated(groups)
                } else {
                    onGroupsUpdated(emptyList())
                }
            }
    }

    fun listenToExpenses(
        groupID: String,
        onExpensesUpdated: (List<Expense>) -> Unit
    ){
        db.collection("groups")
            .document(groupID)
            .collection("expenses")
            .addSnapshotListener { snapshot, e ->
                if (e != null){
                    Log.e("GroupVM", "Error listening to expenses", e)
                    return@addSnapshotListener
                }
                val expenses = snapshot?.toObjects(Expense::class.java).orEmpty()
                onExpensesUpdated(expenses)
            }
    }

    fun listenToGroup(
        groupID: String,
        onGroupUpdated: (Group) -> Unit
    ){
        db.collection("groups")
            .document(groupID)
            .addSnapshotListener { snapshot, e ->
                if (e != null){
                    Log.e("GroupVM", "Error listening to group $groupID", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()){
                    val updatedGroup = snapshot.toObject(Group::class.java)?.copy(id = snapshot.id)
                    if (updatedGroup != null){
                        onGroupUpdated(updatedGroup)
                    }
                }
            }
    }

    fun addMembers(
        groupID: String,
        userIDs: List<String>,
        onAdd: () -> Unit
    ){
        val groupRef = db.collection("groups").document(groupID)

        groupRef.update(
            "memberIDs",
            FieldValue.arrayUnion(*userIDs.toTypedArray())
        )
            .addOnSuccessListener {
                Log.d("GroupVM", "Added ${userIDs.size} members")
                onAdd()
            }
            .addOnFailureListener { e ->
                Log.e("GroupVM", "Error adding members", e)
            }
    }

    fun removeMember(
        groupID: String,
        memberID: String,
        onDone: () -> Unit
    ) {
        db.collection("groups")
            .document(groupID)
            .update("memberIDs", FieldValue.arrayRemove(memberID))
            .addOnSuccessListener {
                Log.d("GroupVM", "Member removed")
                onDone()
            }
            .addOnFailureListener { e ->
                Log.w("GroupVM", "Failed removing member", e)
            }
    }

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
            splitBetween = splitBetween,
            createdAt = System.currentTimeMillis()
        )

        docRef.set(expense)
            .addOnSuccessListener {
                Log.d("GroupVM", "Expense added with id ${docRef.id}")
            }
            .addOnFailureListener { e ->
                Log.e("GroupVM", "Error adding expense", e)
            }
    }

    fun markExpensePaid(groupId: String, expenseId: String, userId: String) {
        val expenseRef = db.collection("groups")
            .document(groupId)
            .collection("expenses")
            .document(expenseId)

        expenseRef.update(
            mapOf(
                "acceptedBy" to FieldValue.arrayUnion(userId),
                "declinedBy" to FieldValue.arrayRemove(userId)
            )
        )
    }

    fun markExpenseDeclined(groupId: String, expenseId: String, userId: String) {
        val expenseRef = db.collection("groups")
            .document(groupId)
            .collection("expenses")
            .document(expenseId)

        expenseRef.update(
            mapOf(
                "declinedBy" to FieldValue.arrayUnion(userId),
                "acceptedBy" to FieldValue.arrayRemove(userId)
            )
        )
    }
}