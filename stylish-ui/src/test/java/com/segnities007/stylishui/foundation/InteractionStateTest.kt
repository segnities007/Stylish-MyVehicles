package com.segnities007.stylishui.foundation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InteractionStateTest {
    @Test
    fun `enabled component with click action is actionable`() {
        assertTrue(isActionable(hasClickAction = true))
    }

    @Test
    fun `enabled component with only long click action is actionable`() {
        assertTrue(
            isActionable(
                hasClickAction = false,
                hasLongClickAction = true,
            ),
        )
    }

    @Test
    fun `component without an action is not actionable`() {
        assertFalse(isActionable(hasClickAction = false))
    }

    @Test
    fun `disabled component is never actionable`() {
        assertFalse(
            isActionable(
                enabled = false,
                hasClickAction = true,
                hasLongClickAction = true,
            ),
        )
    }
}
