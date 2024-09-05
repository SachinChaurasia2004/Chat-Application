package com.example.demochatapp.presentations

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.demochatapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController,viewModel: AuthViewModel) {

    val color = Color(0xFFFFA500)
    var passwordVisible by remember { mutableStateOf(false) }
    var textColor by remember { mutableStateOf(Color.Black) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loading = viewModel.loadingState.collectAsState()
    val authState = viewModel.authState.collectAsState()

    LaunchedEffect(key1 = authState.value) {
        Log.d("TAG", "LoginScreen: ${authState.value}")
        when {
            authState.value != null -> {
                navController.navigate("ChannelScreen") {
                    popUpTo("LoginScreen") {
                        inclusive = true
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
               colors = TopAppBarDefaults.topAppBarColors(
                     containerColor = color,
                    titleContentColor = Color.White,
                ),
                title = { Text(text = "Login Screen", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold) })
        }
    ){ innerPadding ->
        Box (modifier = Modifier.padding(innerPadding)) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                OutlinedTextField(value = email, onValueChange = {email = it},
                    placeholder = { Text(text = "Email") },
                    leadingIcon = { Text(text = "📧") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp)
                        .clickable {
                            textColor = if (textColor == Color.Black) Color.Blue else Color.Black
                        },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(value = password, onValueChange = {password = it},
                    placeholder = { Text(text = "Password") },
                    leadingIcon = { Text(text = "🔑") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp)
                        .clickable {
                            textColor = if (textColor == Color.Black) Color.Blue else Color.Black
                        },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            painterResource(id = R.drawable.baseline_visibility_24)
                        else
                            painterResource(id = R.drawable.baseline_visibility_off_24)
                        IconButton(onClick = {
                            passwordVisible = !passwordVisible
                        }) {
                            Icon(painter = image, contentDescription = "Password Toggle")
                        }
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp),
                    colors = ButtonColors(containerColor = color, contentColor = Color.White, disabledContainerColor = Color.Gray, disabledContentColor = Color.Black),
                    onClick = {
                        viewModel.signIn(email, password)
                    },
                    enabled = email.isNotBlank() && password.isNotBlank()
                ){
                    Text(text = "Sign In",
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize)
                }
                Spacer(modifier = Modifier.height(8.dp))

                when {
                    loading.value -> {
                        CircularProgressIndicator()
                    }
                }

                TextButton(onClick = {
                        navController.navigate("SignUpScreen")
                }) {
                    Text(text = "Don't have an account? Sign Up")
                }
            }
        }
    }
}
