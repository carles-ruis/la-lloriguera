package com.carles.lalloriguera.ui.viewmodel

import MainDispatcherRule
import android.app.Activity
import com.carles.lalloriguera.R
import com.carles.lalloriguera.ui.viewmodel.SignInEvent
import com.carles.lalloriguera.ui.viewmodel.SignInState
import com.carles.lalloriguera.ui.viewmodel.SignInViewModel
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class SignInViewModelTest {

    @get:Rule
    val rule = MainDispatcherRule()

    private lateinit var viewModel: SignInViewModel

    @Test
    fun `given view model, when created, then set state to SigningIn and event to LaunchSignIn`() = runTest {
        viewModel = SignInViewModel()
        assertEquals(SignInState.SigningIn, viewModel.state.value)
        assertEquals(SignInEvent.LaunchSignIn, viewModel.event.first())
    }

    @Test
    fun `given onSignInResult, when result is ok, then send SignedIn event`() = runTest {
        val result = FirebaseAuthUIAuthenticationResult(Activity.RESULT_OK, null)
        viewModel = SignInViewModel()
        viewModel.onSignInResult(result)

        assertEquals(SignInEvent.LaunchSignIn, viewModel.event.first())
        assertEquals(SignInEvent.SignedIn, viewModel.event.first())
    }

    @Test
    fun `given onSignInResult, when result is not ok, then send SignInError event`() = runTest {
        val errorMessage = "Some error"
        val result = FirebaseAuthUIAuthenticationResult(
            Activity.RESULT_CANCELED,
            IdpResponse.from(Exception(errorMessage))
        )

        viewModel = SignInViewModel()
        viewModel.onSignInResult(result)

        val state = SignInState.SignInError(R.string.signin_error, errorMessage)
        assertEquals(state, viewModel.state.value)
    }

    @Test
    fun `given onRetry, when called, then retry launching sign in`() = runTest {
        viewModel = SignInViewModel()
        viewModel.onRetry()

        assertEquals(SignInState.SigningIn, viewModel.state.value)
        assertEquals(SignInEvent.LaunchSignIn, viewModel.event.first())
        assertEquals(SignInEvent.LaunchSignIn, viewModel.event.first())
    }
}