package com.macaosoftware.sdui.app.marketplace.amadeus.auth.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import com.macaosoftware.plugin.LoginRequest
import com.macaosoftware.plugin.MacaoUser
import com.macaosoftware.plugin.util.MacaoResult
import com.macaosoftware.sdui.app.OSVersion
import com.macaosoftware.sdui.app.Plugin
import com.macaosoftware.sdui.app.marketplace.amadeus.auth.AuthViewModel
import com.macaosoftware.sdui.app.marketplace.amadeus.auth.forget.ForgetScreen
import com.macaosoftware.sdui.app.marketplace.amadeus.auth.signup.SignUpScreen
import com.macaosoftware.sdui.app.marketplace.amadeus.profile.ProfileScreen
import kotlinx.coroutines.launch

class LoginScreen(
    private val authViewModel: AuthViewModel,
) : Screen {

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun Content() {
        var emai by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var isError by remember { mutableStateOf(false) }
        var showMessage by remember { mutableStateOf(false) }
        var messageText by remember { mutableStateOf("") }
        var loadingState by remember { mutableStateOf(false) }
        val navigator = LocalNavigator.current
        val keyboardController = LocalSoftwareKeyboardController.current
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(true) {
            val userDataResult = authViewModel.plugin.checkAndFetchUserData()
            when (userDataResult) {
                is MacaoResult.Success -> {
                    navigator?.push(ProfileScreen(authViewModel))
                }
                is MacaoResult.Error -> {
                    println("User not logged in: ${userDataResult.error}")
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Welcome to ${OSVersion()}")
                // Username TextField
                OutlinedTextField(
                    value = emai,
                    onValueChange = {
                        emai = it
                        isError = false
                    },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    isError = isError
                )

                // Password TextField
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        isError = false
                    },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    isError = isError
                )

                // Error Text
                if (isError) {
                    Text(
                        text = "Invalid email or password",
                        color = Color.Red,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                // Login Button
                Button(
                    onClick = {
                        if (isValidCredentials(emai, password)) {
                            coroutineScope.launch {
                                loadingState = true
                                keyboardController?.hide()

                                val loginRequest = LoginRequest(
                                    email = emai,
                                    password = password
                                )
                                val loginResult =
                                    authViewModel.login(loginRequest.email, loginRequest.password)
                                handleLoginResult(loginResult, navigator)
                            }
                        } else {
                            isError = true
                        }
                    },
                    modifier = Modifier.padding(8.dp).fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            if (showMessage) messageText else "Login", // Show message or "Login" text
                            modifier = Modifier.weight(1f, true), // Center the text
                            textAlign = TextAlign.Center // Center the text horizontally
                        )
                        if (!showMessage) {
                            Spacer(modifier = Modifier.width(8.dp)) // Add spacing between text and CircularProgressIndicator
                            AnimatedVisibility(visible = loadingState) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // Text for account not created
                Text(
                    text = "Don't have an account? Create one!",
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            navigator?.push(SignUpScreen(authViewModel))
                        }
                )

                // Text for forgot password
                Text(
                    text = "Forgot Password?",
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            navigator?.push(ForgetScreen(authViewModel))
                        }
                )

                // Text for forgot password
                Text(
                    text = "Login With Link",
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            navigator?.push(LoginWithEmailLinkScreen(authViewModel))
                        }
                )
            }
        }
    }

    private fun handleLoginResult(result: MacaoResult<MacaoUser>, navigator: Navigator?) {
        when (result) {
            is MacaoResult.Success -> {
                println("Login successful!")

                // Navigate to the Profile screen
                navigator?.push(ProfileScreen(authViewModel))
            }

            is MacaoResult.Error -> {
                println("Login failed: ${result.error}")
                // Handle error cases if needed
            }
        }
    }


    private fun isValidCredentials(username: String, password: String): Boolean {
        println("Validating credentials - username: $username, password: $password")
        return username.isNotEmpty() && password.isNotEmpty()
    }
}