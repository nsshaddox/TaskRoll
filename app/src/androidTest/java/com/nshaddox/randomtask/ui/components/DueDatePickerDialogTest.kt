package com.nshaddox.randomtask.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class DueDatePickerDialogTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun confirmButton_invokesOnConfirm_withSelectedDate() {
        var confirmedDate: LocalDate? = LocalDate.MAX // sentinel
        composeTestRule.setContent {
            DueDatePickerDialog(
                initialDate = LocalDate.of(2026, 3, 14),
                onConfirm = { confirmedDate = it },
                onDismiss = {},
            )
        }

        composeTestRule.onNodeWithText("Confirm").performClick()

        // The date picker should have the initial date pre-selected
        assertTrue(
            "onConfirm should have been invoked with a non-sentinel date",
            confirmedDate != LocalDate.MAX,
        )
    }

    @Test
    fun clearButton_invokesOnConfirm_withNull() {
        var confirmCalled = false
        var confirmedDate: LocalDate? = LocalDate.MAX // sentinel
        composeTestRule.setContent {
            DueDatePickerDialog(
                initialDate = LocalDate.of(2026, 3, 14),
                onConfirm = {
                    confirmCalled = true
                    confirmedDate = it
                },
                onDismiss = {},
            )
        }

        composeTestRule.onNodeWithText("Clear").performClick()

        assertTrue("onConfirm should have been called", confirmCalled)
        assertNull("Date should be null when Clear is tapped", confirmedDate)
    }

    @Test
    fun cancelButton_invokesOnDismiss() {
        var dismissCalled = false
        composeTestRule.setContent {
            DueDatePickerDialog(
                initialDate = null,
                onConfirm = {},
                onDismiss = { dismissCalled = true },
            )
        }

        composeTestRule.onNodeWithText("Cancel").performClick()

        assertTrue("onDismiss should have been called", dismissCalled)
    }

    @Test
    fun dialog_displaysAllButtons() {
        composeTestRule.setContent {
            DueDatePickerDialog(
                initialDate = null,
                onConfirm = {},
                onDismiss = {},
            )
        }

        composeTestRule.onNodeWithText("Confirm").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clear").assertIsDisplayed()
    }

    @Test
    fun confirmButton_withNoInitialDate_invokesOnConfirmWithTodaysDate() {
        var confirmedDate: LocalDate? = null
        composeTestRule.setContent {
            DueDatePickerDialog(
                initialDate = null,
                onConfirm = { confirmedDate = it },
                onDismiss = {},
            )
        }

        composeTestRule.onNodeWithText("Confirm").performClick()

        // When no initial date is set, the default DatePicker selection is today
        assertEquals(
            "Default date should be today when no initial date is set",
            LocalDate.now(),
            confirmedDate,
        )
    }

    @Test
    fun confirmButton_withInitialDate_returnsInitialDate() {
        val initialDate = LocalDate.of(2026, 6, 15)
        var confirmedDate: LocalDate? = null
        composeTestRule.setContent {
            DueDatePickerDialog(
                initialDate = initialDate,
                onConfirm = { confirmedDate = it },
                onDismiss = {},
            )
        }

        composeTestRule.onNodeWithText("Confirm").performClick()

        assertEquals(
            "Confirming without changing selection should return the initial date",
            initialDate,
            confirmedDate,
        )
    }
}
