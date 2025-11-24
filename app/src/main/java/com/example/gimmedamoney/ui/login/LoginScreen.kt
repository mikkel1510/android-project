package com.example.gimmedamoney.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.util.Patterns.EMAIL_ADDRESS
import com.example.gimmedamoney.viewmodel.UserViewModel
import com.example.gimmedamoney.ui.theme.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, userVM: UserViewModel = viewModel()) {
    var error by remember { mutableStateOf("") }
    var createAccount by rememberSaveable { mutableStateOf(false) }

    Scaffold{ innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!createAccount){
                Column(
                    verticalArrangement = Arrangement.spacedBy(25.dp)
                ){
                    Column {
                        var email by remember { mutableStateOf("") }
                        var password by remember { mutableStateOf("") }
                        Text("Login")
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        PrimaryButton(
                            text = "Login",
                            onClick = {
                                if (userVM.logIn(email, password)) {
                                    onLoginSuccess()
                                } else {
                                    error = "Invalid email or password"
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (error.isNotEmpty()) {
                            Text(
                                error,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    Column(
                        Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Don't have an account?")
                        PrimaryButton(
                            onClick = { createAccount = true; error = "" },
                            text = "Create new account"
                        )
                    }
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(25.dp)
                ) {
                    Column{
                        var name by remember { mutableStateOf("") }
                        var email by remember { mutableStateOf("") }
                        var phone by remember { mutableStateOf("") }
                        var password by remember { mutableStateOf("") }
                        Text("Create Account")
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { input ->
                                phone = input.filter { it.isDigit() } },
                            label = { Text("Phone Number") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        PrimaryButton(
                            text = "Create Account",
                            onClick = {
                                if (name.isBlank() || phone.isBlank() || email.isBlank() || password.isBlank()){
                                    error = "All fields must be filled"
                                    return@PrimaryButton
                                }

                                if (!EMAIL_ADDRESS.matcher(email).matches()){
                                    error = "Please enter a valid email address"
                                    return@PrimaryButton
                                }

                                if (!(phone.length == 8 && phone.all { it.isDigit() })){
                                    error = "Phone number must be exactly 8 digits"
                                    return@PrimaryButton
                                }

                                if (userVM.createUser(name, phone, email, password)) {
                                    onLoginSuccess()
                                } else {
                                    error = "Email or Phone already taken"
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (error.isNotEmpty()) {
                            Text(
                                error,
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                    }
                    Column(
                        Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PrimaryButton(
                            onClick = { createAccount = false; error = "" },
                            text = "Back to Login"
                        )
                    }

                }
            }
        }
    }
}
