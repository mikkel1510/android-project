package com.example.myapp.members

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gimmedamoney.GroupViewModel
import com.example.gimmedamoney.MemberViewModel
import com.example.gimmedamoney.ui.theme.PrimaryButton
import com.example.gimmedamoney.UserViewModel
import com.example.gimmedamoney.UserViewModel.User
import com.example.gimmedamoney.ui.theme.GimmeDaMoneyTheme
import com.example.gimmedamoney.ui.theme.TopNavBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberScreen(
    onBackPress: () -> Unit,
    userVM: UserViewModel = viewModel(),
    groupVM: GroupViewModel = viewModel(),
    groupID: String
) {
    Scaffold(
        topBar = {
            TopNavBar(
                title = "Add Members",
                subtitle = "to group with id $groupID",
                centerAligned = false,
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
            var searchQuery by rememberSaveable { mutableStateOf("") }

            val selectedUsers = rememberSaveable { mutableStateListOf<User>() }

            val users by userVM.users.collectAsState()

            val filtered = users.filter{ user ->
                !groupVM.getMemberIDsForGroup(groupID).contains(user.id) && (
                user.email.startsWith(searchQuery, ignoreCase = true) ||
                        user.phone.startsWith(searchQuery, ignoreCase = true))
            }

            if (selectedUsers.isNotEmpty()){
                SelectedUsers(
                    selectedUsers,
                    groupVM.getMemberIDsForGroup(groupID),
                    {
                        groupVM.addMembers(groupID, selectedUsers.map { it.id })
                        onBackPress()
                    }
                )
            }
            SearchBar({ query -> searchQuery = query })

            if (searchQuery.length >= 3){
                UserList(filtered, groupVM.getMemberIDsForGroup(groupID), selectedUsers)
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
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedTextColor = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 56.dp)
                .focusRequester(focusRequester)
        )
    }
}

@Composable
fun SelectedUsers(users: MutableList<User>, memberIDs: List<String>, onAdd: () -> Unit){
    Column {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween){
            Text("Selected Users")
            PrimaryButton(
                text = "Add to group",
                onClick = {onAdd()},
                icon = Icons.Filled.Add
            )
        }
        LazyRow(
            Modifier
                .fillMaxWidth(),
        ) {
            items(
                items = users,
                key = { it.id }
            ){
                UserIcon(it, { users.remove(it) })
            }
        }
    }
}

@Composable
fun UserIcon(user: User, onUnselect: () -> Unit){
    Column(
        Modifier.widthIn(60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = { onUnselect() },
            Modifier.size(50.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Go back",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxSize()
            )
        }
        val firstName = user.name.substringBefore(" ")
        Text(firstName, fontSize = 12.sp)
    }
}

@Composable
fun UserList(
    users: List<User>,
    memberIDs: List<String>,
    selectedUsers: MutableList<User>,
){
    if (users.isEmpty()){
        Row {
            Text("No results", fontSize = 20.sp, color = MaterialTheme.colorScheme.tertiary)
        }
    } else {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            items(
                items = users,
                key = { it.id }
            ) { user ->
                if (!memberIDs.contains(user.id)) {
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
        borderColor = MaterialTheme.colorScheme.surfaceVariant
    }
    Surface(
        modifier = Modifier
            .padding(5.dp)
            .fillMaxWidth()
            .clickable { onSelect(); }
            .padding(5.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 4.dp,
        tonalElevation = 0.dp,
        border = BorderStroke(3.dp, borderColor)
    ) {
        Row(
            Modifier
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {


            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Go back",
                tint = Color.White,
                modifier = Modifier.size(70.dp)
            )

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row {
                    Text(
                        user.name,
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                val maskedEmail = maskEmail(email = user.email)
                Column {
                    Text(maskedEmail, color = MaterialTheme.colorScheme.onBackground)

                    Row {
                        Text(
                            user.phone.substring(0, 2),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text("****", color = MaterialTheme.colorScheme.onBackground)
                        Text(
                            user.phone.substring(6),
                            color = MaterialTheme.colorScheme.onBackground
                        )

                    }

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
fun UserListPreview() {
    GimmeDaMoneyTheme {
        val user = User("1", "Bob Stevens", "bobsteve@email.com", "12345678")
        val user2 = User("2", "Stevens Bob", "bobsteve@email.com", "12345678")
        val user3 = User("3", "Joe Man", "bobsteve@email.com", "12345678")
        val users =  listOf(user, user2, user3)

        val members = listOf<String>()

        val selectedUsers: MutableList<User> = listOf(user).toMutableList()
    
        UserList(users, members, selectedUsers)
    }
}

@Preview(showBackground = true)
@Composable
fun SelectedUsersPreview(){
    GimmeDaMoneyTheme {
        val user = User("1", "Bob Stevens", "bobsteve@email.com", "12345678")
        val user2 = User("2", "Stevens Bob", "bobsteve@email.com", "12345678")
        val user3 = User("3", "Joe Man", "bobsteve@email.com", "12345678")
        val users =  listOf(user, user2, user3).toMutableList()

        val members = listOf<String>()

        SelectedUsers(users, members, {})
    }
}

