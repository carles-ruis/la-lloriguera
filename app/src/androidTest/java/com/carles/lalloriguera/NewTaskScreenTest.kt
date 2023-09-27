package com.carles.lalloriguera

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.carles.lalloriguera.ui.extensions.Tags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class NewTaskScreenTest {

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 0)
    val rule = HiltAndroidRule(this)

    @Test
    fun newTaskScreen_checkContent() {
        with (composeRule) {
            val title = activity.getString(R.string.new_task_title)
            val ok = activity.getString(R.string.ok)
            val cancel = activity.getString(R.string.cancel)
            val taskName = "Super Task"
            val periodicity = "4"

            // tasks screen
            onNodeWithTag(Tags.TASKS_LIST).assertDoesNotExist()
            onNodeWithTag(Tags.NEW_TASK_BUTTON).performClick()

            // new task screen
            onNodeWithTag(Tags.TOP_BAR_TITLE).assertTextEquals(title)

            // validation error
            onNodeWithTag(Tags.TASK_SAVE_BUTTON).performClick()
            onNodeWithTag(Tags.TASK_NAME_TEXT_FIELD).assertIsFocused()

            // fill periodic task form
            onNodeWithTag(Tags.TASK_NAME_TEXT_FIELD).performTextInput(taskName)
            onNodeWithTag(Tags.TASK_ONE_TIME_CONTENT).assertDoesNotExist()
            onNodeWithTag(Tags.TASK_PERIODIC_CONTENT).assertIsDisplayed()
            onNodeWithTag(Tags.TASK_PERIODICITY_TEXT_FIELD).assertTextContains("7")
            onNodeWithTag(Tags.TASK_PERIODICITY_MENU_BOX).performClick()
            onNodeWithText(periodicity).performClick()
            onNodeWithTag(Tags.TASK_PERIODICITY_TEXT_FIELD).assertTextContains(periodicity)
            onNodeWithTag(Tags.TASK_CALENDAR_PICKER_DIALOG).assertDoesNotExist()
            onNodeWithTag(Tags.TASK_CALENDAR_TEXT_FIELD).performClick()
            onNodeWithTag(Tags.TASK_CALENDAR_PICKER_DIALOG).assertIsDisplayed()
            onNodeWithText(ok).performClick()

            // fill one time task form
            onNodeWithTag(Tags.TASK_ONE_TIME_TASK_BUTTON).performClick()
            onNodeWithTag(Tags.TASK_ONE_TIME_CONTENT).assertIsDisplayed()
            onNodeWithTag(Tags.TASK_PERIODIC_CONTENT).assertDoesNotExist()
            onNodeWithTag(Tags.TASK_CALENDAR_PICKER_DIALOG).assertDoesNotExist()
            onNodeWithTag(Tags.TASK_ONE_TIME_CONTENT).performClick()
            onNodeWithTag(Tags.TASK_CALENDAR_PICKER_DIALOG).assertIsDisplayed()
            onNodeWithText(cancel).performClick()
            onNodeWithTag(Tags.TASK_SAVE_BUTTON).performClick()

            // tasks screen
            onNodeWithTag(Tags.TASKS_LIST).assertIsDisplayed()
            onNodeWithText(taskName).assertIsDisplayed()
        }
    }
}