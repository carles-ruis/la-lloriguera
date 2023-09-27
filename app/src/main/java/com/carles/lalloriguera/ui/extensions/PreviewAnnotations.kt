package com.carles.lalloriguera.ui.extensions

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "light", uiMode = UI_MODE_NIGHT_NO, showBackground = true)
@Preview(name = "dark", uiMode = UI_MODE_NIGHT_YES, showBackground = true)
annotation class LightDarkPreviews


@Preview(name = "landscape", device = Devices.AUTOMOTIVE_1024p, widthDp = 720, heightDp = 360)
@Preview(name = "portrait")
annotation class OrientationPreviews