package com.carles.lalloriguera.ui.composables

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun SupportingText(@StringRes resId: Int) {
    SupportingText(stringResource(resId))
}

@Composable
fun SupportingText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.outline
    )
}

@Composable
fun AnimatedSupportingText(@StringRes resId: Int, label: String = "AnimatedSupportingText") {
    AnimatedSupportingText(stringResource(resId), label)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedSupportingText(text: String, label: String = "AnimatedSupportingText") {
    AnimatedContent(
        targetState = text,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = label
    ) { target ->
        SupportingText(text = target)
    }
}