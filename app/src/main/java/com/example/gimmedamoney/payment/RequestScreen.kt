package com.example.gimmedamoney.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gimmedamoney.UserViewModel
import com.example.gimmedamoney.ui.theme.PrimaryButton
import com.example.gimmedamoney.UserViewModel.User
import com.example.gimmedamoney.ui.theme.GimmeDaMoneyTheme
import com.example.gimmedamoney.ui.theme.TopNavBar
import com.example.gimmedamoney.GroupViewModel
import com.example.gimmedamoney.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestScreen(
    onBackPress: () -> Unit,
    groupVM: GroupViewModel,
    groupID: String,
    userVM: UserViewModel
) {
    val userID by userVM.currentUser.collectAsState()
    var amount by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedMembers by remember { mutableStateOf(setOf<String>()) }

    Scaffold(
        topBar = {
            TopNavBar(
                title = "Create Request",
                subtitle = "Group: $groupID",
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Select Members:", style = MaterialTheme.typography.titleMedium)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 250.dp)
            ) {
                GroupList(
                    members = groupVM.getMembersForGroup(groupID, userVM.users.value).filter { it.id != userID },
                    selected = selectedMembers,
                    onToggleMember = { memberId ->
                        selectedMembers =
                            if (memberId in selectedMembers) selectedMembers - memberId
                            else selectedMembers + memberId
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            OutlinedTextField(
                value = amount,
                onValueChange = { input -> amount = input.filter { it.isDigit() } },
                label = { Text("Amount") },
                prefix = { Text("$") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            PrimaryButton(
                text = "Send Request",
                onClick = {

                    userID?.let { id ->
                        val amountValue = amount.toDoubleOrNull() ?: return@PrimaryButton
                        if (selectedMembers.isEmpty()) return@PrimaryButton

                        groupVM.addExpense(
                            groupId = groupID,
                            description = message.ifBlank { "No description" },
                            amount = amountValue,
                            paidBy = id,
                            splitBetween = selectedMembers.toList()
                        )

                        onBackPress()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun GroupList(
    members: List<User>,
    selected: Set<String>,
    onToggleMember: (String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        items(members) { member ->
            GroupBar(
                member = member,
                isSelected = member.id in selected,
                onClick = { onToggleMember(member.id) }
            )
        }
    }
}

@Composable
fun GroupBar(
    member: User,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor =
        if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 2.dp, color = borderColor, shape = MaterialTheme.shapes.small)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (member.profilePictureURL.isNotBlank()){
            AsyncImage(
                model = member.profilePictureURL,
                contentDescription = "User icon",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                error = painterResource(id = R.drawable.user_icon)
            )
        } else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "User icon",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
        }


        Spacer(modifier = Modifier.width(10.dp))
        Text(member.name)
    }
}

@Preview(showBackground = true)
@Composable
fun RequestScreenPreview() {
    val groupVM: GroupViewModel = viewModel()
    val userVM: UserViewModel = viewModel()

    val fakeGroupId = "group123"

    GimmeDaMoneyTheme {
        RequestScreen(
            onBackPress = {},
            groupVM = groupVM,
            groupID = fakeGroupId,
            userVM
        )
    }
}
