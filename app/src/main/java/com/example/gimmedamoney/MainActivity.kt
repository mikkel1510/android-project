package com.example.gimmedamoney

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapp.members.AddMemberScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val nav = rememberNavController()
            NavHost(
                navController = nav,
                startDestination = "home",
            ) {
                composable("home") {
                    HomeScreen(
                        navController = nav,
                    )
                }
                composable("members") {
                    MembersScreen(
                        navController = nav,
                    )
                }
                composable("addMember") {
                    AddMemberScreen(
                        { nav.popBackStack() }
                    )
                }
            }
        }
    }
}