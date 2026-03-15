package com.nshaddox.randomtask.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class SortOrderTest {
    @Test
    fun `SortOrder has four values`() {
        val values = SortOrder.entries
        assertEquals(4, values.size)
    }

    @Test
    fun `SortOrder values are in expected order`() {
        val values = SortOrder.entries
        assertEquals(SortOrder.CREATED_DATE_DESC, values[0])
        assertEquals(SortOrder.DUE_DATE_ASC, values[1])
        assertEquals(SortOrder.PRIORITY_DESC, values[2])
        assertEquals(SortOrder.TITLE_ASC, values[3])
    }

    @Test
    fun `valueOf CREATED_DATE_DESC returns CREATED_DATE_DESC`() {
        assertEquals(SortOrder.CREATED_DATE_DESC, SortOrder.valueOf("CREATED_DATE_DESC"))
    }

    @Test
    fun `valueOf DUE_DATE_ASC returns DUE_DATE_ASC`() {
        assertEquals(SortOrder.DUE_DATE_ASC, SortOrder.valueOf("DUE_DATE_ASC"))
    }

    @Test
    fun `valueOf PRIORITY_DESC returns PRIORITY_DESC`() {
        assertEquals(SortOrder.PRIORITY_DESC, SortOrder.valueOf("PRIORITY_DESC"))
    }

    @Test
    fun `valueOf TITLE_ASC returns TITLE_ASC`() {
        assertEquals(SortOrder.TITLE_ASC, SortOrder.valueOf("TITLE_ASC"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `valueOf invalid name throws IllegalArgumentException`() {
        SortOrder.valueOf("INVALID")
    }
}
