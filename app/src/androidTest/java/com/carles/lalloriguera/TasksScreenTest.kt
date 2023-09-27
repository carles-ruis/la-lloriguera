package com.carles.lalloriguera

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.carles.lalloriguera.ui.extensions.Tags.EMPTY_TASKS_BOX
import com.carles.lalloriguera.ui.extensions.Tags.NEW_TASK_BUTTON
import com.carles.lalloriguera.ui.extensions.Tags.TASKS_LIST
import com.carles.lalloriguera.ui.extensions.Tags.TASK_DONE_ICON
import com.carles.lalloriguera.ui.extensions.Tags.TASK_NAME_TEXT_FIELD
import com.carles.lalloriguera.ui.extensions.Tags.TASK_ONE_TIME_TASK_BUTTON
import com.carles.lalloriguera.ui.extensions.Tags.TASK_PERIODICITY_MENU_BOX
import com.carles.lalloriguera.ui.extensions.Tags.TASK_SAVE_BUTTON
import com.carles.lalloriguera.ui.extensions.Tags.TOP_BAR_TITLE
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class TasksScreenTest {

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 0)
    val rule = HiltAndroidRule(this)

    @Test
    fun tasksScreen_checkContent() {
        with(composeRule) {
            val title = activity.getString(R.string.tasks_title)
            val oneTimeTask = "Task one time"
            val periodicTask = "Task periodic"
            val periodicity = "2"
            val oneTimeTaskInfo = activity.getString(R.string.tasks_one_time)

            // check empty tasks list
            onNodeWithTag(TOP_BAR_TITLE).assertTextEquals(title)
            onNodeWithTag(EMPTY_TASKS_BOX).assertIsDisplayed()
            onNodeWithTag(TASKS_LIST).assertDoesNotExist()

            // create tasks
            onNodeWithTag(NEW_TASK_BUTTON).performClick()
            onNodeWithTag(TASK_NAME_TEXT_FIELD).performTextInput(oneTimeTask)
            onNodeWithTag(TASK_ONE_TIME_TASK_BUTTON).performClick()
            onNodeWithTag(TASK_SAVE_BUTTON).performClick()

            onNodeWithTag(NEW_TASK_BUTTON).performClick()
            onNodeWithTag(TASK_NAME_TEXT_FIELD).performTextInput(periodicTask)
            onNodeWithTag(TASK_PERIODICITY_MENU_BOX).performClick()
            onNodeWithText(periodicity).performClick()
            onNodeWithTag(TASK_SAVE_BUTTON).performClick()

            // check tasks list content
            onNodeWithTag(TASKS_LIST, useUnmergedTree = true).apply {
                assertIsDisplayed()
                onChildAt(0).assert(hasAnyChild(hasText(periodicTask)))
                onChildAt(0).assert(hasAnyChild(hasText(periodicity)))
                onChildAt(1).assert(hasAnyChild(hasText(oneTimeTask)))
                onChildAt(1).assert(hasAnyChild(hasText(oneTimeTaskInfo)))
            }

            // mark tasks as done
            onNodeWithText(oneTimeTask).assertIsDisplayed()
            onNode(hasTestTag(TASK_DONE_ICON) and hasAnySibling(hasText(oneTimeTask))).performClick()
            waitForIdle()
            onNodeWithText(oneTimeTask).assertIsNotDisplayed()

            onNodeWithText(periodicTask).assertIsDisplayed()
            onNode(hasTestTag(TASK_DONE_ICON) and hasAnySibling(hasText(periodicTask))).performClick()
            onNodeWithText(periodicTask).assertIsDisplayed()
        }
    }
}