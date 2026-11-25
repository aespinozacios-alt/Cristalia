package com.example.ui.game

import com.example.domain.model.ActivePiece
import com.example.domain.model.BoardCell
import com.example.domain.model.CharacterHero
import com.example.domain.model.GridPoint
import com.example.domain.model.JewelType
import com.example.domain.model.StoryLevel
import com.example.domain.model.TetrominoType
import com.example.domain.model.VisualEffectEvent
import kotlin.math.roundToInt
import kotlin.random.Random

data class LockResolution(
    val linesCleared: Int,
    val triggeredJewels: List<JewelType>,
    val pointsEarned: Int,
    val effects: List<VisualEffectEvent>,
    val reachedLevelTarget: Boolean
)

data class TetrisBoardState(
    val grid: List<List<BoardCell?>>,
    val activePiece: ActivePiece?,
    val nextPieceType: TetrominoType,
    val nextPieceJewel: JewelType,
    val holdPieceType: TetrominoType?,
    val holdPieceJewel: JewelType,
    val canHoldThisTurn: Boolean,
    val ghostRow: Int?,
    val score: Int,
    val linesCleared: Int,
    val comboCount: Int,
    val jewelsTriggeredCount: Int,
    val relicCharge: Int,
    val slowMoRemainingMs: Long,
    val isGameOver: Boolean,
    val levelTargetCompleted: Boolean,
    val endlessContinued: Boolean
)

