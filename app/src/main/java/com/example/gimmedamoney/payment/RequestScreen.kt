package com.example.gimmedamoney.payment

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gimmedamoney.MemberViewModel
import com.example.gimmedamoney.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestScreen(
    onBackPress: () -> Unit,
    vm: MemberViewModel
) {
    var amount by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Request") },
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
                GroupList(vm.members)
            }
            Spacer(modifier = Modifier.weight(1f))

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))


            Button(
                onClick = {/*TODO*/},
                modifier = Modifier.fillMaxWidth()
            ){
                Text("Send Request")
            }

        }
    }
}

@Composable
fun GroupList(members: List<MemberViewModel.Member>) {
    LazyColumn (
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ){
        items(members) { member ->
            GroupBar(member)
        }
    }
}

@Composable
fun GroupBar(member: MemberViewModel.Member){

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 2.dp, color = Color.LightGray, shape = RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ){
        Image(
            modifier = Modifier.size(40.dp),
            painter = painterResource(id = R.drawable.user_icon),
            contentDescription = "User icon"
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            member.name
        )
    }
}


@Preview(showBackground = true)
@Composable
fun RequestScreenPreview()  {
    val vm: MemberViewModel = viewModel()

    vm.addPerson("Bob")
    vm.addPerson("Pete")
    vm.addPerson("Steve")
    vm.addPerson("hj")
    vm.addPerson("blob")
    vm.addPerson("bleh")


    RequestScreen({}, vm)
}


