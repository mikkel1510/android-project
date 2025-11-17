package com.example.gimmedamoney

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.gimmedamoney.chat.GroupChatScreen
import com.example.gimmedamoney.login.LoginScreen
import com.example.gimmedamoney.payment.RequestScreen
import com.example.myapp.members.AddMemberScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val nav = rememberNavController()
            val userViewModel: UserViewModel = viewModel()
            val groupViewModel: GroupViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return GroupViewModel(userViewModel) as T
                    }
                }
            )

            NavHost(
                navController = nav,
                startDestination = "login",
            ) {
                composable("login") {
                    LoginScreen(onLoginSuccess = {
                        nav.navigate("home") {
                            popUpTo("login") {
                                inclusive = true
                            }
                        }
                    })
                }

                composable("home") {
                    HomeScreen(
                        onMembersPress = { groupId -> nav.navigate("group/$groupId") },
                        onCreateGroup = { nav.navigate("createGroupScreen") },
                        vm = groupViewModel
                    )
                }

                composable("createGroupScreen") {
                    CreateGroupScreen(
                        onBackPress = { nav.popBackStack() },
                        onGroupCreatePress = { groupName, imageUri ->
                            val newGroup = groupViewModel.addGroup(groupName, imageUri?.toString())

                            if (newGroup != null) {
                                nav.popBackStack()
                            }
                        }
                    )

                }
                navigation(startDestination = "groupChat", route = "group/{groupId}") {

                    composable("groupChat") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            nav.getBackStackEntry("group/{groupId}")
                        }
                        val groupId = parentEntry.arguments?.getString("groupId")!!
                        val group = groupViewModel.getGroupById(groupId)
                        val vm = groupViewModel.getMemberViewModel(groupId)
                        GroupChatScreen(
                            group?.name ?: "Group",
                            vm.members.size,
                            { nav.popBackStack() },
                            { nav.navigate("members") },
                            { nav.navigate("createRequest") }
                        )
                    }

                    composable("createRequest") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            nav.getBackStackEntry("group/{groupId}")
                        }
                        val groupId = parentEntry.arguments?.getString("groupId")!!
                        val vm = groupViewModel.getMemberViewModel(groupId)

                        RequestScreen(
                            { nav.popBackStack() },
                            vm = vm
                        )

                    }


                    composable("members") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            nav.getBackStackEntry("group/{groupId}")
                        }
                        val groupId = parentEntry.arguments?.getString("groupId")!!
                        val vm = groupViewModel.getMemberViewModel(groupId)
                        MembersScreen(
                            { nav.popBackStack() },
                            { nav.navigate("addMember") },
                            vm = vm,
                            { nav.navigate("createRequest") }
                        )
                    }
                    composable("addMember") { backStackEntry ->
                        val parentEntry = remember(backStackEntry) {
                            nav.getBackStackEntry("group/{groupId}")
                        }
                        val groupId = parentEntry.arguments?.getString("groupId")!!
                        val vm = groupViewModel.getMemberViewModel(groupId)
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