package com.nshaddox.randomtask.ui.screens.tasklist

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.nshaddox.randomtask.domain.model.Priority
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class EditTaskDialogTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createTestTask(
        id: Long = 1L,
        title: String = "Test Task",
        description: String? = "Test Description",
        priority: Priority = Priority.HIGH,
        dueDateLabel: String? = "Mar 14, 2026",
    ) = TaskUiModel(
        id = id,
        title = title,
        description = description,
        isCompleted = false,
        createdAt = "Jan 1, 2025 12:00 AM",
        updatedAt = "Jan 1, 2025 12:00 AM",
        priority = priority,
        priorityLabel =
            when (priority) {
                Priority.HIGH -> "High"
                Priority.MEDIUM -> "Medium"
                Priority.LOW -> "Low"
            },
        dueDateLabel = dueDateLabel,
    )

    @Test
    fun confirmButton_isDisabled_whenTitleIsBlank() {
        composeTestRule.setContent {
            EditTaskDialog(
                task = null,
                onConfirm = { _, _, _, _, _ -> },
                onDismiss = {},
            )
        }

        composeTestRule.onNodeWithText("Add").assertIsNotEnabled()
    }

    @Test
    fun confirmButton_isEnabled_afterEnteringTitle() {
        composeTestRule.setContent {
            EditTaskDialog(
                task = null,
                onConfirm = { _, _, _, _, _ -> },
                onDismiss = {},
            )
        }

        composeTestRule.onNodeWithText("Task title").performTextInput("My Task")
        composeTestRule.onNodeWithText("Add").assertIsEnabled()
    }

    @Test
    fun addMode_fieldsInitializeToDefaults() {
        composeTestRule.setContent {
            EditTaskDialog(
                task = null,
                onConfirm = { _, _, _, _, _ -> },
                onDismiss = {},
            )
        }

        // Dialog should show "Add Task" title
        composeTestRule.onNodeWithText("Add Task").assertExists()
        // Add button should exist (not Save)
        composeTestRule.onNodeWithText("Add").assertExists()
        // Medium priority should be selected by default
        composeTestRule.onNodeWithText("Medium").assertExists()
        // Due date should show placeholder
        composeTestRule.onNodeWithText("No due date").assertExists()
    }

    @Test
    fun editMode_fieldsPrePopulatedFromTask() {
        val task =
            createTestTask(
                title = "Existing Task",
                description = "Existing Description",
                priority = Priority.HIGH,
            )

        composeTestRule.setContent {
            EditTaskDialog(
                task = task,
                initialDueDate = LocalDate.of(2026, 3, 14),
                onConfirm = { _, _, _, _, _ -> },
                onDismiss = {},
            )
        }

        // Dialog should show "Edit Task" title
        composeTestRule.onNodeWithText("Edit Task").assertExists()
        // Save button (not Add) should exist
        composeTestRule.onNodeWithText("Save").assertExists()
        // Title should be pre-populated
        composeTestRule.onNodeWithText("Existing Task").assertExists()
        // Description should be pre-populated
        composeTestRule.onNodeWithText("Existing Description").assertExists()
        // High priority should be shown
        composeTestRule.onNodeWithText("High").assertExists()
    }

    @Test
    fun onConfirm_invokedWithCorrectValues_whenConfirmTapped() {
        var confirmedTitle = ""
        var confirmedDescription: String? = null
        var confirmedPriority = Priority.MEDIUM
        var confirmedDueDate: LocalDate? = null
        var confirmedCategory: String? = null

        composeTestRule.setContent {
            EditTaskDialog(
                task = null,
                onConfirm = { title, desc, priority, dueDate, category ->
                    confirmedTitle = title
                    confirmedDescription = desc
                    confirmedPriority = priority
                    confirmedDueDate = dueDate
                    confirmedCategory = category
                },
                onDismiss = {},
            )
        }

        composeTestRule.onNodeWithText("Task title").performTextInput("New Task")
        composeTestRule.onNodeWithText("Description (optional)").performTextInput("Some details")
        composeTestRule.onNodeWithText("High").performClick()
        composeTestRule.onNodeWithText("Add").performClick()

        assertEquals("New Task", confirmedTitle)
        assertEquals("Some details", confirmedDescription)
        assertEquals(Priority.HIGH, confirmedPriority)
        assertNull(confirmedDueDate)
        assertNull(confirmedCategory)
    }

    @Test
    fun onDismiss_invokedWhenCancelTapped() {
        var dismissCalled = false

        composeTestRule.setContent {
            EditTaskDialog(
                task = null,
                onConfirm = { _, _, _, _, _ -> },
                onDismiss = { dismissCalled = true },
            )
        }

        composeTestRule.onNodeWithText("Cancel").performClick()

        assertTrue("onDismiss should have been called", dismissCalled)
    }

    @Test
    fun dueDateField_tapOpensDueDatePickerDialog() {
        composeTestRule.setContent {
            EditTaskDialog(
                task = null,
                onConfirm = { _, _, _, _, _ -> },
                onDismiss = {},
            )
        }

        // Tap the due date field
        composeTestRule.onNodeWithText("No due date").performClick()

        // DueDatePickerDialog should now be visible with its buttons
        composeTestRule.onNodeWithText("Confirm").assertExists()
        composeTestRule.onNodeWithText("Clear").assertExists()
    }

    @Test
    fun editMode_confirmButtonShowsSave() {
        val task = createTestTask()

        composeTestRule.setContent {
            EditTaskDialog(
                task = task,
                initialDueDate = LocalDate.of(2026, 3, 14),
                onConfirm = { _, _, _, _, _ -> },
                onDismiss = {},
            )
        }

        composeTestRule.onNodeWithText("Save").assertIsEnabled()
    }
}
