package com.example.gimmedamoney.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gimmedamoney.GroupViewModel
import com.example.gimmedamoney.UserViewModel
import com.example.gimmedamoney.ui.theme.PrimaryButton
import com.example.gimmedamoney.ui.theme.TopNavBar
import com.example.gimmedamoney.chat.ChatViewModel.RequestMessage

@Composable
fun GroupChatScreen(
    onBack: () -> Unit = {},
    onInfo: () -> Unit = {},
    onRequest: () -> Unit,
    chatVM: ChatViewModel,
    groupID: String,
    groupVM: GroupViewModel,
    userVM: UserViewModel
) {
    var input by remember { mutableStateOf("") }

    // load current user and all users (with name instead of id)
    val currentUserId by userVM.currentUser.collectAsState()
    val users by userVM.users.collectAsState()
    val userNameById = remember(users) {
        users.associate { it.id to it.name }
    }

    // listens to expenses in the group from Firestore
    LaunchedEffect(groupID) {
        groupVM.listenToExpenses(groupID)
    }
    val expensesByGroup by groupVM.expensesByGroup.collectAsState()
    val expenses = expensesByGroup[groupID].orEmpty()


    // from expenses to display request message in the chat
    val requestMessages = expenses.map { expense ->
        RequestMessage(
            id = expense.id,
            senderID = expense.paidBy,
            text = expense.description,
            amount = expense.amount,
            acceptedBy = expense.acceptedBy,
            declinedBy = expense.declinedBy,
            splitBetween = expense.splitBetween,
            timestamp = expense.createdAt
        )
    }

    // load messages in correct order (sorted by timestamp)
    val allMessages = (requestMessages + chatVM.messages).sortedBy { it.timestamp }

    val safeUserId = currentUserId ?: ""

    Scaffold(
        topBar = {
            TopNavBar(
                title = groupVM.getGroupById(groupID).name,
                subtitle = "Members: ${groupVM.getGroupById(groupID).memberIDs.size}",
                actions = {
                    IconButton(onClick = onInfo) {
                        Icon(Icons.Filled.Info, contentDescription = "Group info")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomBar(
                input = input,
                onInputChange = { input = it },
                onSend = {
                    if (safeUserId.isNotEmpty() && input.isNotBlank()) {
                        chatVM.sendTextMessage(senderId = safeUserId, text = input)
                        input = ""
                    }
                },
                onRequest = onRequest
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(8.dp),
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {

                // if message is from current user = displays "You"
                // else displays the name of the sender id
                items(allMessages) { msg ->
                    val isMe = msg.senderID == safeUserId
                    val senderName = if (isMe) {
                        "You"
                    } else {
                        userNameById[msg.senderID] ?: msg.senderID
                    }

                    MessageItem(
                        message = msg,
                        isMe = isMe,
                        senderName = senderName,
                        currentUserId = safeUserId,
                        groupId = groupID,
                        groupVM = groupVM,
                        userNameById = userNameById
                    )
                }
            }
        }
    }
}

@Composable
fun MessageItem(
    message: ChatViewModel.Message,
    isMe: Boolean,
    senderName: String,
    currentUserId: String,
    groupId: String,
    groupVM: GroupViewModel,
    userNameById: Map<String, String>
) {
    when (message) {
        is ChatViewModel.TextMessage ->
            TextMessageBubble(message, isMe = isMe, senderName = senderName)

        is RequestMessage ->
            RequestMessageCard(
                message = message,
                isMe = isMe,
                senderName = senderName,
                currentUserId = currentUserId,
                groupId = groupId,
                groupVM = groupVM,
                userNameById = userNameById
            )

        is ChatViewModel.SystemMessage ->
            SystemMessageBubble(message)
    }
}

@Composable
fun TextMessageBubble(
    message: ChatViewModel.TextMessage,
    isMe: Boolean,
    senderName: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),

        // aligns the messages based on the sender of message
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Text(
            text = senderName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(2.dp))

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 1.dp
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun RequestMessageCard(
    message: RequestMessage,
    isMe: Boolean,
    senderName: String,
    currentUserId: String,
    groupId: String,
    groupVM: GroupViewModel,
    userNameById: Map<String, String>
) {
    val hasPaid = message.acceptedBy.contains(currentUserId)
    val hasDeclined = message.declinedBy.contains(currentUserId)

    // the members if the groups
    val groupMemberIds = groupVM.getGroupById(groupId).memberIDs

    // who the request is for
    val recipientIds = message.splitBetween

    // is everyone in the group choosen?
    val isEveryone = recipientIds.toSet() == groupMemberIds.toSet()

    // display if request is to everyone or specific members
    val recipientsText = if (isEveryone) {
        "Everyone"
    } else {
        recipientIds.joinToString(", ") { id -> userNameById[id] ?: "Unknown" }
    }

    // members that had paid or declined (later displaying for everyone)
    val paidNames = message.acceptedBy
        .mapNotNull { userNameById[it] }
        .distinct()

    val declinedNames = message.declinedBy
        .mapNotNull { userNameById[it] }
        .distinct()


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
    ) {
        Text(
            text = "Request",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "From $senderName to $recipientsText",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(4.dp))

            if (message.text.isNotBlank()) {
                Text(
                    text = "\"${message.text}\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${message.amount.toInt()} DKK",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(12.dp))

            // if current user sends the request = don't show pay/decline buttons
            // else the user id get stored in either acceptedBy or declinedBy (removed the id from the opposite list)
            if (isMe) {
                Text(
                    text = "Status",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            } else {
                when {
                    hasPaid -> {
                        Text(
                            text = "You have paid this request",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    hasDeclined -> {
                        Text(
                            text = "You declined this request",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    else -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    groupVM.markExpensePaid(
                                        groupId = groupId,
                                        expenseId = message.id,
                                        userId = currentUserId
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Pay")
                            }

                            Button(
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    groupVM.markExpenseDeclined(
                                        groupId = groupId,
                                        expenseId = message.id,
                                        userId = currentUserId
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Decline")
                            }
                        }
                        Spacer(Modifier.height(8.dp))

                    }
                }
            }

            // fetching the acceptedBy and declinedBy list from Firestore, to display for everyone in the group
            if (paidNames.isNotEmpty() || declinedNames.isNotEmpty()) {
                Divider(Modifier.padding(vertical = 4.dp))

                if (paidNames.isNotEmpty()) {
                    Text(
                        text = "Paid by: ${paidNames.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (declinedNames.isNotEmpty()) {
                    Text(
                        text = "Declined by: ${declinedNames.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}


@Composable
fun SystemMessageBubble(message: ChatViewModel.SystemMessage) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Surface(
            color = MaterialTheme.colorScheme.tertiary,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun BottomBar(
    input: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    onRequest: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .imePadding(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            PrimaryButton(
                text = "Request",
                onClick = onRequest,
                modifier = Modifier.weight(1f)
            )
            PrimaryButton(
                text = "Pay all",
                onClick = { /* TODO */ },
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = onInputChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground
                ),
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onSend) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send",
                    Modifier.size(30.dp)
                )
            }
        }
    }
}
