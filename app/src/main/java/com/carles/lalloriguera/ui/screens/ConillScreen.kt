package com.carles.lalloriguera.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.carles.lalloriguera.R
import com.carles.lalloriguera.ui.extensions.Tags
import com.carles.lalloriguera.ui.theme.LlorigueraTheme

@Composable
fun ConillScreen(navigateUp: () -> Unit = {}) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.kissingrabbits4),
            contentDescription = stringResource(R.string.conill_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
        )
        Text(text = stringResource(id = R.string.conill_text))
        ElevatedButton(
            onClick = navigateUp,
            modifier = Modifier.testTag(Tags.CONILL_OK_BUTTON)
        ) {
            Text(stringResource(id = R.string.ok))
        }
    }
}

@Composable
@Preview
private fun ConillScreenPreview() {
    LlorigueraTheme {
        Box(Modifier.fillMaxSize()) {
            ConillScreen()
        }
    }
}