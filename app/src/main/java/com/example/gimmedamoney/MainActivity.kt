package com.example.gimmedamoney

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.gimmedamoney.chat.GroupChatScreen
import com.example.gimmedamoney.payment.RequestScreen
import com.example.myapp.members.AddMemberScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val nav = rememberNavController()
            NavHost(
                navController = nav,
                startDestination = "chat_flow",
            ) {

                navigation(startDestination = "home", route = "chat_flow"){ //Change startDestination to groupChat

                    composable("home") {
                        HomeScreen(
                            { nav.navigate("groupChat") },
                            { nav.navigate("createGroupScreen") },
                        )
                    }

                    composable("createGroupScreen") {
                        CreateGroupScreen(
                        { nav.popBackStack() },
                        { nav.navigate("groupChat") }
                        )

                    }
                    composable("groupChat") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            nav.getBackStackEntry("chat_flow")
                        }
                        val vm: MemberViewModel = viewModel(parentEntry)
                        GroupChatScreen(
                            "Copenhagen Trip",
                            vm.members.size,
                            {nav.popBackStack()},
                            {nav.navigate("members")},
                            { nav.navigate("createRequest") }
                        )
                    }

                    composable("createRequest") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            nav.getBackStackEntry("chat_flow")
                        }
                        val vm: MemberViewModel = viewModel(parentEntry)

                        RequestScreen(
                            { nav.popBackStack() },
                            vm = vm
                        )

                    }


                    composable("members") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            nav.getBackStackEntry("chat_flow")
                        }
                        val vm: MemberViewModel = viewModel(parentEntry)
                        MembersScreen(
                            { nav.popBackStack() },
                            { nav.navigate("addMember") },
                            vm = vm,
                            { nav.navigate("createRequest")}
                        )
                    }
                    composable("addMember") { backStackEntry ->
                        val parentEntry = remember(backStackEntry){
                            nav.getBackStackEntry("chat_flow")
                        }
                        val vm: MemberViewModel = viewModel(parentEntry)
                        AddMemberScreen(
                            { nav.popBackStack() },
                            memberVM = vm,
                        )
                    }
                }
            }
        }
    }
}