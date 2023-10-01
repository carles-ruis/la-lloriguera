package com.carles.lalloriguera.ui.composables

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.carles.lalloriguera.R
import com.carles.lalloriguera.ui.extensions.LightDarkPreviews
import com.carles.lalloriguera.ui.extensions.Tags.TOP_BAR_TITLE
import com.carles.lalloriguera.ui.theme.LlorigueraTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LlorigueraTopBar(
    @StringRes title: Int,
    showUpButton: Boolean,
    modifier: Modifier = Modifier,
    onUpClick: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            AnimatedContent(
                targetState = stringResource(title),
                transitionSpec = { fadeIn(tween()) togetherWith fadeOut(tween()) },
                label = "AnimatedContent:title"
            ) { target ->
                Text(
                    text = target,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.testTag(TOP_BAR_TITLE)
                )
            }
        },
        modifier = modifier,
        navigationIcon = {
            if (showUpButton) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    modifier = Modifier.clickable { onUpClick() })
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(MaterialTheme.colorScheme.secondaryContainer)
    )
}

@LightDarkPreviews
@Composable
private fun LlorigueraTopBar_ShowUpButton() {
    LlorigueraTheme {
        LlorigueraTopBar(title = R.string.new_task_title, showUpButton = true)
    }
}

@LightDarkPreviews
@Composable
private fun LlorigueraTopBar_NoShowUpButton() {
    LlorigueraTheme {
        LlorigueraTopBar(title = R.string.tasks_title, showUpButton = false)
    }
}