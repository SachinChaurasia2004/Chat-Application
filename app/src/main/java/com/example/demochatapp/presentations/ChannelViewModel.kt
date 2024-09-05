package com.example.demochatapp.presentations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChannelViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
): ViewModel() {

    init {
        viewModelScope.launch {
            getChannels()
        }
    }


    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels = _channels.asStateFlow()

    fun createChannel(channelName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val channel = hashMapOf(
                    "name" to channelName
                )
                fireStore.collection("channels").add(channel)
                    .addOnSuccessListener {
                        viewModelScope.launch {
                            getChannels()
                        }
                    }
            }
        }
    }

    private suspend fun getChannels() {
        withContext(Dispatchers.IO) {
            fireStore.collection("channels").get()
                .addOnSuccessListener {
                    val list = mutableListOf<Channel>()
                    it.forEach { data ->
                        val channel = Channel(data.id, data.data["name"].toString())
                        list.add(channel)
                    }
                    _channels.value = list
                }
        }

    }

}

data class Channel(
    val channelId: String,
    val name: String
)