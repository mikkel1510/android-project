package com.example.gimmedamoney

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gimmedamoney.chat.GroupChatScreen
import com.example.gimmedamoney.login.LoginScreen
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
            val userVM: UserViewModel = viewModel()

            val darkTheme = when (settingsVM.theme) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT  -> false
                ThemeMode.DARK   -> true
            }

            GimmeDaMoneyTheme(darkTheme) {

                val nav = rememberNavController()
                NavHost(
                    navController = nav,
                    startDestination = if (userVM.currentUser.value == null) "login_flow" else "app_flow",
                ) {

                    navigation(startDestination = "login", route = "login_flow"){
                        composable("login"){
                            LoginScreen(
                                { nav.navigate("app_flow"){
                                  popUpTo("login") {
                                      inclusive = true
                                  }
                                } },
                                userVM = userVM
                            )
                        }
                    }

                    navigation(startDestination = "home", route = "app_flow"){ //Change startDestination to groupChat
                        composable("home") {
                            HomeScreen(
                                { id -> nav.navigate("group_flow/$id") },
                                { nav.navigate("createGroupScreen") },
                                { nav.navigate("settings") },
                                userVM = userVM
                            )
                        }

                        composable("settings") {
                            SettingsScreen(
                                onBack = { nav.popBackStack() },
                                vm = settingsVM,
                                userVM = userVM,
                                onLogOut = { nav.navigate("login_flow"){
                                    popUpTo("settings"){
                                        inclusive = true
                                    }
                                } }
                            )
                        }

                        composable("createGroupScreen") {
                            CreateGroupScreen(
                                { nav.popBackStack() },
                                { nav.navigate("groupChat"){
                                    popUpTo("createGroupScreen") {
                                        inclusive = true
                                    }
                                } },
                                userVM = userVM
                            )
                        }

                        navigation(
                            startDestination = "groupChat",
                            route = "group_flow/{groupID}",
                            arguments = listOf(
                                navArgument("groupID") { type = NavType.StringType }
                            )
                        ){
                            composable("groupChat") { backStackEntry ->
                                val parentEntry = remember(backStackEntry) {
                                    nav.getBackStackEntry("app_flow")
                                }
                                val vm: MemberViewModel = viewModel(parentEntry)
                                val groupID = backStackEntry.arguments?.getString("groupID")!!

                                GroupChatScreen(
                                    "Copenhagen Trip",
                                    vm.members.size,
                                    {nav.popBackStack()},
                                    {nav.navigate("members")},
                                    { nav.navigate("createRequest") },
                                    groupID = groupID
                                )
                            }

                            composable("createRequest") { backStackEntry ->
                                val parentEntry = remember(backStackEntry) {
                                    nav.getBackStackEntry("app_flow")
                                }
                                val vm: MemberViewModel = viewModel(parentEntry)
                                val groupID = backStackEntry.arguments?.getString("groupID")!!

                                RequestScreen(
                                    { nav.popBackStack() },
                                    membervm = vm
                                )

                            }

                            composable(
                                route = "members",
                            ) { backStackEntry ->
                                val parentEntry = remember(backStackEntry) {
                                    nav.getBackStackEntry("app_flow")
                                }
                                val vm: MemberViewModel = viewModel(parentEntry)
                                val groupID = backStackEntry.arguments?.getString("groupID")!!

                                MembersScreen(
                                    { nav.popBackStack() },
                                    { nav.navigate("addMember") },
                                    vm = vm,
                                    { nav.navigate("createRequest")},
                                    groupID = groupID
                                )
                            }
                            composable("addMember") { backStackEntry ->
                                val parentEntry = remember(backStackEntry){
                                    nav.getBackStackEntry("app_flow")
                                }
                                val vm: MemberViewModel = viewModel(parentEntry)
                                val groupID = backStackEntry.arguments?.getString("groupID")!!

                                AddMemberScreen(
                                    { nav.popBackStack() },
                                    memberVM = vm
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}