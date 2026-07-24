package com.segnities007.stylish_myvehicles.presentation.screen.maintenance

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ScheduleDueItemTest {

    @Test
    fun `isOverdue is true when daysRemaining is negative`() {
        val item = ScheduleDueItem("オイル交換", LocalDate.now(), daysRemaining = -5)
        assertTrue(item.isOverdue)
    }

    @Test
    fun `isOverdue is false when daysRemaining is zero`() {
        val item = ScheduleDueItem("オイル交換", LocalDate.now(), daysRemaining = 0)
        assertFalse(item.isOverdue)
    }

    @Test
    fun `isOverdue is false when daysRemaining is positive`() {
        val item = ScheduleDueItem("オイル交換", LocalDate.now(), daysRemaining = 10)
        assertFalse(item.isOverdue)
    }

    @Test
    fun `isDueSoon is true when daysRemaining is 0`() {
        val item = ScheduleDueItem("オイル交換", LocalDate.now(), daysRemaining = 0)
        assertTrue(item.isDueSoon)
    }

    @Test
    fun `isDueSoon is true when daysRemaining is 30`() {
        val item = ScheduleDueItem("オイル交換", LocalDate.now(), daysRemaining = 30)
        assertTrue(item.isDueSoon)
    }

    @Test
    fun `isDueSoon is false when daysRemaining is 31`() {
        val item = ScheduleDueItem("オイル交換", LocalDate.now(), daysRemaining = 31)
        assertFalse(item.isDueSoon)
    }

    @Test
    fun `isDueSoon is false when daysRemaining is negative`() {
        val item = ScheduleDueItem("オイル交換", LocalDate.now(), daysRemaining = -1)
        assertFalse(item.isDueSoon)
    }

    @Test
    fun `isDueSoon is true when daysRemaining is 15`() {
        val item = ScheduleDueItem("タイヤ交換", LocalDate.now(), daysRemaining = 15)
        assertTrue(item.isDueSoon)
    }
}
