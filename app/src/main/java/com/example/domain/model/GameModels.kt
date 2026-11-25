package com.example.domain.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

data class GridPoint(val row: Int, val col: Int)

enum class TetrominoType {
    I, O, T, S, Z, J, L;

    fun cells(rotation: Int): List<GridPoint> {
        val rot = ((rotation % 4) + 4) % 4
        return when (this) {
            I -> when (rot) {
                0 -> listOf(GridPoint(1, 0), GridPoint(1, 1), GridPoint(1, 2), GridPoint(1, 3))
                1 -> listOf(GridPoint(0, 2), GridPoint(1, 2), GridPoint(2, 2), GridPoint(3, 2))
                2 -> listOf(GridPoint(2, 0), GridPoint(2, 1), GridPoint(2, 2), GridPoint(2, 3))
                else -> listOf(GridPoint(0, 1), GridPoint(1, 1), GridPoint(2, 1), GridPoint(3, 1))
            }
            O -> listOf(GridPoint(0, 1), GridPoint(0, 2), GridPoint(1, 1), GridPoint(1, 2))
            T -> when (rot) {
                0 -> listOf(GridPoint(0, 1), GridPoint(1, 0), GridPoint(1, 1), GridPoint(1, 2))
                1 -> listOf(GridPoint(0, 1), GridPoint(1, 1), GridPoint(1, 2), GridPoint(2, 1))
                2 -> listOf(GridPoint(1, 0), GridPoint(1, 1), GridPoint(1, 2), GridPoint(2, 1))
                else -> listOf(GridPoint(0, 1), GridPoint(1, 0), GridPoint(1, 1), GridPoint(2, 1))
            }
            S -> when (rot) {
                0 -> listOf(GridPoint(0, 1), GridPoint(0, 2), GridPoint(1, 0), GridPoint(1, 1))
                1 -> listOf(GridPoint(0, 1), GridPoint(1, 1), GridPoint(1, 2), GridPoint(2, 2))
                2 -> listOf(GridPoint(1, 1), GridPoint(1, 2), GridPoint(2, 0), GridPoint(2, 1))
                else -> listOf(GridPoint(0, 0), GridPoint(1, 0), GridPoint(1, 1), GridPoint(2, 1))
            }
            Z -> when (rot) {
                0 -> listOf(GridPoint(0, 0), GridPoint(0, 1), GridPoint(1, 1), GridPoint(1, 2))
                1 -> listOf(GridPoint(0, 2), GridPoint(1, 1), GridPoint(1, 2), GridPoint(2, 1))
                2 -> listOf(GridPoint(1, 0), GridPoint(1, 1), GridPoint(2, 1), GridPoint(2, 2))
                else -> listOf(GridPoint(0, 1), GridPoint(1, 0), GridPoint(1, 1), GridPoint(2, 0))
            }
            J -> when (rot) {
                0 -> listOf(GridPoint(0, 0), GridPoint(1, 0), GridPoint(1, 1), GridPoint(1, 2))
                1 -> listOf(GridPoint(0, 1), GridPoint(0, 2), GridPoint(1, 1), GridPoint(2, 1))
                2 -> listOf(GridPoint(1, 0), GridPoint(1, 1), GridPoint(1, 2), GridPoint(2, 2))
                else -> listOf(GridPoint(0, 1), GridPoint(1, 1), GridPoint(2, 0), GridPoint(2, 1))
            }
            L -> when (rot) {
                0 -> listOf(GridPoint(0, 2), GridPoint(1, 0), GridPoint(1, 1), GridPoint(1, 2))
                1 -> listOf(GridPoint(0, 1), GridPoint(1, 1), GridPoint(2, 1), GridPoint(2, 2))
                2 -> listOf(GridPoint(1, 0), GridPoint(1, 1), GridPoint(1, 2), GridPoint(2, 0))
                else -> listOf(GridPoint(0, 0), GridPoint(0, 1), GridPoint(1, 1), GridPoint(2, 1))
            }
        }
    }
}

