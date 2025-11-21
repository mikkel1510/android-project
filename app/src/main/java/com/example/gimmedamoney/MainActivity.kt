package com.example.gimmedamoney

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gimmedamoney.chat.ChatViewModel
import com.example.gimmedamoney.chat.GroupChatScreen
import com.example.gimmedamoney.login.LoginScreen
import com.example.gimmedamoney.payment.RequestScreen
import com.example.gimmedamoney.ui.theme.GimmeDaMoneyTheme
import com.example.gimmedamoney.settings.SettingsScreen
import com.example.gimmedamoney.settings.SettingsViewModel
import com.example.gimmedamoney.settings.ThemeMode
import androidx.compose.runtime.collectAsState


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
                    startDestination = if (userVM.currentUser.collectAsState().value == null) "login_flow" else "app_flow",
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
                        composable("home") { backStackEntry ->
                            val parentEntry = remember(backStackEntry){
                                nav.getBackStackEntry("app_flow")
                            }
                            val groupVM: GroupViewModel = viewModel(parentEntry)

                            HomeScreen(
                                { id -> nav.navigate("group_flow/$id") },
                                { nav.navigate("createGroupScreen") },
                                { nav.navigate("settings") },
                                groupVM = groupVM,
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
                                { id -> nav.navigate("group_flow/$id"){
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
                                val chatEntry = remember(backStackEntry) {
                                    nav.getBackStackEntry("group_flow/{groupID}")
                                }
                                val appEntry = remember(backStackEntry){
                                    nav.getBackStackEntry("app_flow")
                                }
                                val chatVM: ChatViewModel = viewModel(chatEntry)
                                val groupVM: GroupViewModel = viewModel(appEntry)
                                val groupID = chatEntry.arguments?.getString("groupID")!!

                                GroupChatScreen(
                                    {nav.popBackStack()},
                                    {nav.navigate("members")},
                                    { nav.navigate("createRequest") },
                                    groupID = groupID,
                                    chatVM = chatVM,
                                    groupVM = groupVM
                                )
                            }

                            composable("createRequest") { backStackEntry ->
                                val groupEntry = remember(backStackEntry) {
                                    nav.getBackStackEntry("group_flow/{groupID}")
                                }
                                val appEntry = remember(backStackEntry) {
                                    nav.getBackStackEntry("app_flow")
                                }
                                val groupVM: GroupViewModel = viewModel(appEntry)
                                val groupID = groupEntry.arguments?.getString("groupID")!!

                                RequestScreen(
                                    { nav.popBackStack() },
                                    groupVM = groupVM,
                                    groupID = groupID,
                                    userVM = userVM,
                                )

                            }

                            composable(
                                route = "members",
                            ) { backStackEntry ->
                                val groupEntry = remember(backStackEntry) {
                                    nav.getBackStackEntry("group_flow/{groupID}")
                                }
                                val appEntry = remember(backStackEntry) {
                                    nav.getBackStackEntry("app_flow")
                                }
                                val groupID = groupEntry.arguments?.getString("groupID")!!
                                val groupVM: GroupViewModel = viewModel(appEntry)

                                MembersScreen(
                                    { nav.popBackStack() },
                                    { nav.navigate("addMember") },
                                    userVM = userVM,
                                    groupVM = groupVM,
                                    groupID = groupID
                                )
                            }
                            composable("addMember") { backStackEntry ->
                                val appEntry = remember(backStackEntry){
                                    nav.getBackStackEntry("app_flow")
                                }
                                val groupVM: GroupViewModel = viewModel(appEntry)

                                val groupID = nav
                                    .getBackStackEntry("group_flow/{groupID}")
                                    .arguments?.getString("groupID")!!

                                AddMemberScreen(
                                    { nav.popBackStack() },
                                    groupVM = groupVM,
                                    groupID = groupID
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}
