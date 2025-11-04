package com.example.gimmedamoney

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gimmedamoney.ui.theme.GimmeDaMoneyTheme

@Composable
fun PersonList(members: List<MemberViewModel.Member>) {
    Column {
        for(member in members){
            PersonBar(member)
        }
    }
}

@Composable
fun PersonBar(member: MemberViewModel.Member){
    val vm: MemberViewModel = viewModel()
    val dialogOpen = remember { mutableStateOf(false) }

    RemoveMemberDialog(      //TODO: Should be on the screen instead of every individual person
        active = dialogOpen.value,
        onDismissRequest = { dialogOpen.value = false },
        onConfirmation = { vm.removePerson(member.id) },
        memberName = member.name
    )
    Row(modifier = Modifier
        .padding(10.dp)
        .border(width = 2.dp, color = Color.LightGray, shape = RoundedCornerShape(8.dp))
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
            onClick = { dialogOpen.value = true }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier
                .width(100.dp)){
            Text("Remove")
        }
    }
}

@Composable
fun AddMemberBar(onAddMember: (String) -> Unit, modifier: Modifier = Modifier){
    var memberName by rememberSaveable() { mutableStateOf("") }

    Row (modifier
        .fillMaxWidth()
        .padding(12.dp)
        .height(75.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ){
        OutlinedTextField(
            value = memberName,
            onValueChange = { memberName = it },
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 56.dp)
        )
        Button(
            onClick = { onAddMember(memberName); memberName = "" },
            modifier = Modifier
                .width(75.dp)
        ) {
            Text("Add")
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
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            Button(onClick = { onConfirmation() }) {
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


@OptIn(ExperimentalMaterial3Api::class) //TODO: Maybe choose another version
@Composable
fun MembersScreen(vm: MemberViewModel = viewModel(), onAddMembers: () -> Unit){
    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("Members") },
                navigationIcon = {
                    IconButton(onClick = { /*TODO */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onAddMembers() }) {
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
            PersonList(vm.members)
            AddMemberBar(onAddMember = {name -> vm.addPerson(name)})
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MemberScreenPreview() {
    val vm: MemberViewModel = viewModel()
    vm.addPerson("Bob")
    vm.addPerson("Pete")
    vm.addPerson("Steve")

    GimmeDaMoneyTheme {
        MembersScreen(onAddMembers = {})
    }

}