class TetrisEngine(
    private var level: StoryLevel,
    private var character: CharacterHero,
    private val random: Random = Random.Default
) {
    companion object {
        const val ROWS = 20
        const val COLS = 10
    }

    private val grid: Array<Array<BoardCell?>> = Array(ROWS) { arrayOfNulls(COLS) }
    private val bag = ArrayDeque<TetrominoType>()
    private var piecesSpawned = 0

    private var activePiece: ActivePiece? = null
    private var nextPieceType: TetrominoType = TetrominoType.T
    private var nextPieceJewel: JewelType = JewelType.NONE
    private var holdPieceType: TetrominoType? = null
    private var holdPieceJewel: JewelType = JewelType.NONE
    private var canHoldThisTurn: Boolean = true

    private var score: Int = 0
    private var linesCleared: Int = 0
    private var comboCount: Int = 0
    private var jewelsTriggeredCount: Int = 0
    private var relicCharge: Int = 0
    private var slowMoRemainingMs: Long = 0L
    private var isGameOver: Boolean = false
    private var levelTargetCompleted: Boolean = false
    private var endlessContinued: Boolean = false
    private var nextEffectId: Long = 1L

    init {
        reset(level, character)
    }

    fun reset(newLevel: StoryLevel, newCharacter: CharacterHero) {
        level = newLevel
        character = newCharacter
        for (r in 0 until ROWS) {
            for (c in 0 until COLS) {
                grid[r][c] = null
            }
        }
        bag.clear()
        piecesSpawned = 0
        holdPieceType = null
        holdPieceJewel = JewelType.NONE
        canHoldThisTurn = true
        score = 0
        linesCleared = 0
        comboCount = 0
        jewelsTriggeredCount = 0
        relicCharge = 0
        slowMoRemainingMs = 0L
        isGameOver = false
        levelTargetCompleted = false
        endlessContinued = false

        seedInitialStoneRows(newLevel.initialStoneRows)
        prepareNextPreview()
        spawnNextPiece()
    }

    fun continueEndless() {
        endlessContinued = true
    }

    private fun seedInitialStoneRows(stoneRows: Int) {
        if (stoneRows <= 0) return
        val palette = TetrominoType.entries
        for (offset in 0 until stoneRows.coerceAtMost(6)) {
            val row = ROWS - 1 - offset
            val gapCols = (0 until COLS).shuffled(random).take(3).toSet()
            for (col in 0 until COLS) {
                if (col !in gapCols) {
                    grid[row][col] = BoardCell(
                        tetrominoType = palette[(row + col) % palette.size],
                        jewelType = JewelType.NONE,
                        isStoneObstacle = true
                    )
                }
            }
        }
    }

    private fun refillBagIfNeeded() {
        if (bag.isEmpty()) {
            val shuffled = TetrominoType.entries.shuffled(random)
            bag.addAll(shuffled)
        }
    }

    private fun rollJewelForSpawn(): JewelType {
        val interval = if (character.id == "sora") {
            (level.jewelEveryNPieces - 1).coerceAtLeast(3)
        } else {
            level.jewelEveryNPieces
        }
        return if (piecesSpawned > 0 && piecesSpawned % interval == 0) {
            val roll = random.nextFloat()
            if (roll < 0.45f) {
                character.affinityJewel
            } else {
                listOf(
                    JewelType.RUBY,
                    JewelType.SAPPHIRE,
                    JewelType.EMERALD,
                    JewelType.AMETHYST
                ).random(random)
            }
        } else {
            JewelType.NONE
        }
    }

    private fun prepareNextPreview() {
        refillBagIfNeeded()
        nextPieceType = bag.removeFirst()
        piecesSpawned++
        nextPieceJewel = rollJewelForSpawn()
    }

    private fun spawnNextPiece() {
        val type = nextPieceType
        val jewel = nextPieceJewel
        val jewelIdx = if (jewel != JewelType.NONE) random.nextInt(4) else null
        val candidate = ActivePiece(
            type = type,
            row = 0,
            col = 3,
            rotation = 0,
            jewelCellIndex = jewelIdx,
            jewelType = jewel
        )
        prepareNextPreview()
        canHoldThisTurn = true
        if (isValidPosition(candidate)) {
            activePiece = candidate
        } else {
            activePiece = null
            isGameOver = true
        }
    }

    fun currentTickIntervalMs(): Long {
        val progress = (linesCleared.toFloat() / level.targetLines.coerceAtLeast(1).toFloat())
            .coerceIn(0f, 1.4f)
        val range = (level.baseTickMs - level.minTickMs).coerceAtLeast(60L)
        var interval = (level.baseTickMs - (range * progress)).toLong()
            .coerceAtLeast((level.minTickMs * 0.8f).toLong())
        if (character.id == "kael") {
            interval = (interval * 1.12f).toLong()
        }
        if (slowMoRemainingMs > 0L) {
            interval *= 2L
        }
        return interval.coerceAtLeast(120L)
    }

    fun tickSlowMo(elapsedMs: Long) {
        if (slowMoRemainingMs > 0L) {
            slowMoRemainingMs = (slowMoRemainingMs - elapsedMs).coerceAtLeast(0L)
        }
    }

    fun moveLeft(): Boolean = tryMove(dRow = 0, dCol = -1)

    fun moveRight(): Boolean = tryMove(dRow = 0, dCol = 1)

    fun softDropStep(): Pair<Boolean, LockResolution?> {
        if (isGameOver) return false to null
        if (tryMove(dRow = 1, dCol = 0)) {
            score += 1
            return true to null
        }
        val resolution = lockActivePiece()
        return false to resolution
    }

    fun gravityStep(): LockResolution? {
        if (isGameOver) return null
        if (tryMove(dRow = 1, dCol = 0)) {
            return null
        }
        return lockActivePiece()
    }

    fun hardDrop(): LockResolution? {
        if (isGameOver) return null
        val piece = activePiece ?: return null
        val ghost = computeGhostRow(piece)
        val droppedRows = (ghost - piece.row).coerceAtLeast(0)
        activePiece = piece.copy(row = ghost)
        score += droppedRows * 2
        return lockActivePiece()
    }

    fun rotateClockwise(): Boolean {
        val piece = activePiece ?: return false
        if (piece.type == TetrominoType.O) return false
        val targetRot = (piece.rotation + 1) % 4
        val kicks = listOf(
            GridPoint(0, 0),
            GridPoint(0, -1),
            GridPoint(0, 1),
            GridPoint(0, -2),
            GridPoint(0, 2),
            GridPoint(-1, 0)
        )
        for (kick in kicks) {
            val candidate = piece.copy(
                rotation = targetRot,
                row = piece.row + kick.row,
                col = piece.col + kick.col
            )
            if (isValidPosition(candidate)) {
                activePiece = candidate
                return true
            }
        }
        return false
    }

    fun holdCurrentPiece(): Boolean {
        if (isGameOver || !canHoldThisTurn) return false
        val current = activePiece ?: return false
        val prevHoldType = holdPieceType
        val prevHoldJewel = holdPieceJewel
        holdPieceType = current.type
        holdPieceJewel = current.jewelType
        canHoldThisTurn = false

        if (prevHoldType == null) {
            spawnNextPiece()
            canHoldThisTurn = false
        } else {
            val jewelIdx = if (prevHoldJewel != JewelType.NONE) random.nextInt(4) else null
            val restored = ActivePiece(
                type = prevHoldType,
                row = 0,
                col = 3,
                rotation = 0,
                jewelCellIndex = jewelIdx,
                jewelType = prevHoldJewel
            )
            if (isValidPosition(restored)) {
                activePiece = restored
            } else {
                activePiece = null
                isGameOver = true
            }
        }
        return true
    }

    fun activateCharacterRelic(): LockResolution? {
        if (isGameOver || relicCharge < 100) return null
        relicCharge = 0
        val effects = mutableListOf<VisualEffectEvent>()
        var bonusPoints = 0
        var clearedCount = 0

        when (character.id) {
            "lyra" -> {
                val dissolved = dissolveMostCommonColor(maxCells = 10)
                bonusPoints = 200 + dissolved * 15
                effects.add(
                    VisualEffectEvent(
                        id = nextEffectId++,
                        title = "¡Luz Astral!",
                        subtitle = "$dissolved bloques purificados",
                        color = character.accentColor,
                        targetRow = ROWS / 2,
                        targetCol = COLS / 2
                    )
                )
            }
            "kael" -> {
                slowMoRemainingMs = 12_000L
                val clearedRow = clearBottomOccupiedRow()
                if (clearedRow != null) clearedCount = 1
                bonusPoints = 180
                effects.add(
                    VisualEffectEvent(
                        id = nextEffectId++,
                        title = "¡Brisa Temporal!",
                        subtitle = "Cámara lenta 12s y fila despejada",
                        color = character.accentColor,
                        targetRow = clearedRow ?: (ROWS - 1),
                        targetCol = COLS / 2
                    )
                )
            }
            "sora" -> {
                var rowsRemoved = 0
                repeat(2) {
                    if (clearBottomOccupiedRow() != null) rowsRemoved++
                }
                clearedCount = rowsRemoved
                bonusPoints = 220
                effects.add(
                    VisualEffectEvent(
                        id = nextEffectId++,
                        title = "¡Ola Purificadora!",
                        subtitle = "$rowsRemoved filas barridas",
                        color = character.accentColor,
                        targetRow = ROWS - 2,
                        targetCol = COLS / 2
                    )
                )
            }
            else -> {
                val target = findDensestCenter()
                val destroyed = blast3x3(target.row, target.col)
                bonusPoints = 240 + destroyed * 20
                effects.add(
                    VisualEffectEvent(
                        id = nextEffectId++,
                        title = "¡Estallido Solar!",
                        subtitle = "$destroyed bloques pulverizados",
                        color = character.accentColor,
                        targetRow = target.row,
                        targetCol = target.col
                    )
                )
            }
        }

        score += bonusPoints
        linesCleared += clearedCount
        val reachedTarget = !levelTargetCompleted && !endlessContinued && linesCleared >= level.targetLines
        if (reachedTarget) {
            levelTargetCompleted = true
        }
        return LockResolution(
            linesCleared = clearedCount,
            triggeredJewels = listOf(character.affinityJewel),
            pointsEarned = bonusPoints,
            effects = effects,
            reachedLevelTarget = reachedTarget
        )
    }

    private fun tryMove(dRow: Int, dCol: Int): Boolean {
        val piece = activePiece ?: return false
        val candidate = piece.copy(row = piece.row + dRow, col = piece.col + dCol)
        if (isValidPosition(candidate)) {
            activePiece = candidate
            return true
        }
        return false
    }

    private fun isValidPosition(piece: ActivePiece): Boolean {
        for (pt in piece.occupiedPoints()) {
            if (pt.col < 0 || pt.col >= COLS || pt.row < 0 || pt.row >= ROWS) {
                return false
            }
            if (grid[pt.row][pt.col] != null) {
                return false
            }
        }
        return true
    }

    private fun computeGhostRow(piece: ActivePiece): Int {
        var r = piece.row
        while (isValidPosition(piece.copy(row = r + 1))) {
            r++
        }
        return r
    }

    private fun lockActivePiece(): LockResolution {
        val piece = activePiece ?: return LockResolution(0, emptyList(), 0, emptyList(), false)
        val cells = piece.occupiedPoints()
        cells.forEachIndexed { idx, pt ->
            if (pt.row in 0 until ROWS && pt.col in 0 until COLS) {
                val jewel = if (piece.jewelCellIndex == idx) piece.jewelType else JewelType.NONE
                grid[pt.row][pt.col] = BoardCell(
                    tetrominoType = piece.type,
                    jewelType = jewel,
                    isStoneObstacle = false
                )
            }
        }

        val fullRows = (0 until ROWS).filter { r ->
            (0 until COLS).all { c -> grid[r][c] != null }
        }.toMutableSet()

        val effects = mutableListOf<VisualEffectEvent>()
        val triggeredJewels = mutableListOf<JewelType>()
        var jewelBonusPoints = 0

        if (fullRows.isNotEmpty()) {
            // Find jewel cells inside the cleared rows
            data class JewelHit(val row: Int, val col: Int, val type: JewelType)
            val jewelHits = mutableListOf<JewelHit>()
            for (r in fullRows.sorted()) {
                for (c in 0 until COLS) {
                    val cell = grid[r][c]
                    if (cell != null && cell.jewelType != JewelType.NONE) {
                        jewelHits.add(JewelHit(r, c, cell.jewelType))
                    }
                }
            }

            // Apply jewel superpowers before collapsing rows
            for (hit in jewelHits) {
                triggeredJewels.add(hit.type)
                jewelsTriggeredCount++
                when (hit.type) {
                    JewelType.RUBY -> {
                        val blasted = blast3x3(hit.row, hit.col)
                        val pts = if (character.id == "ignis") 400 else 200
                        jewelBonusPoints += pts + blasted * 15
                        effects.add(
                            VisualEffectEvent(
                                id = nextEffectId++,
                                title = "¡Bomba Solar!",
                                subtitle = "+${pts + blasted * 15} pts",
                                color = JewelType.RUBY.gemColor,
                                targetRow = hit.row,
                                targetCol = hit.col
                            )
                        )
                    }
                    JewelType.SAPPHIRE -> {
                        val extraRow = (hit.row + 1).coerceAtMost(ROWS - 1)
                        fullRows.add(extraRow)
                        val pts = 220
                        jewelBonusPoints += pts
                        effects.add(
                            VisualEffectEvent(
                                id = nextEffectId++,
                                title = "¡Pulso de Marea!",
                                subtitle = "Fila extra despejada +$pts",
                                color = JewelType.SAPPHIRE.gemColor,
                                targetRow = extraRow,
                                targetCol = hit.col
                            )
                        )
                    }
                    JewelType.EMERALD -> {
                        slowMoRemainingMs = 12_000L
                        fullRows.add(ROWS - 1)
                        val pts = 180
                        jewelBonusPoints += pts
                        effects.add(
                            VisualEffectEvent(
                                id = nextEffectId++,
                                title = "¡Calma Temporal!",
                                subtitle = "Cámara lenta 12s +$pts",
                                color = JewelType.EMERALD.gemColor,
                                targetRow = hit.row,
                                targetCol = hit.col
                            )
                        )
                    }
                    JewelType.AMETHYST -> {
                        val dissolved = dissolveMostCommonColor(maxCells = 8)
                        val pts = 250 + dissolved * 15
                        jewelBonusPoints += pts
                        effects.add(
                            VisualEffectEvent(
                                id = nextEffectId++,
                                title = "¡Purga Cromática!",
                                subtitle = "$dissolved bloques +$pts",
                                color = JewelType.AMETHYST.gemColor,
                                targetRow = hit.row,
                                targetCol = hit.col
                            )
                        )
                    }
                    JewelType.NONE -> Unit
                }
            }

            // Collapse cleared rows cleanly
            val sortedRows = fullRows.sorted()
            collapseRows(sortedRows)
        }

        val countCleared = fullRows.size
        var pointsEarned = jewelBonusPoints
        if (countCleared > 0) {
            comboCount++
            val baseLinePts = when (countCleared) {
                1 -> 100
                2 -> 300
                3 -> 520
                else -> 850
            }
            val levelMult = 1.0 + (level.levelNumber - 1) * 0.25
            val comboBonus = (comboCount - 1) * 50
            var lineScore = ((baseLinePts * levelMult) + comboBonus).roundToInt()
            if (character.id == "lyra") {
                lineScore = (lineScore * 1.15f).roundToInt()
            }
            if (character.id == "ignis" && countCleared >= 4) {
                lineScore *= 2
            }
            pointsEarned += lineScore
            linesCleared += countCleared
            relicCharge = (relicCharge + countCleared * 25 + triggeredJewels.size * 15).coerceAtMost(100)

            if (triggeredJewels.isEmpty()) {
                val label = when {
                    countCleared >= 4 -> "¡TETRIS!"
                    comboCount > 1 -> "¡Combo x$comboCount!"
                    else -> "+$lineScore pts"
                }
                effects.add(
                    VisualEffectEvent(
                        id = nextEffectId++,
                        title = label,
                        subtitle = if (countCleared >= 2) "$countCleared líneas" else "",
                        color = character.accentColor,
                        targetRow = fullRows.min(),
                        targetCol = COLS / 2
                    )
                )
            }
        } else {
            comboCount = 0
        }

        score += pointsEarned
        spawnNextPiece()

        val reachedTarget = !levelTargetCompleted && !endlessContinued && linesCleared >= level.targetLines
        if (reachedTarget) {
            levelTargetCompleted = true
        }

        return LockResolution(
            linesCleared = countCleared,
            triggeredJewels = triggeredJewels,
            pointsEarned = pointsEarned,
            effects = effects,
            reachedLevelTarget = reachedTarget
        )
    }

    private fun collapseRows(rowsToRemove: List<Int>) {
        if (rowsToRemove.isEmpty()) return
        val removeSet = rowsToRemove.toSet()
        val remaining = mutableListOf<Array<BoardCell?>>()
        for (r in 0 until ROWS) {
            if (r !in removeSet) {
                remaining.add(grid[r].copyOf())
            }
        }
        val emptyTop = ROWS - remaining.size
        for (r in 0 until ROWS) {
            grid[r] = if (r < emptyTop) {
                arrayOfNulls(COLS)
            } else {
                remaining[r - emptyTop]
            }
        }
    }

    private fun blast3x3(centerRow: Int, centerCol: Int): Int {
        var count = 0
        for (r in (centerRow - 1)..(centerRow + 1)) {
            for (c in (centerCol - 1)..(centerCol + 1)) {
                if (r in 0 until ROWS && c in 0 until COLS && grid[r][c] != null) {
                    grid[r][c] = null
                    count++
                }
            }
        }
        return count
    }

    private fun dissolveMostCommonColor(maxCells: Int): Int {
        val counts = mutableMapOf<TetrominoType, Int>()
        for (r in 0 until ROWS) {
            for (c in 0 until COLS) {
                val cell = grid[r][c]
                if (cell != null && !cell.isStoneObstacle) {
                    counts[cell.tetrominoType] = (counts[cell.tetrominoType] ?: 0) + 1
                }
            }
        }
        val targetType = counts.maxByOrNull { it.value }?.key ?: return 0
        var removed = 0
        for (r in 0 until ROWS) {
            for (c in 0 until COLS) {
                if (removed >= maxCells) return removed
                val cell = grid[r][c]
                if (cell != null && !cell.isStoneObstacle && cell.tetrominoType == targetType) {
                    grid[r][c] = null
                    removed++
                }
            }
        }
        return removed
    }

    private fun clearBottomOccupiedRow(): Int? {
        for (r in (ROWS - 1) downTo 0) {
            if ((0 until COLS).any { c -> grid[r][c] != null }) {
                collapseRows(listOf(r))
                return r
            }
        }
        return null
    }

    private fun findDensestCenter(): GridPoint {
        for (r in 0 until ROWS) {
            for (c in 0 until COLS) {
                if (grid[r][c] != null) {
                    return GridPoint((r + 2).coerceAtMost(ROWS - 2), c.coerceIn(1, COLS - 2))
                }
            }
        }
        return GridPoint(ROWS - 2, COLS / 2)
    }

    fun snapshot(): TetrisBoardState {
        val gridCopy = List(ROWS) { r ->
            List(COLS) { c -> grid[r][c] }
        }
        val piece = activePiece
        val ghost = piece?.let { computeGhostRow(it) }
        return TetrisBoardState(
            grid = gridCopy,
            activePiece = piece,
            nextPieceType = nextPieceType,
            nextPieceJewel = nextPieceJewel,
            holdPieceType = holdPieceType,
            holdPieceJewel = holdPieceJewel,
            canHoldThisTurn = canHoldThisTurn,
            ghostRow = ghost,
            score = score,
            linesCleared = linesCleared,
            comboCount = comboCount,
            jewelsTriggeredCount = jewelsTriggeredCount,
            relicCharge = relicCharge,
            slowMoRemainingMs = slowMoRemainingMs,
            isGameOver = isGameOver,
            levelTargetCompleted = levelTargetCompleted,
            endlessContinued = endlessContinued
        )
    }
}
