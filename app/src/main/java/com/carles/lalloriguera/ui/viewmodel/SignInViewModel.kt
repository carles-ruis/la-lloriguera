package com.carles.lalloriguera.ui.viewmodel

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carles.lalloriguera.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SignInState {
    object SigningIn : SignInState()
    data class SignInError(@StringRes val messageId: Int, val message: String) : SignInState()
}

sealed class SignInEvent {
    object LaunchSignIn : SignInEvent()
    object SignedIn : SignInEvent()
}

@HiltViewModel
class SignInViewModel @Inject constructor() : ViewModel() {

    private var _state = MutableStateFlow<SignInState>(SignInState.SigningIn)
    val state: StateFlow<SignInState> = _state

    private var _event = Channel<SignInEvent>(Channel.BUFFERED)
    val event: Flow<SignInEvent> = _event.receiveAsFlow()


    init {
        launchSignIn()
    }

    private fun launchSignIn() {
        viewModelScope.launch {
            _state.value = SignInState.SigningIn
            _event.send(SignInEvent.LaunchSignIn)
        }
    }

    fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        viewModelScope.launch {
            if (result.resultCode == Activity.RESULT_OK) {
                _event.send(SignInEvent.SignedIn)
            } else {
                val idpResponseError: String = result.idpResponse?.let { it.error?.localizedMessage } ?: ""
                _state.value = SignInState.SignInError(R.string.signin_error, idpResponseError)
            }
        }
    }

    fun onRetry() {
        launchSignIn()
    }

    fun launch(launcher: ManagedActivityResultLauncher<Intent, FirebaseAuthUIAuthenticationResult>) {
        launcher.launch(
            AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setLogo(R.mipmap.ic_launcher)
            .setAvailableProviders(listOf(AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()))
            .build())
    }
}