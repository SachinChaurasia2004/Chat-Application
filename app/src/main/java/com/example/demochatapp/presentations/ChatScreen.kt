package com.example.demochatapp.presentations

import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.demochatapp.R
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavController, channelId: String,viewModel: ChatViewModel) {
    val messages by viewModel.messages.collectAsState()
    var messageText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    val imagePicker =   rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()) {
            uri: Uri? ->
        if (uri != null) {
            viewModel.sendImageMessage(uri,channelId,"")
        }
    }


LaunchedEffect(channelId) {
    viewModel.listenForMessages(channelId)
}
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Chat") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(messages.size) { message ->
                        val message = messages[message]
                        ChatBubble(
                            message.message,
                            message.senderName,
                            message.senderId,
                            message.imageUrl
                        )
                    }
                }
                Row(
                    modifier = Modifier.padding(8.dp)
                ) {

                    IconButton(onClick = {
                    imagePicker.launch("image/*")

                    }) {
                        Icon(painter = painterResource(id = R.drawable.image), contentDescription = null,
                        modifier = Modifier.size(48.dp)
                        )
                    }
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a message") }
                    )
                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                viewModel.sendMessage(channelId, messageText)
                                messageText = ""
                                keyboardController?.hide()
                            }
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send",
                            modifier = Modifier.size(48.dp)
                            )
                    }
                }
            }
        }
    )
}

@Composable
fun ChatBubble(
    message: String,
    senderName: String,
    senderId: String,
    imageUrl: String?
) {
    val isCurrentUser = senderId == FirebaseAuth.getInstance().currentUser?.uid
    val backgroundColor = if (isCurrentUser) Color(0xFFDCF8C6) else Color(0xFFFFFFFF)
    val cornerRadius = if (isCurrentUser) RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    else RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .background(backgroundColor, shape = cornerRadius)
                .padding(8.dp),
            horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
        ) {
            if (!isCurrentUser) {
                Text(
                    text = senderName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Blue,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    //modifier = Modifier.height(200.dp)
                )
            }
            Text(
                text = message,
                style = MaterialTheme.typography.titleLarge.copy(color = Color.Gray),
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}

