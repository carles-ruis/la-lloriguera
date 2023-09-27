package com.carles.lalloriguera.ui.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carles.lalloriguera.R
import com.carles.lalloriguera.ui.theme.LlorigueraTheme

@Composable
fun CenteredProgressIndicator(modifier: Modifier = Modifier, @StringRes message: Int? = null) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (message != null) {
                Text(
                    text = stringResource(message),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            CircularProgressIndicator()
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun CenteredProgressIndicator_WithoutTitle() {
    LlorigueraTheme {
        CenteredProgressIndicator()
    }
}
