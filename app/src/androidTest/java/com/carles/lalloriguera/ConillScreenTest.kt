package com.carles.lalloriguera

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.printToLog
import com.carles.lalloriguera.ui.extensions.Tags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ConillScreenTest {

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 0)
    val rule = HiltAndroidRule(this)

    @Test
    fun conillScreen_checkContent() {
        with(composeRule) {
            val title = activity.getString(R.string.conill_title)
            val ok = activity.getString(R.string.ok)
            val taskName = "Conill Task"
            val conillImageDescription = activity.getString(R.string.conill_image)
            val conillText = activity.getString(R.string.conill_text)

            // tasks screen
            onNodeWithTag(Tags.TASKS_LIST).assertDoesNotExist()
            waitForIdle()
            onNodeWithTag(Tags.NEW_TASK_BUTTON).performClick()

            // new task screen: create delayed task
            onNodeWithTag(Tags.TASK_NAME_TEXT_FIELD).performTextInput(taskName)
            onNodeWithTag(Tags.TASK_CALENDAR_TEXT_FIELD).performClick()
            onNodeWithTag(Tags.TASK_CALENDAR_PICKER_DIALOG, useUnmergedTree = false)
                .printToLog("conillScreen_checkContent:TASK_CALENDAR_PICKER_DIALOG")

            onNode(SemanticsMatcher.keyIsDefined(SemanticsProperties.CollectionInfo)).onChildAt(0).performClick(); onNodeWithText(ok).performClick()
            onNodeWithTag(Tags.TASK_PERIODICITY_MENU_BOX).performClick()
            onNodeWithText("1").performClick()
            onNodeWithTag(Tags.TASK_SAVE_BUTTON).performClick()

            // tasks screen
            onNodeWithTag(Tags.TASKS_LIST).assertIsDisplayed()
            onNodeWithText(taskName).assertIsDisplayed()
            onNodeWithTag(Tags.TASK_DONE_ICON).performClick()

            // conill screen
            onNodeWithText(title).assertIsDisplayed()
            onNodeWithContentDescription(conillImageDescription).assertIsDisplayed()
            onNodeWithText(conillText).assertIsDisplayed()
            onNodeWithTag(Tags.CONILL_OK_BUTTON).performClick()

            // tasks screen
            onNodeWithTag(Tags.TASKS_LIST).assertIsDisplayed()
        }
    }
}