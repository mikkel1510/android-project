package com.example.gimmedamoney.ui.group

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gimmedamoney.R
import com.example.gimmedamoney.data.model.User
import com.example.gimmedamoney.ui.theme.DialogPopUp
import com.example.gimmedamoney.ui.theme.GimmeDaMoneyTheme
import com.example.gimmedamoney.ui.theme.Red
import com.example.gimmedamoney.ui.theme.TopNavBar
import com.example.gimmedamoney.viewmodel.GroupViewModel
import com.example.gimmedamoney.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun MemberList(members: List<User>, onRemove: (User) -> Unit) {
    Column {
        members.forEachIndexed { index, member ->
           MemberBar(member, onRemove, index == 0)
        }
    }
}

@Composable
fun MemberBar(member: User, onRemove: (User) -> Unit, isCreator: Boolean = false){

    Row(modifier = Modifier
        .padding(10.dp)
        .border(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = MaterialTheme.shapes.large
        )
        .padding(10.dp)
        .width(250.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){

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
        Text(
            if (isCreator) "${member.name} 👑" else member.name
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
fun MembersScreen(
    onBackPress: () -> Unit,
    onAddMember: () -> Unit,
    groupVM: GroupViewModel = viewModel(),
    userVM: UserViewModel = viewModel(),
    groupID: String
){
    Scaffold (
        topBar = {
            TopNavBar(
                title = "Members",
                subtitle = "of ${groupVM.getGroupById(groupID).name}",
                centerAligned = false,
                actions = {
                    IconButton(onClick = { onAddMember() }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add new member",
                        )
                    }
                },
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
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LaunchedEffect(groupID) {
                groupVM.listenToGroup(groupID)
            }

            val users by userVM.users.collectAsState()
            val members = groupVM.getMembersForGroup(groupID, users)

            var selectedMember by remember { mutableStateOf<User?>(null) }

            selectedMember?.let { member ->
                DialogPopUp(
                    active = true,
                    title = "Confirm Removal",
                    content = {
                        Row{
                            Text("Remove ")
                            Text(member.name, fontWeight = FontWeight.Bold)
                            Text("?")
                        }
                    },
                    onDismissRequest = { selectedMember = null },
                    onConfirmation = { groupVM.removeMember(groupID, member.id){ selectedMember = null } },
                    confirmButtonColor = Red
                )
            }


            MemberList(members, { member -> selectedMember = member })

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = sdf.format(groupVM.getGroupById(groupID).creationDate)
            Text("Group created on: $formattedDate")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MemberScreenPreview() {
    GimmeDaMoneyTheme {
        MembersScreen({}, {}, groupID = "")
    }

}
