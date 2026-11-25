package com.example

import com.example.domain.model.GameCatalog
import com.example.domain.model.JewelType
import com.example.ui.game.TetrisEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class TetrisEngineTest {

    @Test
    fun `initial state spawns active piece and respects stone rows`() {
        val level4 = GameCatalog.levelByNumber(4) // 3 initialStoneRows
        val lyra = GameCatalog.characterById("lyra")
        val engine = TetrisEngine(level = level4, character = lyra, random = Random(42))

        val snap = engine.snapshot()
        assertNotNull(snap.activePiece)
        assertEquals(0, snap.score)

        // Bottom row should contain stone obstacles
        val bottomRowHasStone = snap.grid[TetrisEngine.ROWS - 1].any { it?.isStoneObstacle == true }
        assertTrue(bottomRowHasStone)
    }

    @Test
    fun `hard drop increases score and spawns next piece`() {
        val level1 = GameCatalog.levelByNumber(1)
        val ignis = GameCatalog.characterById("ignis")
        val engine = TetrisEngine(level = level1, character = ignis, random = Random(7))

        val before = engine.snapshot()
        assertNotNull(before.activePiece)

        engine.hardDrop()
        val after = engine.snapshot()
        assertTrue(after.score > 0)
        assertNotNull(after.activePiece)
    }

    @Test
    fun `jewel pieces spawn periodically during play`() {
        val level1 = GameCatalog.levelByNumber(1)
        val sora = GameCatalog.characterById("sora")
        val engine = TetrisEngine(level = level1, character = sora, random = Random(123))

        var sawJewel = false
        repeat(10) {
            val snap = engine.snapshot()
            if (snap.activePiece?.jewelType != JewelType.NONE || snap.nextPieceJewel != JewelType.NONE) {
                sawJewel = true
            }
            engine.hardDrop()
        }
        assertTrue("Expected at least one jewel piece within 10 spawns", sawJewel)
    }

    @Test
    fun `progressive difficulty speeds up higher levels`() {
        val level1 = GameCatalog.levelByNumber(1)
        val level6 = GameCatalog.levelByNumber(6)
        val lyra = GameCatalog.characterById("lyra")

        val engine1 = TetrisEngine(level1, lyra, Random(1))
        val engine6 = TetrisEngine(level6, lyra, Random(1))

        assertTrue(engine6.currentTickIntervalMs() < engine1.currentTickIntervalMs())
    }
}
