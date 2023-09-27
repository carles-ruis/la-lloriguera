package com.carles.lalloriguera

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import com.carles.lalloriguera.ui.extensions.Tags.NEW_TASK_BUTTON
import com.carles.lalloriguera.ui.extensions.Tags.TASK_NAME_TEXT_FIELD
import com.carles.lalloriguera.ui.extensions.Tags.TOP_BAR_TITLE
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MainActivityTest {

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @get:Rule(order = 0)
    val rule = HiltAndroidRule(this)

    @Test
    fun mainActivity_navigation() {
        with(composeRule) {
            val tasksTitle = activity.getString(R.string.tasks_title)
            val newTaskTitle = activity.getString(R.string.new_task_title)
            val editTaskTitle = activity.getString(R.string.edit_task_title)

            val newTaskSaveText = activity.getString(R.string.new_task_save)
            val editIconDescription = activity.getString(R.string.tasks_edit)
            val backDescription = activity.getString(R.string.back)
            val taskName = "menja llaminadures"

            onRoot(useUnmergedTree = true).printToLog("mainActivity_navigation")
            // tasks screen
            onNodeWithTag(TOP_BAR_TITLE).assertTextEquals(tasksTitle)
            onNodeWithTag(NEW_TASK_BUTTON).performClick()
            // new task screen
            onNodeWithTag(TOP_BAR_TITLE).assertTextEquals(newTaskTitle)
            onNodeWithTag(TASK_NAME_TEXT_FIELD).performTextInput(taskName)
            onNodeWithText(newTaskSaveText).performClick()
            // tasks screen
            onNodeWithTag(TOP_BAR_TITLE).assertTextEquals(tasksTitle)
            onNode(hasContentDescription(editIconDescription) and hasAnySibling(hasText(taskName))).performClick()
            // edit task screen
            onNodeWithTag(TOP_BAR_TITLE).assertTextEquals(editTaskTitle)
            onNodeWithContentDescription(backDescription).performClick()
            // tasks screen
            onNodeWithTag(TOP_BAR_TITLE).assertTextEquals(tasksTitle)
            // leave app
            activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
            val isResumed = activityRule.scenario.state == Lifecycle.State.RESUMED
            val isCreated = activityRule.scenario.state == Lifecycle.State.CREATED
            assertTrue(isResumed || isCreated)
        }
    }

}