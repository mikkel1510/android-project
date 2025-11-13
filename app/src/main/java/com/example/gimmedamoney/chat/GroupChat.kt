package com.example.gimmedamoney.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

    @Composable
    fun GroupChatScreen(
        groupName: String,
        memberCount: Int,
        onBack: () -> Unit = {},
        onInfo: () -> Unit = {},
        onRequest: () -> Unit
    ){
        Scaffold (
            topBar = { TopBar(onBack, onInfo, groupName, memberCount) },
            bottomBar = { BottomBar(onRequest) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){

            }
        }
    }

    @Composable
    fun BottomBar(onRequest: () -> Unit){
        Column (
            modifier = Modifier.fillMaxWidth().padding(10.dp).imePadding(),

        ) {
            Row ( modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Button(onClick = { onRequest() }, modifier = Modifier.weight(1f)) { Text("Request") }
                Button(onClick = {      }, modifier = Modifier.weight(1f)) { Text("Pay all")}
            }

            Row (
                modifier = Modifier.fillMaxWidth().padding(10.dp)
            ) {
                TextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    placeholder = {Text("Send a message...")},
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = {           }) {
                    Icon(Icons.Default.Send, contentDescription = "Send", Modifier.size(30.dp))
                }
            }
        }
    }

    @Composable
    fun TopBar( onBack: ()-> Unit, onInfo: ()-> Unit, name: String, memberCount: Int ){
        Column { //TODO: APPLY TOP PADDING
            Spacer(Modifier.padding(20.dp)) //TODO: REMOVE IN FUTURE
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }

            Row (
                modifier = Modifier.fillMaxWidth()
            ){
                Row (modifier = Modifier.weight(1f)) {
                    Column (modifier = Modifier.padding(start = 35.dp)){
                        Text("Group")
                        Text(name, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                        Text("$memberCount members")
                    }
                }

                IconButton(onClick = onInfo, modifier = Modifier.padding(end = 20.dp)) {
                    Icon(Icons.Filled.Info, contentDescription = "Group info", Modifier.size(50.dp))
                }
            }

        }

    }



@Preview(showBackground = true)
@Composable
fun GroupChatPreview() {
    MaterialTheme {
        GroupChatScreen(groupName = "Copenhagen Trip", memberCount = 4, {}, {}, {})
    }
}