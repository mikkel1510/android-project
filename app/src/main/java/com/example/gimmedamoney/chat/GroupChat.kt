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
import com.example.gimmedamoney.ui.theme.PrimaryButton
import com.example.gimmedamoney.ui.theme.TopNavBar

@Composable
fun GroupChatScreen(
    groupName: String,
    memberCount: Int,
    onBack: () -> Unit = {},
    onInfo: () -> Unit = {},
    onRequest: () -> Unit,
    chatVM: ChatViewModel
) {
    var input by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopNavBar(
                title = groupName,
                subtitle = "Members: $memberCount",
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
                    chatVM.sendTextMessage(senderId = "me", text = input)
                    input = ""
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
                items(chatVM.messages) { msg ->
                    MessageItem(
                        message = msg,
                        currentUserId = "me",
                        chatVM = chatVM
                    )
                }
            }
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


//message and request message ui
@Composable
fun MessageItem(
    message: ChatViewModel.Message,
    currentUserId: String,
    chatVM: ChatViewModel
) {
    when (message) {
        is ChatViewModel.TextMessage ->
            TextMessageBubble(message, isMe = message.senderID == currentUserId)
        is ChatViewModel.RequestMessage ->
            RequestMessageCard(message, isMe = message.senderID == currentUserId, chatVM = chatVM)
        is ChatViewModel.SystemMessage ->
            SystemMessageBubble(message)
    }
}

@Composable
fun TextMessageBubble(
    message: ChatViewModel.TextMessage,
    isMe: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Text(
            text = if (isMe) "You" else message.senderID,
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
    message: ChatViewModel.RequestMessage,
    isMe: Boolean,
    chatVM: ChatViewModel
) {
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
                text = "From ${if (isMe) "You" else message.senderID} to Everyone",
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { /* TODO: Pay */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Pay")
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { chatVM.declineRequest(message.id) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Decline")
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
