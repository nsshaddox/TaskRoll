package com.nshaddox.randomtask.ui.theme

import com.nshaddox.randomtask.domain.model.ThemeVariant
import org.junit.Assert.assertEquals
import org.junit.Test

class AnimationTokensTest {
    @Test
    fun `animationDurationMs returns 200 for OBSIDIAN`() {
        assertEquals(200, animationDurationMs(ThemeVariant.OBSIDIAN))
    }

    @Test
    fun `animationDurationMs returns 250 for NEO_BRUTALIST`() {
        assertEquals(250, animationDurationMs(ThemeVariant.NEO_BRUTALIST))
    }

    @Test
    fun `animationDurationMs returns 300 for VAPOR`() {
        assertEquals(300, animationDurationMs(ThemeVariant.VAPOR))
    }

    @Test
    fun `NAV_TRANSITION_DURATION_MS is 300`() {
        assertEquals(300, NAV_TRANSITION_DURATION_MS)
    }
}