enum class JewelType(
    val displayName: String,
    val powerTitle: String,
    val description: String,
    val gemColor: Color,
    val glowColor: Color
) {
    NONE(
        displayName = "Sin joya",
        powerTitle = "",
        description = "",
        gemColor = Color.Transparent,
        glowColor = Color.Transparent
    ),
    RUBY(
        displayName = "Rubí Carmesí",
        powerTitle = "Bomba Solar",
        description = "Al completar su línea, explota un área de 3x3 alrededor del rubí destruyendo obstáculos.",
        gemColor = Color(0xFFFF5A6E),
        glowColor = Color(0xFFFF9AA7)
    ),
    SAPPHIRE(
        displayName = "Zafiro Lunar",
        powerTitle = "Pulso de Marea",
        description = "Al completar su línea, barre también la fila inferior dejando espacio despejado.",
        gemColor = Color(0xFF47B8FF),
        glowColor = Color(0xFF9DE0FF)
    ),
    EMERALD(
        displayName = "Esmeralda Vital",
        powerTitle = "Calma Temporal",
        description = "Ralentiza la gravedad al 50% durante 12 segundos y purifica la fila más baja.",
        gemColor = Color(0xFF4BE0A8),
        glowColor = Color(0xFFA2F7D8)
    ),
    AMETHYST(
        displayName = "Amatista Astral",
        powerTitle = "Purga Cromática",
        description = "Disuelve hasta 8 bloques del color más abundante del tablero y multiplica tus puntos.",
        gemColor = Color(0xFFBA84FF),
        glowColor = Color(0xFFE0C4FF)
    )
}

data class BoardCell(
    val tetrominoType: TetrominoType,
    val jewelType: JewelType = JewelType.NONE,
    val isStoneObstacle: Boolean = false
)

data class ActivePiece(
    val type: TetrominoType,
    val row: Int,
    val col: Int,
    val rotation: Int = 0,
    val jewelCellIndex: Int? = null,
    val jewelType: JewelType = JewelType.NONE
) {
    fun occupiedPoints(): List<GridPoint> =
        type.cells(rotation).map { GridPoint(row + it.row, col + it.col) }
}

data class CharacterHero(
    val id: String,
    val name: String,
    val title: String,
    val lore: String,
    val affinityJewel: JewelType,
    val passiveTitle: String,
    val passiveDesc: String,
    val activePowerTitle: String,
    val activePowerDesc: String,
    @DrawableRes val portraitResId: Int,
    val accentColor: Color
)

enum class BoardMotif {
    SANCTUARY_STARS,
    SAKURA_PETALS,
    JADE_BAMBOO,
    CORAL_WAVES,
    ASTRAL_CONSTELLATION
}

data class BoardTheme(
    val id: String,
    val name: String,
    val subtitle: String,
    val unlockCostCrystals: Int,
    val unlockLevelNumber: Int,
    val bgTop: Color,
    val bgBottom: Color,
    val gridLineColor: Color,
    val frameBorderColor: Color,
    val stoneColor: Color,
    val motif: BoardMotif,
    val pieceColors: Map<TetrominoType, Color>
)

data class StoryLevel(
    val levelNumber: Int,
    val title: String,
    val location: String,
    val difficultyLabel: String,
    val targetLines: Int,
    val baseTickMs: Long,
    val minTickMs: Long,
    val initialStoneRows: Int,
    val jewelEveryNPieces: Int,
    val rewardCrystals: Int,
    val unlocksThemeId: String?,
    val generalPrologue: String,
    val characterVoices: Map<String, String>,
    val epilogue: String
)

data class AmbientTrack(
    val id: String,
    val name: String,
    val description: String,
    val chordFrequencies: List<Float>,
    val shimmerRateSeconds: Float
)

data class VisualEffectEvent(
    val id: Long,
    val title: String,
    val subtitle: String,
    val color: Color,
    val targetRow: Int,
    val targetCol: Int,
    val createdAtMs: Long = System.currentTimeMillis()
)
