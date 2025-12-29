package com.example.gimmedamoney.core

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gimmedamoney.data.sync.SyncViewModel
import com.example.gimmedamoney.ui.common.SyncSnackbarHandler
import com.example.gimmedamoney.ui.group.AddMemberScreen
import com.example.gimmedamoney.ui.group.CreateGroupScreen
import com.example.gimmedamoney.viewmodel.GroupViewModel
import com.example.gimmedamoney.ui.home.HomeScreen
import com.example.gimmedamoney.ui.group.MembersScreen
import com.example.gimmedamoney.viewmodel.UserViewModel
import com.example.gimmedamoney.viewmodel.ChatViewModel
import com.example.gimmedamoney.ui.group.GroupChatScreen
import com.example.gimmedamoney.ui.login.LoginScreen
import com.example.gimmedamoney.ui.payment.RequestScreen
import com.example.gimmedamoney.ui.profile.ProfileScreen
import com.example.gimmedamoney.ui.settings.SettingsScreen
import com.example.gimmedamoney.ui.settings.SettingsViewModel
import com.example.gimmedamoney.ui.settings.ThemeMode
import com.example.gimmedamoney.ui.theme.GimmeDaMoneyTheme

class MainActivity : ComponentActivity() {
    @SuppressLint("UnrememberedGetBackStackEntry")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        LocalNotifier.createChannel(this)
        setContent {

            val settingsVM: SettingsViewModel = viewModel()
            val userVM: UserViewModel = viewModel()
            val syncVM: SyncViewModel = viewModel()

            val darkTheme = when (settingsVM.theme) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT  -> false
                ThemeMode.DARK   -> true
            }

            GimmeDaMoneyTheme(darkTheme) {

                val nav = rememberNavController()

                val globalSnackbarHost = remember { SnackbarHostState() }

                Scaffold(
                    snackbarHost = { SnackbarHost(globalSnackbarHost) }
                ) {
                    SyncSnackbarHandler(syncVM, globalSnackbarHost)
                    NavHost(
                        navController = nav,
                        startDestination = if (userVM.currentUser.collectAsState().value == null)
                            "login_flow"
                        else
                            "app_flow",
                    ) {

                        navigation(startDestination = "login", route = "login_flow") {
                            composable("login") {
                                LoginScreen(
                                    onLoginSuccess = {
                                        nav.navigate("app_flow") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    },
                                    userVM = userVM
                                )
                            }
                        }

                        navigation(startDestination = "home", route = "app_flow") {

                            composable("home") { backStackEntry ->
                                val parentEntry = remember(backStackEntry) {
                                    nav.getBackStackEntry("app_flow")
                                }
                                val groupVM: GroupViewModel = viewModel(parentEntry)

                                HomeScreen(
                                    onGroupPress = { id -> nav.navigate("group_flow/$id") },
                                    onCreateGroup = { nav.navigate("createGroupScreen") },
                                    onOpenSettings = { nav.navigate("settings") },
                                    onOpenProfile = { nav.navigate("profile") },
                                    groupVM = groupVM,
                                    userVM = userVM,
                                )
                            }

                            composable("settings") {
                                SettingsScreen(
                                    onOpenHome = { nav.navigate("home") },
                                    onOpenProfile = { nav.navigate("profile") },
                                    vm = settingsVM,
                                    userVM = userVM,
                                    onLogOut = {
                                        nav.navigate("login_flow") {
                                            popUpTo("app_flow") { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable("createGroupScreen") { backStackEntry ->
                                val parentEntry = remember(backStackEntry) {
                                    nav.getBackStackEntry("app_flow")
                                }
                                val groupVM: GroupViewModel = viewModel(parentEntry)

                                CreateGroupScreen(
                                    onBackPress = { nav.popBackStack() },
                                    onGroupCreatePress = { id ->
                                        nav.navigate("group_flow/$id") {
                                            popUpTo("createGroupScreen") { inclusive = true }
                                        }
                                    },
                                    userVM = userVM,
                                    vm = groupVM,
                                    syncVM = syncVM
                                )
                            }

                            composable("profile") {
                                ProfileScreen(
                                    onOpenHome = { nav.navigate("home") },
                                    onOpenSettings = { nav.navigate("settings") },
                                    onSaveBasic = { _, _, _ -> },
                                    onChangePassword = { _, _ -> },
                                    onAddPayment = { _ -> },
                                    onLogOut = {
                                        nav.navigate("login_flow") {
                                            popUpTo("app_flow") { inclusive = true }
                                        }
                                    },
                                    userVM = userVM
                                )
                            }

                            navigation(
                                startDestination = "groupChat",
                                route = "group_flow/{groupID}",
                                arguments = listOf(
                                    navArgument("groupID") { type = NavType.StringType }
                                )
                            ) {

                                composable("groupChat") { backStackEntry ->
                                    val chatEntry = remember(backStackEntry) {
                                        nav.getBackStackEntry("group_flow/{groupID}")
                                    }
                                    val appEntry = remember(backStackEntry) {
                                        nav.getBackStackEntry("app_flow")
                                    }
                                    val chatVM: ChatViewModel = viewModel(chatEntry)
                                    val groupVM: GroupViewModel = viewModel(appEntry)
                                    val groupID = chatEntry.arguments?.getString("groupID")!!

                                    GroupChatScreen(
                                        onBack = { nav.popBackStack() },
                                        onInfo = { nav.navigate("members") },
                                        onRequest = { nav.navigate("createRequest") },
                                        chatVM = chatVM,
                                        groupID = groupID,
                                        groupVM = groupVM,
                                        userVM = userVM,
                                        syncVM = syncVM,
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
                                        onBackPress = { nav.popBackStack() },
                                        groupVM = groupVM,
                                        groupID = groupID,
                                        userVM = userVM,
                                        syncVM = syncVM
                                    )
                                }

                                composable("members") { backStackEntry ->
                                    val groupEntry = remember(backStackEntry) {
                                        nav.getBackStackEntry("group_flow/{groupID}")
                                    }
                                    val appEntry = remember(backStackEntry) {
                                        nav.getBackStackEntry("app_flow")
                                    }
                                    val groupVM: GroupViewModel = viewModel(appEntry)
                                    val groupID = groupEntry.arguments?.getString("groupID")!!

                                    MembersScreen(
                                        onBackPress = { nav.popBackStack() },
                                        onAddMember = { nav.navigate("addMember") },
                                        userVM = userVM,
                                        groupVM = groupVM,
                                        groupID = groupID
                                    )
                                }

                                composable("addMember") { backStackEntry ->
                                    val appEntry = remember(backStackEntry) {
                                        nav.getBackStackEntry("app_flow")
                                    }
                                    val groupVM: GroupViewModel = viewModel(appEntry)

                                    val groupEntry = remember(backStackEntry) {
                                        nav.getBackStackEntry("group_flow/{groupID}")
                                    }
                                    val groupID = groupEntry.arguments?.getString("groupID")!!

                                    AddMemberScreen(
                                        onBackPress = { nav.popBackStack() },
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
}

