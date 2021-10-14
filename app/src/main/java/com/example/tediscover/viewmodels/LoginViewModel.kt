package com.example.tediscover.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.example.tediscover.firebase.FirebaseUserLiveData

class LoginViewModel : ViewModel() {


    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }


    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }
}