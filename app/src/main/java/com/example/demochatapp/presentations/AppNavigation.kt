package com.example.demochatapp.presentations

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel,channelViewModel: ChannelViewModel,chatViewModel: ChatViewModel) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination =  "LoginScreen"){
        composable("SignUpScreen"){
            SignUpScreen(navController, authViewModel)
        }
        composable("LoginScreen"){
            LoginScreen(navController, authViewModel)
        }
        composable("ChannelScreen"){
            ChannelScreen(navController, channelViewModel,authViewModel)
        }
        composable("ChatScreen/{channelId}"){ backStackEntry ->
            val channelId = backStackEntry.arguments?.getString("channelId")
            if (channelId != null) {
                ChatScreen(navController, channelId, chatViewModel)
            }
        }
    }
    
}
