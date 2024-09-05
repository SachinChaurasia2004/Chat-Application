package com.example.demochatapp.presentations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelScreen(navController: NavController,viewModel: ChannelViewModel,authViewModel: AuthViewModel) {

    var showDialog by remember { mutableStateOf(false) }
    val channels = viewModel.channels.collectAsState()

    Scaffold(

        topBar = {
            TopAppBar(title = { Text(text = "Channels") },
                navigationIcon = {
                    IconButton(onClick = {
                        authViewModel.signOut()
                        navController.navigate("SignUpScreen") {
                            popUpTo("SignUpScreen") {
                                inclusive = true
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Add"
                        )
                    }
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(onClick = {
                showDialog = true
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier.padding(padding)
            ) {
                if (showDialog) {
                    AddChannelDialog(
                        onDismiss = { showDialog = false },
                        onAddChannel = { channelName ->
                            viewModel.createChannel(channelName)
                            showDialog = false
                        })
                }

                LazyColumn() {
                    items(channels.value) { channel ->
                        ChannelItem(channelName = channel.name, onClick = {
                            navController.navigate("ChatScreen/${channel.channelId}")
                        })
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    )
}

@Composable
fun ChannelItem(channelName: String,onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Gray)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(8.dp)
                .size(70.dp)
                .clip(CircleShape)
                .background(Color.Yellow.copy(alpha = 0.3f))

        ) {
            Text(
                text = channelName[0].uppercase(),
                color = Color.White,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }


        Text(text = channelName, modifier = Modifier.padding(8.dp), color = Color.White)

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChannelDialog(
    onDismiss: () -> Unit,
    onAddChannel: (String) -> Unit
) {
    var channelName by remember { mutableStateOf("") }

    BasicAlertDialog(onDismissRequest = { onDismiss() },
        content = {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Add Channel")
                TextField(
                    value = channelName,
                    onValueChange = { channelName = it },
                    label = { Text("Channel Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { onAddChannel(channelName) },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(text = "Add")
                }
            }
        }
    )

}

