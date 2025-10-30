package com.example.gimmedamoney

import android.R
import android.app.Person
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.gimmedamoney.ui.theme.GimmeDaMoneyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GimmeDaMoneyTheme {
                    PersonList()
                    AddMember()
                }
            }
        }
    }
data class Member(
    val name: String
)


@Composable
fun PersonList() {
    val members = remember { mutableStateListOf<Member>() }
    Column {
        for(member in members){
            Person(member.name)
        }
    }
}


@Composable
fun Person(name: String, modifier: Modifier = Modifier){
    Row(){
        Text(
            name
        )
    }
}

@Composable
fun AddMember(members: MutableList<Member>){
    var member = Member("Bruun")
    Button(onClick = { members.add(member) }) { }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GimmeDaMoneyTheme {
        PersonList()
        AddMember()
    }
}