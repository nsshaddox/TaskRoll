package com.nshaddox.randomtask.ui.components

import com.nshaddox.randomtask.domain.model.Priority
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class PriorityBadgeTest {
    @Test
    fun `priorityContainerColor returns tertiaryContainer color for LOW`() {
        val color = priorityContainerColor(Priority.LOW)
        assertEquals(LowPriorityColor, color)
    }

    @Test
    fun `priorityContainerColor returns amber color for MEDIUM`() {
        val color = priorityContainerColor(Priority.MEDIUM)
        assertEquals(MediumPriorityColor, color)
    }

    @Test
    fun `priorityContainerColor returns errorContainer color for HIGH`() {
        val color = priorityContainerColor(Priority.HIGH)
        assertEquals(HighPriorityColor, color)
    }

    @Test
    fun `priorityContainerColor returns distinct colors for all priority values`() {
        val lowColor = priorityContainerColor(Priority.LOW)
        val mediumColor = priorityContainerColor(Priority.MEDIUM)
        val highColor = priorityContainerColor(Priority.HIGH)

        assertNotEquals(lowColor, mediumColor)
        assertNotEquals(lowColor, highColor)
        assertNotEquals(mediumColor, highColor)
    }

    @Test
    fun `priorityOnContainerColor returns distinct colors for all priority values`() {
        val lowColor = priorityOnContainerColor(Priority.LOW)
        val mediumColor = priorityOnContainerColor(Priority.MEDIUM)
        val highColor = priorityOnContainerColor(Priority.HIGH)

        assertNotEquals(lowColor, mediumColor)
        assertNotEquals(lowColor, highColor)
        assertNotEquals(mediumColor, highColor)
    }

    @Test
    fun `priorityOnContainerColor returns correct colors for each priority`() {
        assertEquals(OnLowPriorityColor, priorityOnContainerColor(Priority.LOW))
        assertEquals(OnMediumPriorityColor, priorityOnContainerColor(Priority.MEDIUM))
        assertEquals(OnHighPriorityColor, priorityOnContainerColor(Priority.HIGH))
    }

    @Test
    fun `priorityLabelResId returns correct string resource for LOW`() {
        val resId = priorityLabelResId(Priority.LOW)
        assertEquals(com.nshaddox.randomtask.R.string.priority_low, resId)
    }

    @Test
    fun `priorityLabelResId returns correct string resource for MEDIUM`() {
        val resId = priorityLabelResId(Priority.MEDIUM)
        assertEquals(com.nshaddox.randomtask.R.string.priority_medium, resId)
    }

    @Test
    fun `priorityLabelResId returns correct string resource for HIGH`() {
        val resId = priorityLabelResId(Priority.HIGH)
        assertEquals(com.nshaddox.randomtask.R.string.priority_high, resId)
    }
}
