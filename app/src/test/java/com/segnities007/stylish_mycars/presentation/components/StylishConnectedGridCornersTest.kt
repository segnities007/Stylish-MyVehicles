package com.segnities007.stylish_mycars.presentation.components

import com.segnities007.stylish_mycars.presentation.components.atoms.utils.StylishConnectedCorners
import com.segnities007.stylish_mycars.presentation.components.atoms.utils.stylishConnectedColumnCorners
import com.segnities007.stylish_mycars.presentation.components.atoms.utils.stylishConnectedGridCorners
import com.segnities007.stylish_mycars.presentation.components.atoms.utils.stylishConnectedRowCorners
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * 接続グリッドの角丸判定のテスト。
 * 規則: ある角が「外角」（12dpで丸める）なのは、その角に隣り合う2辺の両方が
 * 空白に面している場合のみ。それ以外は「接続角」（2dp）とする。
 */
class StylishConnectedGridCornersTest {

    private fun corners(
        topStart: Boolean,
        topEnd: Boolean,
        bottomStart: Boolean,
        bottomEnd: Boolean,
    ) = StylishConnectedCorners(topStart, topEnd, bottomStart, bottomEnd)

    @Test
    fun `single item is fully rounded`() {
        assertEquals(corners(true, true, true, true), stylishConnectedGridCorners(0, 1, 2))
    }

    @Test
    fun `full 2x2 grid rounds only the four outer corners`() {
        // [0][1]
        // [2][3]
        assertEquals(corners(true, false, false, false), stylishConnectedGridCorners(0, 4, 2))
        assertEquals(corners(false, true, false, false), stylishConnectedGridCorners(1, 4, 2))
        assertEquals(corners(false, false, true, false), stylishConnectedGridCorners(2, 4, 2))
        assertEquals(corners(false, false, false, true), stylishConnectedGridCorners(3, 4, 2))
    }

    @Test
    fun `3 items in 2 columns rounds the corner above the empty cell`() {
        // [0][1]
        // [2][ ]
        assertEquals(corners(true, false, false, false), stylishConnectedGridCorners(0, 3, 2))
        // item1 の bottomEnd は下のセルが空白なので丸くなる（従来のバグ箇所）
        assertEquals(corners(false, true, false, true), stylishConnectedGridCorners(1, 3, 2))
        // item2 は最終行かつ右隣も空白なので bottomStart/bottomEnd が丸い
        assertEquals(corners(false, false, true, true), stylishConnectedGridCorners(2, 3, 2))
    }

    @Test
    fun `5 items in 3 columns rounds corners facing empty cells`() {
        // [0][1][2]
        // [3][4][ ]
        assertEquals(corners(true, false, false, false), stylishConnectedGridCorners(0, 5, 3))
        assertEquals(corners(false, false, false, false), stylishConnectedGridCorners(1, 5, 3))
        // item2 の bottomEnd は下(セル(1,2))が空白なので丸い
        assertEquals(corners(false, true, false, true), stylishConnectedGridCorners(2, 5, 3))
        assertEquals(corners(false, false, true, false), stylishConnectedGridCorners(3, 5, 3))
        // item4 は上(item1)・左(item3)と接続し、右隣・下隣が空白なので bottomEnd のみ丸い
        assertEquals(corners(false, false, false, true), stylishConnectedGridCorners(4, 5, 3))
    }

    @Test
    fun `single row behaves like a connected row`() {
        // [0][1][2]
        assertEquals(corners(true, false, true, false), stylishConnectedGridCorners(0, 3, 3))
        assertEquals(corners(false, false, false, false), stylishConnectedGridCorners(1, 3, 3))
        assertEquals(corners(false, true, false, true), stylishConnectedGridCorners(2, 3, 3))
    }

    @Test
    fun `single column behaves like a connected column`() {
        // [0]
        // [1]
        // [2]
        assertEquals(corners(true, true, false, false), stylishConnectedGridCorners(0, 3, 1))
        assertEquals(corners(false, false, false, false), stylishConnectedGridCorners(1, 3, 1))
        assertEquals(corners(false, false, true, true), stylishConnectedGridCorners(2, 3, 1))
    }

    @Test
    fun `4 items in 3 columns leaves two cells empty in last row`() {
        // [0][1][2]
        // [3][ ][ ]
        assertEquals(corners(true, false, false, false), stylishConnectedGridCorners(0, 4, 3))
        // item1 は下(セル(1,1))が空白だが、左右が埋まっているので底辺の両角は接続角
        assertEquals(corners(false, false, false, false), stylishConnectedGridCorners(1, 4, 3))
        // item2 の bottomEnd は下・右が空白なので丸い
        assertEquals(corners(false, true, false, true), stylishConnectedGridCorners(2, 4, 3))
        // item3 は最終行の唯一の要素。上(item0)と接続し、左・右・下が空白
        assertEquals(corners(false, false, true, true), stylishConnectedGridCorners(3, 4, 3))
    }

    @Test
    fun `single element column is fully rounded like a standalone`() {
        assertEquals(corners(true, true, true, true), stylishConnectedColumnCorners(0, 1))
    }

    @Test
    fun `single element row is fully rounded like a standalone`() {
        assertEquals(corners(true, true, true, true), stylishConnectedRowCorners(0, 1))
    }

    @Test
    fun `two element column rounds only outer corners`() {
        assertEquals(corners(true, true, false, false), stylishConnectedColumnCorners(0, 2))
        assertEquals(corners(false, false, true, true), stylishConnectedColumnCorners(1, 2))
    }

    @Test
    fun `two element row rounds only outer corners`() {
        assertEquals(corners(true, false, true, false), stylishConnectedRowCorners(0, 2))
        assertEquals(corners(false, true, false, true), stylishConnectedRowCorners(1, 2))
    }
}
