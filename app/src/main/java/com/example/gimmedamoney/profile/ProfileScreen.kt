package com.example.gimmedamoney.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.navigationBarsPadding

class ProfileViewModel : ViewModel() {
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var phone by mutableStateOf("")
}

enum class PaymentMethod { GoogleWallet, PayPal, Card }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenFavourites: () -> Unit = {},
    onChangeProfilePic: () -> Unit = {},                  // placeholder
    onSaveBasic: (name: String, email: String, phone: String) -> Unit = { _,_,_ -> },
    onChangePassword: (current: String, new: String) -> Unit = { _,_ -> },
    onAddPayment: (PaymentMethod) -> Unit = {},
    onLogOut: () -> Unit,
    vm: ProfileViewModel = viewModel()
) {
    var showAddPayment by remember { mutableStateOf(false) }
    var currentPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            ProfileBottomNavBar(
                onGoHome = onOpenHome,
                onOpenSettings = onOpenSettings,
                onFavourites = onOpenFavourites
            )
        }
    ) { inner ->
        val scroll = rememberScrollState()
        Column(
            Modifier
                .padding(inner)
                .verticalScroll(scroll)
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
        ) {
            Spacer(Modifier.height(12.dp))

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.AccountCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(Modifier.weight(1f)) {
                    Text("Profile picture", style = MaterialTheme.typography.titleMedium)
                    Text("Tap to change", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                TextButton(onClick = onChangeProfilePic) { Text("Change") }
            }

            Spacer(Modifier.height(16.dp))

            Text("Basic info", style = MaterialTheme.typography.titleMedium)
            Card(Modifier.fillMaxWidth()) {
                Column(
                    Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = vm.name, onValueChange = { vm.name = it },
                        label = { Text("Name") },
                        leadingIcon = { Icon(Icons.Outlined.Person, null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = vm.email,
                        onValueChange = { vm.email = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Outlined.Email, null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = vm.phone,
                        onValueChange = { new ->
                            if (new.all { it.isDigit() || it == ' ' || it == '+' }) vm.phone = new
                        },
                        label = { Text("Phone") },
                        leadingIcon = { Icon(Icons.Outlined.Phone, null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = { onSaveBasic(vm.name, vm.email, vm.phone) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Save changes") }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("Security", style = MaterialTheme.typography.titleMedium)
            Card(Modifier.fillMaxWidth()) {
                Column(
                    Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = newPass, onValueChange = { newPass = it },
                        label = { Text("New password") },
                        leadingIcon = { Icon(Icons.Outlined.Lock, null) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = confirmPass, onValueChange = { confirmPass = it },
                        label = { Text("Confirm new password") },
                        leadingIcon = { Icon(Icons.Outlined.Lock, null) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            if (newPass == confirmPass && newPass.isNotBlank())
                                onChangePassword(currentPass, newPass)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Update password") }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("Payments", style = MaterialTheme.typography.titleMedium)
            Card(Modifier.fillMaxWidth()) {
                Column(
                    Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "No payment methods linked",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = { showAddPayment = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Outlined.AccountBalanceWallet, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Add payment method")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("Account", style = MaterialTheme.typography.titleMedium)
            Button(
                onClick = onLogOut,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Outlined.Logout, null)
                Spacer(Modifier.width(8.dp))
                Text("Log out")
            }

            Spacer(Modifier.height(24.dp))
        }
    }

    if (showAddPayment) {
        AlertDialog(
            onDismissRequest = { showAddPayment = false },
            icon = { Icon(Icons.Outlined.AccountBalanceWallet, null) },
            title = { Text("Add payment") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            onAddPayment(PaymentMethod.GoogleWallet); showAddPayment = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Add Google Wallet") }
                    OutlinedButton(
                        onClick = {
                            onAddPayment(PaymentMethod.PayPal); showAddPayment = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Add PayPal") }
                    OutlinedButton(
                        onClick = {
                            onAddPayment(PaymentMethod.Card); showAddPayment = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Add card") }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddPayment = false }) { Text("Close") }
            }
        )
    }
}

@Composable
private fun ProfileBottomNavBar(
    onGoHome: () -> Unit,
    onOpenSettings: () -> Unit,
    onFavourites: () -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        NavigationBarItem(
            selected = true,
            onClick = { /* no-op */ },
            icon = { Icon(Icons.Outlined.Person, null) },
            label = { Text("Profile") }
        )
        NavigationBarItem(
            selected = false,
            onClick = onGoHome,
            icon = { Icon(Icons.Outlined.Group, null) },
            label = { Text("Groups") }
        )
        NavigationBarItem(
            selected = false,
            onClick = onFavourites,
            icon = { Icon(Icons.Outlined.FavoriteBorder, null) },
            label = { Text("Favourites") }
        )
        NavigationBarItem(
            selected = false,
            onClick = onOpenSettings,
            icon = { Icon(Icons.Outlined.Settings, null) },
            label = { Text("Settings") }
        )
    }
}
