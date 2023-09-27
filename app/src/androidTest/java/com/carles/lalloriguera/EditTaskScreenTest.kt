package com.carles.lalloriguera

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.carles.lalloriguera.ui.extensions.Tags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class EditTaskScreenTest {

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 0)
    val rule = HiltAndroidRule(this)

    @Test
    fun editTaskScreen_checkContent() {
        with (composeRule) {
            val title = activity.getString(R.string.edit_task_title)
            val taskName = "Giga Task"
            val newTaskName = "Mega Task"
            val yes = activity.getString(R.string.yes)

            // tasks screen
            onNodeWithTag(Tags.TASKS_LIST).assertDoesNotExist()
            onNodeWithTag(Tags.NEW_TASK_BUTTON).performClick()

            // new task screen
            onNodeWithTag(Tags.TASK_NAME_TEXT_FIELD).performTextInput(taskName)
            onNodeWithTag(Tags.TASK_SAVE_BUTTON).performClick()

            // tasks screen
            onNodeWithText(taskName).assertIsDisplayed()
            waitForIdle()
            onNodeWithTag(Tags.TASK_EDIT_ICON).performClick()

            // edit task screen: edit task
            onNodeWithText(title).assertIsDisplayed()
            onNodeWithTag(Tags.TASK_NAME_TEXT_FIELD).performTextClearance()
            onNodeWithTag(Tags.TASK_NAME_TEXT_FIELD).performTextInput(newTaskName)
            onNodeWithTag(Tags.TASK_UPDATE_BUTTON).performClick()

            // tasks screen
            onNodeWithText(taskName).assertDoesNotExist()
            onNodeWithText(newTaskName).assertIsDisplayed()
            onNodeWithTag(Tags.TASK_EDIT_ICON).performClick()

            // edit task screen: delete task
            onNodeWithTag(Tags.TASK_DELETE_CONFIRMATION_DIALOG).assertDoesNotExist()
            onNodeWithTag(Tags.TASK_DELETE_BUTTON).performClick()
            onNodeWithTag(Tags.TASK_DELETE_CONFIRMATION_DIALOG).assertIsDisplayed()
            onNodeWithText(yes).performClick()

            // tasks screen
            onNodeWithText(newTaskName).assertDoesNotExist()
        }
    }
}
