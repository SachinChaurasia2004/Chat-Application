package com.example.demochatapp.presentations

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore
) : ViewModel() {

    private val _loadingState = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> = _loadingState

    private val _authState = MutableStateFlow<FirebaseUser?>(null)
    val authState: StateFlow<FirebaseUser?> = _authState

    init {
        _authState.value = auth.currentUser
    }

    fun signUp(name: String, email: String, number: String, password: String) {

        _loadingState.value = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                _loadingState.value = false
                if (task.isSuccessful) {
                    Log.d("TAG", "signUp: Success")
                    _authState.value = auth.currentUser
                    val user = auth.currentUser
                    user?.let {
                        it.updateProfile(
                            com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build()
                        )
                        addUserToFireStore(it.uid, name, email, number)
                    }
                } else {
                    Log.d("TAG", "signUp: Failed")
                    _authState.value = null
                }
            }
    }

    fun signIn(email: String, password: String) {

        _loadingState.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _loadingState.value = false
                if (task.isSuccessful) {
                    Log.d("TAG", "signIn: Success")
                    val user = auth.currentUser
                    _authState.value = user
                } else {
                    Log.d("TAG", "signIn: Failed")
                    _authState.value = null
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = null
        Log.d("TAG", "signOut: Success")
    }

    private fun addUserToFireStore(userId: String, name: String, email: String, number: String) {

        val user = hashMapOf(
            "name" to name,
            "email" to email,
            "number" to number
        )
        fireStore.collection("users").document(userId).set(user)
            .addOnSuccessListener {
                Log.d("TAG", "addUserToFireStore: Success")
            }
            .addOnFailureListener {
                Log.d("TAG", "addUserToFireStore: Failed")
            }
    }

}