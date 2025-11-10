package com.example.myapp.members

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gimmedamoney.MemberViewModel
import com.example.gimmedamoney.R
import com.example.gimmedamoney.UserViewModel
import com.example.gimmedamoney.UserViewModel.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberScreen(onBackPress: () -> Unit, memberVM: MemberViewModel = viewModel(), userVM: UserViewModel = viewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Members") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { onBackPress() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                            tint = Color.White
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var searchQuery by remember { mutableStateOf("") }
            SearchBar({ query -> searchQuery = query })

            val selectedUsers = remember { mutableStateListOf<User>() }
            val addedUsers = remember { mutableStateListOf<User>() }

            val filtered = userVM.users.filter{ user ->
                user.name.startsWith(searchQuery, ignoreCase = true)
            }

            UserList(filtered, memberVM.members, selectedUsers, )
            Button(onClick = {
                memberVM.addMembers(selectedUsers);
                addedUsers.clear()
                addedUsers.addAll(selectedUsers)
            }) {
                Text("Add selected users")
            }

            if (addedUsers.isNotEmpty()){
                val sharedTextSize = TextStyle(fontSize = 20.sp)
                addedUsers.forEach { user ->
                    Row {
                        Text("Added ", style = sharedTextSize)
                        Text(user.name, fontWeight = FontWeight.Bold, style = sharedTextSize)
                        Text(" to group", style = sharedTextSize)
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(updateQuery: (String) -> Unit){
    var query by rememberSaveable { mutableStateOf("") }

    Row (Modifier
        .fillMaxWidth()
        .padding(12.dp)
        .height(75.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ){
        OutlinedTextField(
            value = query,
            onValueChange = { query = it; updateQuery(query) },
            label = { Text("Phone or email") },
            singleLine = true,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 56.dp)
        )
        Button(
            onClick = { },
            modifier = Modifier
                .width(75.dp)
        ) {
            Text("Search")
        }
    }
}

@Composable
fun UserList(
    users: List<User>,
    members: List<User>,
    selectedUsers: MutableList<User>,
){
    Column {
        users.forEach { user ->

            if (!members.contains(user)){
                UserCard(user, {
                    if (selectedUsers.contains(user)) selectedUsers.remove(user)
                    else selectedUsers.add(user)
                })
            }
        }
    }
}

@Composable
fun UserCard(user: User, onSelect: () -> Unit){

    var selected by remember { mutableStateOf(false) }
    val borderColor: Color

    if (selected){
        borderColor = Color.Blue
    } else {
        borderColor = Color.LightGray
    }

    Row(modifier = Modifier
        .padding(10.dp)
        .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(8.dp))
        .padding(10.dp)
        .width(250.dp)
        .clickable { onSelect(); selected = !selected },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Image(
            modifier = Modifier.size(40.dp),
            painter = painterResource(id = R.drawable.user_icon),
            contentDescription = "User icon"
        )
        Text(
            user.name
        )
    }
}


@Preview(showBackground = true)
@Composable
fun AddMemberPreview() {
    val vm: UserViewModel = viewModel()
    vm.addUser("1", "bob")
    vm.addUser("2", "steve")
    vm.addUser("3", "joe")
    AddMemberScreen({})
}
