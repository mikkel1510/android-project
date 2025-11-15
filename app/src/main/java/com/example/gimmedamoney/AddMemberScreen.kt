package com.example.myapp.members

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gimmedamoney.MemberViewModel
import com.example.gimmedamoney.UserViewModel
import com.example.gimmedamoney.UserViewModel.User
import com.example.gimmedamoney.ui.theme.GimmeDaMoneyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberScreen(
    onBackPress: () -> Unit,
    memberVM: MemberViewModel = viewModel(),
    userVM: UserViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Members") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                navigationIcon = {
                    IconButton(onClick = { onBackPress() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            var searchQuery by remember { mutableStateOf("") }
            SearchBar({ query -> searchQuery = query })

            val selectedUsers = remember { mutableStateListOf<User>() }
            val addedUsers = remember { mutableStateListOf<User>() }

            val filtered = userVM.users.filter{ user ->
                !memberVM.members.contains(user) && (
                user.email.startsWith(searchQuery, ignoreCase = true) ||
                        user.phone.startsWith(searchQuery, ignoreCase = true))
            }

            if (selectedUsers.isNotEmpty()){
                SelectedUsers(selectedUsers, memberVM.members)
            }


            if (searchQuery.length >= 3){
                UserList(filtered, memberVM.members, selectedUsers)
            }

            if (selectedUsers.isNotEmpty()){
                Button(onClick = {
                    memberVM.addMembers(selectedUsers);
                    addedUsers.clear()
                    addedUsers.addAll(selectedUsers)
                    selectedUsers.clear()
                }, Modifier
                    .width(260.dp)
                    .height(70.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary)) {
                    Text("Add selected users (${selectedUsers.size})", fontSize = 20.sp)
                }
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

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

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
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search icon"
                )
            },
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 56.dp)
                .focusRequester(focusRequester)
        )
    }
}

@Composable
fun SelectedUsers(users: MutableList<User>, members: List<User>){
    Column {
        Text("Selected Users")
        LazyRow(
            Modifier
                .fillMaxWidth()
                .border(2.dp, MaterialTheme.colorScheme.tertiary, RoundedCornerShape(16.dp))
                .padding(10.dp)
        ) {
            items(
                items = users,
                key = { it.id }
            ){
                if (!members.contains(it)){
                    UserIcon(it, { users.remove(it) })
                }
            }
        }
    }
}

@Composable
fun UserIcon(user: User, onUnselect: () -> Unit){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = { onUnselect() },
            Modifier.size(50.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Go back",
                tint = Color.Black,
                modifier = Modifier.fillMaxSize()
            )
        }
        val firstName = user.name.substringBefore(" ")
        Text(firstName)
    }
}

@Composable
fun UserList(
    users: List<User>,
    members: List<User>,
    selectedUsers: MutableList<User>,
){
    if (users.isEmpty()){
        Row {
            Text("No results", fontSize = 20.sp, color = MaterialTheme.colorScheme.tertiary)
        }
    } else {
        LazyColumn(
            Modifier
                .clip(
                    RoundedCornerShape(12.dp)
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(
                items = users,
                key = { it.id }
            ) { user ->
                if (!members.contains(user)) {
                    UserCard(user, {
                        if (selectedUsers.contains(user)) selectedUsers.remove(user)
                        else selectedUsers.add(user)
                    }, selectedUsers.contains(user))
                }
            }
        }
    }
}

@Composable
fun UserCard(user: User, onSelect: () -> Unit, isSelected: Boolean){

    val borderColor: Color

    if (isSelected){
        borderColor = MaterialTheme.colorScheme.primary
    } else {
        borderColor = MaterialTheme.colorScheme.background
    }

    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onSelect(); }
        .clip(RoundedCornerShape(16.dp))
        .background(MaterialTheme.colorScheme.secondary)
        .border(width = 3.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
        .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ){


        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = "Go back",
            tint = Color.White,
            modifier = Modifier.size(70.dp)
        )

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ){
            Row{
                Text(
                    user.name, fontWeight = FontWeight.Bold, textDecoration = TextDecoration.Underline, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface
                )
            }
            val maskedEmail = maskEmail(email = user.email)
            Column{
                Text(maskedEmail, color = MaterialTheme.colorScheme.onSurface)

                Row{
                    Text(user.phone.substring(0, 2), color = MaterialTheme.colorScheme.onSurface)
                    Text("****", color = MaterialTheme.colorScheme.onSurface)
                    Text(user.phone.substring(6), color = MaterialTheme.colorScheme.onSurface)

                }

            }
        }
    }
}

fun maskEmail(email: String): String{
    val parts = email.split("@")

    val username = parts[0]
    val domain = parts[1]

    val maskedUsername = when {
        username.length <= 2 -> username.first() + "*"
        else -> username.take(2) + "*".repeat(username.length - 2)
    }

    return "$maskedUsername@$domain"
}


@Preview(showBackground = true)
@Composable
fun AddMemberPreview() {
    GimmeDaMoneyTheme {
        val vm: UserViewModel = viewModel()
        vm.addUser("1", "bob", "bob@email.com", "12345678")
        vm.addUser("2", "steve", "steve@email.com", "87654321")
        vm.addUser("3", "joe", "joe@email.com", "45362718")

        AddMemberScreen({})
    }
}

@Preview(showBackground = true)
@Composable
fun UserListPreview() {
    GimmeDaMoneyTheme {
        val user = User("1", "Bob Stevens", "bobsteve@email.com", "12345678")
        val user2 = User("2", "Stevens Bob", "bobsteve@email.com", "12345678")
        val user3 = User("3", "Joe Man", "bobsteve@email.com", "12345678")
        val users =  listOf(user, user2, user3)
    
        val members = listOf<User>()
    
        val selectedUsers: MutableList<User> = listOf(user).toMutableList()
    
        UserList(users, members, selectedUsers)
    }

}

