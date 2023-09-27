package com.carles.lalloriguera.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.carles.lalloriguera.R
import com.carles.lalloriguera.ui.composables.CenteredProgressIndicator
import com.carles.lalloriguera.ui.composables.RetrySnackBar
import com.carles.lalloriguera.ui.theme.LlorigueraTheme
import com.carles.lalloriguera.ui.viewmodel.SignInEvent
import com.carles.lalloriguera.ui.viewmodel.SignInState
import com.carles.lalloriguera.ui.viewmodel.SignInViewModel
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SignInScreen(
    viewModel: SignInViewModel,
    onSignedIn: () -> Unit,
) {
    val signInLauncher = rememberLauncherForActivityResult(FirebaseAuthUIActivityResultContract()) { result ->
        viewModel.onSignInResult(result)
    }
    val launchSignIn = { viewModel.launch(signInLauncher) }

    val eventFlow = viewModel.event
    SignInEventHandler(eventFlow, launchSignIn, onSignedIn)

    val state: SignInState by viewModel.state.collectAsStateWithLifecycle()
    state.run {
        when (this) {
            SignInState.SigningIn -> CenteredProgressIndicator(message = R.string.signin_message)
            is SignInState.SignInError -> RetrySnackBar(stringResource(messageId, message), onRetry = { viewModel.onRetry() })
        }
    }
}

@Composable
private fun SignInEventHandler(eventFlow: Flow<SignInEvent>, launchSignIn: () -> Unit, onSignedIn: () -> Unit) {
    LaunchedEffect(Unit) {
        eventFlow.collectLatest { event ->
            when (event) {
                SignInEvent.LaunchSignIn -> launchSignIn()
                SignInEvent.SignedIn -> onSignedIn()
            }
        }
    }
}

@Composable
@Preview
private fun CenteredProgressIndicator_SigningIn() {
    LlorigueraTheme {
        CenteredProgressIndicator(message = R.string.signin_message)
    }
}
