package com.example.gimmedamoney

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.gimmedamoney.chat.ChatViewModel
import com.example.gimmedamoney.chat.GroupChatScreen
import com.example.gimmedamoney.payment.RequestScreen
import com.example.gimmedamoney.ui.theme.GimmeDaMoneyTheme
import com.example.myapp.members.AddMemberScreen
import com.example.gimmedamoney.settings.SettingsScreen
import com.example.gimmedamoney.settings.SettingsViewModel
import com.example.gimmedamoney.settings.ThemeMode


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val settingsVM: SettingsViewModel = viewModel()

            val darkTheme = when (settingsVM.theme) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT  -> false
                ThemeMode.DARK   -> true
            }

            GimmeDaMoneyTheme(darkTheme) {

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
                                { nav.navigate("settings") }
                            )
                        }

                        composable("settings") {
                            SettingsScreen(onBack = { nav.popBackStack() }, vm = settingsVM)
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
                            val memberVM: MemberViewModel = viewModel(parentEntry)
                            val chatVM: ChatViewModel = viewModel(parentEntry)

                            GroupChatScreen(
                                groupName = "Copenhagen Trip",
                                memberCount = memberVM.members.size,
                                onBack = { nav.popBackStack() },
                                onInfo = { nav.navigate("members") },
                                onRequest = { nav.navigate("createRequest") },
                                chatVM = chatVM
                            )
                        }


                        composable("createRequest") { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                nav.getBackStackEntry("chat_flow")
                            }
                            val memberVM: MemberViewModel = viewModel(parentEntry)
                            val chatVM: ChatViewModel = viewModel(parentEntry)
                            RequestScreen(
                                { nav.popBackStack() },
                                memberVM = memberVM,
                                chatViewModel = chatVM
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
}