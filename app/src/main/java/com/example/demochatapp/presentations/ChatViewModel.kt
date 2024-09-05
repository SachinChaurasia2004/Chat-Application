package com.example.demochatapp.presentations

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.storage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.SimpleTimeZone
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    fun sendMessage(channelId: String, messageText: String,image: String? = null) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val message = Message(
                    message = messageText,
                    senderId = auth.currentUser?.uid ?: "",
                    senderName = auth.currentUser?.displayName ?: "",
                    timestamp = System.currentTimeMillis(),
                    imageUrl = image
                )
                fireStore.collection("channels").document(channelId).collection("messages").add(message)
            }
        }

    }

    fun listenForMessages(channelId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            fireStore.collection("channels").document(channelId).collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, _ ->
                    val messages = snapshot?.documents?.map { document ->

                        Message(
                            message = document.data?.get("message").toString(),
                            senderId = document.data?.get("senderId").toString(),
                            senderName = document.data?.get("senderName").toString(),
                            timestamp = document.data?.get("timestamp") as Long,
                            imageUrl = document.data?.get("imageUrl").toString()
                        )

                    }
                    _messages.value = messages ?: emptyList()
                   // Log.d("TAG", "listenForMessages: $messages")
                }
        }

    }

    fun sendImageMessage(uri: Uri, channelId: String,messageText: String) {
        val imageRef = Firebase.storage.reference.child("images/${UUID.randomUUID()}")
        imageRef.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    sendMessage(channelId,messageText,downloadUri.toString() )
                    Log.d("TAG", "sendImageMessage: $downloadUri")
                }
            }
    }
}

data class Message(
    val message: String="",
    val senderId: String="",
    val senderName: String = "",
    val timestamp: Long,
    val imageUrl: String? = null
)