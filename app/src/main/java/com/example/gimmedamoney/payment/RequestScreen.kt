package com.example.gimmedamoney.payment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gimmedamoney.MemberViewModel
import com.example.gimmedamoney.ui.theme.PrimaryButton
import com.example.gimmedamoney.R
import com.example.gimmedamoney.UserViewModel.User
import com.example.gimmedamoney.ui.theme.GimmeDaMoneyTheme
import com.example.gimmedamoney.ui.theme.TopNavBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestScreen(
    onBackPress: () -> Unit,
    membervm: MemberViewModel,
    groupID: String
) {
    var amount by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedMembers by remember { mutableStateOf(setOf<String>()) }

    Scaffold(
        topBar = {
            TopNavBar(
                title = "Create Reqest",
                subtitle = "INSERT GROUP NAME HERE AIPWDNBWAIPNDAWIPDNAWPIUNDPIWAND", //TODO: Use Group ID
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
                    members = membervm.members,
                    selected = selectedMembers,
                    onToggleMember = { memberId ->
                        selectedMembers = if (memberId in selectedMembers) {
                            selectedMembers - memberId
                        } else {
                            selectedMembers + memberId
                        }
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
                label = { Text("Enter Text") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))


            PrimaryButton(
                text = "Send Request",
                onClick = {
                    groupID?.let { id ->
                        /*TODO: handle request with selectedMembers list*/
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
    ){
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

    val borderColor: Color

    if (isSelected){
        borderColor = MaterialTheme.colorScheme.primary
    } else {
        borderColor = MaterialTheme.colorScheme.surfaceVariant
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 2.dp, color = borderColor, shape = MaterialTheme.shapes.small)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ){
        Image(
            modifier = Modifier.size(40.dp),
            painter = painterResource(id = R.drawable.user_icon),
            contentDescription = "User icon"
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(member.name)
    }
}



@Preview(showBackground = true)
@Composable
fun RequestScreenPreview()  {
    val membervm: MemberViewModel = viewModel()

    membervm.addMember(User("1", "Bob", "bob@email.com", "12345678"))
    membervm.addMember(User("2", "Steve", "bob@email.com", "12345678"))
    membervm.addMember(User("3", "Jan", "bob@email.com", "12345678"))
    membervm.addMember(User("4", "Man", "bob@email.com", "12345678"))
    membervm.addMember(User("5", "Dan", "bob@email.com", "12345678"))
    membervm.addMember(User("6", "Stan", "bob@email.com", "12345678"))
    membervm.addMember(User("7", "Klan", "bob@email.com", "12345678"))

    GimmeDaMoneyTheme {
        RequestScreen({}, membervm, "")
    }
}


