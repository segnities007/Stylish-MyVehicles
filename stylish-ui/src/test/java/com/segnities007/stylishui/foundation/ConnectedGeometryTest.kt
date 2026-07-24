package com.segnities007.stylishui.foundation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ConnectedGeometryTest {
    @Test
    fun `standalone item exposes every corner`() {
        assertEquals(ConnectedCorners.Standalone, connectedGridCorners(0, 1, 2))
    }

    @Test
    fun `full grid exposes only its four outside corners`() {
        assertEquals(ConnectedCorners(topStart = true), connectedGridCorners(0, 4, 2))
        assertEquals(ConnectedCorners(topEnd = true), connectedGridCorners(1, 4, 2))
        assertEquals(ConnectedCorners(bottomStart = true), connectedGridCorners(2, 4, 2))
        assertEquals(ConnectedCorners(bottomEnd = true), connectedGridCorners(3, 4, 2))
    }

    @Test
    fun `incomplete row exposes corners facing empty cells`() {
        assertEquals(
            ConnectedCorners(topEnd = true, bottomEnd = true),
            connectedGridCorners(1, 3, 2),
        )
        assertEquals(
            ConnectedCorners(bottomStart = true, bottomEnd = true),
            connectedGridCorners(2, 3, 2),
        )
    }

    @Test
    fun `five items in three columns expose corners adjacent to empty cells`() {
        assertEquals(ConnectedCorners(topStart = true), connectedGridCorners(0, 5, 3))
        assertEquals(ConnectedCorners(), connectedGridCorners(1, 5, 3))
        assertEquals(
            ConnectedCorners(topEnd = true, bottomEnd = true),
            connectedGridCorners(2, 5, 3),
        )
        assertEquals(ConnectedCorners(bottomStart = true), connectedGridCorners(3, 5, 3))
        assertEquals(ConnectedCorners(bottomEnd = true), connectedGridCorners(4, 5, 3))
    }

    @Test
    fun `row and column geometry expose only their outer corners`() {
        assertEquals(
            ConnectedCorners(topStart = true, bottomStart = true),
            connectedRowCorners(0, 2),
        )
        assertEquals(
            ConnectedCorners(topEnd = true, bottomEnd = true),
            connectedRowCorners(1, 2),
        )
        assertEquals(
            ConnectedCorners(topStart = true, topEnd = true),
            connectedColumnCorners(0, 2),
        )
        assertEquals(
            ConnectedCorners(bottomStart = true, bottomEnd = true),
            connectedColumnCorners(1, 2),
        )
    }

    @Test
    fun `invalid geometry is rejected at the public boundary`() {
        assertThrows(IllegalArgumentException::class.java) {
            connectedGridCorners(index = 0, size = 1, columns = 0)
        }
        assertThrows(IllegalArgumentException::class.java) {
            connectedGridCorners(index = 1, size = 1, columns = 1)
        }
    }
}
