package com.example.gimmedamoney

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gimmedamoney.UserViewModel.User
import com.example.gimmedamoney.ui.theme.GimmeDaMoneyTheme
import com.example.gimmedamoney.ui.theme.Red

@Composable
fun MemberList(members: List<User>, onRemove: (User) -> Unit) {
    Column {
        members.forEach { member ->
           MemberBar(member, onRemove)
        }
    }
}

@Composable
fun MemberBar(member: User, onRemove: (User) -> Unit){

    Row(modifier = Modifier
        .padding(10.dp)
        .border(width = 2.dp, color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp))
        .padding(10.dp)
        .width(250.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Image(
            modifier = Modifier.size(40.dp),
            painter = painterResource(id = R.drawable.user_icon),
            contentDescription = "User icon"
        )
        Text(
            member.name
        )
        Button(
            onClick = { onRemove(member) }, colors = ButtonDefaults.buttonColors(containerColor = Red),
            modifier = Modifier
                .width(100.dp)){
            Text("Remove")
        }
    }
}

@Composable
fun RemoveMemberDialog(
    active: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    memberName: String
){
    if (!active) return
    AlertDialog(
        title = {
            Text("Confirm removal")
        },
        text = {
            Row {
                Text("Remove ")
                Text(memberName, fontWeight = FontWeight.Bold)
                Text("?")
            }

        },
        textContentColor = MaterialTheme.colorScheme.onBackground,
        titleContentColor = MaterialTheme.colorScheme.onBackground,
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            Button(onClick = { onConfirmation(); onDismissRequest() }) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button({ onDismissRequest() }) {
                Text("No")
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersScreen(onBackPress: () -> Unit, onAddMember: () -> Unit, vm: MemberViewModel = viewModel(), onCreateRequest: () -> Unit){
    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("Members") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                navigationIcon = {
                    IconButton(onClick = { onBackPress() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onAddMember() }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add new member",
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var selectedMember by remember { mutableStateOf<User?>(null) }

            selectedMember?.let { member ->
                RemoveMemberDialog(
                    true,
                    { selectedMember = null },
                    { vm.removeMember(member) },
                    member.name
                )
            }

            MemberList(vm.members, { member -> selectedMember = member })
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MemberScreenPreview() {
    GimmeDaMoneyTheme {
        val vm: MemberViewModel = viewModel()
        vm.addMember(User("1", "Bob", "bob@email.com", "12345678"))
        vm.addMember(User("2", "Steve", "steve@email.com", "87654321"))
        vm.addMember(User("3", "Joe", "joe@email.com", "45362718"))

        MembersScreen({}, {}, onCreateRequest = {})
    }

}