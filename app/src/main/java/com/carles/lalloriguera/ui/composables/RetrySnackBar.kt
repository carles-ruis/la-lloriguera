package com.carles.lalloriguera.ui.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.carles.lalloriguera.R

@Composable
fun RetrySnackBar(@StringRes resId: Int, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    RetrySnackBar(stringResource(resId), onRetry, modifier)
}

@Composable
fun RetrySnackBar(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    val snackbarHostState = remember { SnackbarHostState() }
    val retryText = stringResource(R.string.retry)

    Box(modifier = modifier.fillMaxSize()) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { snackbarData ->
            Snackbar(snackbarData, actionOnNewLine = true)
        }
    }
    LaunchedEffect(snackbarHostState) {
        snackbarHostState.showSnackbar(message, retryText)
        onRetry()
    }
}