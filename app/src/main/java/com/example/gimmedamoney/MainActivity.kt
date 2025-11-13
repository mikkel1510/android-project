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
                        { nav.navigate("members_flow") },
                        { nav.navigate("createGroupScreen") }
                    )
                }

                composable("createGroupScreen") {
                    /*
                    { nav.popBackStack() }
                    { nav.navigate("addMembers")) }
                     */
                }



                navigation(startDestination = "groupChat", route = "chat_flow"){

                    composable("groupChat") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            nav.getBackStackEntry("chat_flow")
                        }
                        val vm: MemberViewModel = viewModel(parentEntry)
                        GroupChatScreen(
                            "Copenhagen Trip",
                            vm.members.size,
                            {nav.popBackStack()},
                            {nav.navigate("infoScreen")},
                        )

                        /*
                        { nav.navigate("members") }
                        { nav.navigate("createRequest")}

                         */
                    }

                    composable("createRequest") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            nav.getBackStackEntry("chat_flow")
                        }
                        val vm: MemberViewModel = viewModel(parentEntry)

                        /*
                        { nav.popBackStack() }
                         */

                    }


                    composable("members") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            nav.getBackStackEntry("chat_flow")
                        }
                        val vm: MemberViewModel = viewModel(parentEntry)
                        MembersScreen(
                            { nav.popBackStack() },
                            { nav.navigate("addMember") },
                            vm = vm
                        )
                    }
                    composable("addMember") { backStackEntry ->
                        val parentEntry = remember(backStackEntry){
                            nav.getBackStackEntry("chat_flow")
                        }
                        val vm: MemberViewModel = viewModel(parentEntry)
                        AddMemberScreen(
                            { nav.popBackStack() },
                            vm = vm
                        )
                    }
                }
            }
        }
    }
}