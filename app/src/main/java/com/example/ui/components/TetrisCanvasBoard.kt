package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.BoardMotif
import com.example.domain.model.BoardTheme
import com.example.domain.model.JewelType
import com.example.domain.model.TetrominoType
import com.example.domain.model.VisualEffectEvent
import com.example.ui.game.TetrisBoardState
import com.example.ui.game.TetrisEngine
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TetrisCanvasBoard(
    boardState: TetrisBoardState,
    theme: BoardTheme,
    effects: List<VisualEffectEvent>,
    onTapRotate: () -> Unit,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onSoftDrop: () -> Unit,
    onHardDrop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "board_ambient")
    val ambientPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ambient_phase"
    )
    val jewelPulse by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "jewel_pulse"
    )

    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val boardShape = RoundedCornerShape(16.dp)
        Box(
            modifier = Modifier
                .aspectRatio(0.5f, matchHeightConstraintsFirst = true)
                .clip(boardShape)
                .border(
                    width = 1.8.dp,
                    color = theme.frameBorderColor.copy(alpha = 0.72f),
                    shape = boardShape
                )
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { onTapRotate() })
                    }
                    .pointerInput(Unit) {
                        var accumX = 0f
                        var accumY = 0f
                        var hardDropped = false
                        val stepPx = (size.width / TetrisEngine.COLS.toFloat()).coerceAtLeast(24f)
                        detectDragGestures(
                            onDragStart = {
                                accumX = 0f
                                accumY = 0f
                                hardDropped = false
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                if (hardDropped) return@detectDragGestures
                                accumX += dragAmount.x
                                accumY += dragAmount.y

                                while (accumX >= stepPx * 0.85f) {
                                    onMoveRight()
                                    accumX -= stepPx * 0.85f
                                }
                                while (accumX <= -stepPx * 0.85f) {
                                    onMoveLeft()
                                    accumX += stepPx * 0.85f
                                }
                                if (accumY >= stepPx * 3.4f && dragAmount.y > 28f) {
                                    onHardDrop()
                                    hardDropped = true
                                } else {
                                    while (accumY >= stepPx * 0.9f) {
                                        onSoftDrop()
                                        accumY -= stepPx * 0.9f
                                    }
                                }
                            }
                        )
                    }
            ) {
                val cellW = size.width / TetrisEngine.COLS.toFloat()
                val cellH = size.height / TetrisEngine.ROWS.toFloat()

                // 1. Background Gradient
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(theme.bgTop, theme.bgBottom)
                    )
                )

                // 2. Thematic Subtle Motif
                drawBoardMotif(theme, ambientPhase)

                // 3. Subtle Inner Grid Lines
                for (c in 1 until TetrisEngine.COLS) {
                    val x = c * cellW
                    drawLine(
                        color = theme.gridLineColor,
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 1f
                    )
                }
                for (r in 1 until TetrisEngine.ROWS) {
                    val y = r * cellH
                    drawLine(
                        color = theme.gridLineColor,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                }

                // 4. Locked Cells
                for (r in 0 until TetrisEngine.ROWS) {
                    for (c in 0 until TetrisEngine.COLS) {
                        val cell = boardState.grid[r][c] ?: continue
                        val baseColor = if (cell.isStoneObstacle) {
                            theme.stoneColor
                        } else {
                            theme.pieceColors[cell.tetrominoType] ?: Color.Cyan
                        }
                        drawStyledCell(
                            topLeft = Offset(c * cellW, r * cellH),
                            cellSize = Size(cellW, cellH),
                            color = baseColor,
                            jewelType = cell.jewelType,
                            isStone = cell.isStoneObstacle,
                            jewelPulse = jewelPulse
                        )
                    }
                }

                // 5. Ghost Piece Preview
                val active = boardState.activePiece
                val ghostRow = boardState.ghostRow
                if (active != null && ghostRow != null && ghostRow != active.row) {
                    val ghostColor = theme.pieceColors[active.type] ?: Color.White
                    active.type.cells(active.rotation).forEach { rel ->
                        val r = ghostRow + rel.row
                        val c = active.col + rel.col
                        if (r in 0 until TetrisEngine.ROWS && c in 0 until TetrisEngine.COLS) {
                            val pad = cellW * 0.08f
                            drawRoundRect(
                                color = ghostColor.copy(alpha = 0.35f),
                                topLeft = Offset(c * cellW + pad, r * cellH + pad),
                                size = Size(cellW - pad * 2, cellH - pad * 2),
                                cornerRadius = CornerRadius(cellW * 0.16f, cellW * 0.16f),
                                style = Stroke(width = 2.2f)
                            )
                        }
                    }
                }

                // 6. Active Falling Piece
                if (active != null) {
                    val pieceColor = theme.pieceColors[active.type] ?: Color.White
                    active.type.cells(active.rotation).forEachIndexed { idx, rel ->
                        val r = active.row + rel.row
                        val c = active.col + rel.col
                        if (r in 0 until TetrisEngine.ROWS && c in 0 until TetrisEngine.COLS) {
                            val jewel = if (active.jewelCellIndex == idx) active.jewelType else JewelType.NONE
                            drawStyledCell(
                                topLeft = Offset(c * cellW, r * cellH),
                                cellSize = Size(cellW, cellH),
                                color = pieceColor,
                                jewelType = jewel,
                                isStone = false,
                                jewelPulse = jewelPulse
                            )
                        }
                    }
                }
            }

            // Floating Power / Combo Effect Banners
            if (effects.isNotEmpty()) {
                val latest = effects.last()
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 18.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xD90E1324))
                        .border(1.dp, latest.color.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = latest.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = latest.color
                    )
                    if (latest.subtitle.isNotEmpty()) {
                        Text(
                            text = latest.subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFF5F1E8)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MiniPiecePreview(
    pieceType: TetrominoType?,
    jewelType: JewelType,
    theme: BoardTheme,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(44.dp)) {
        if (pieceType == null) return@Canvas
        val cells = pieceType.cells(0)
        val minR = cells.minOf { it.row }
        val maxR = cells.maxOf { it.row }
        val minC = cells.minOf { it.col }
        val maxC = cells.maxOf { it.col }
        val spanR = (maxR - minR + 1).coerceAtLeast(1)
        val spanC = (maxC - minC + 1).coerceAtLeast(1)
        val cellDim = (size.minDimension / 4.2f)
        val offsetX = (size.width - spanC * cellDim) / 2f
        val offsetY = (size.height - spanR * cellDim) / 2f
        val color = theme.pieceColors[pieceType] ?: Color.White

        cells.forEachIndexed { idx, pt ->
            val x = offsetX + (pt.col - minC) * cellDim
            val y = offsetY + (pt.row - minR) * cellDim
            val jewel = if (idx == 1 && jewelType != JewelType.NONE) jewelType else JewelType.NONE
            drawStyledCell(
                topLeft = Offset(x, y),
                cellSize = Size(cellDim, cellDim),
                color = color,
                jewelType = jewel,
                isStone = false,
                jewelPulse = 0.9f
            )
        }
    }
}

private fun DrawScope.drawStyledCell(
    topLeft: Offset,
    cellSize: Size,
    color: Color,
    jewelType: JewelType,
    isStone: Boolean,
    jewelPulse: Float
) {
    val pad = cellSize.width * 0.055f
    val x = topLeft.x + pad
    val y = topLeft.y + pad
    val w = (cellSize.width - pad * 2).coerceAtLeast(2f)
    val h = (cellSize.height - pad * 2).coerceAtLeast(2f)
    val radius = CornerRadius(w * 0.18f, w * 0.18f)

    // Base fill with subtle vertical shading
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(
                color.copy(alpha = if (isStone) 0.78f else 0.98f),
                Color(
                    red = (color.red * 0.62f).coerceIn(0f, 1f),
                    green = (color.green * 0.62f).coerceIn(0f, 1f),
                    blue = (color.blue * 0.62f).coerceIn(0f, 1f),
                    alpha = 0.98f
                )
            ),
            start = Offset(x, y),
            end = Offset(x + w, y + h)
        ),
        topLeft = Offset(x, y),
        size = Size(w, h),
        cornerRadius = radius
    )

    // Top-left bevel highlight
    drawRoundRect(
        color = Color.White.copy(alpha = if (isStone) 0.14f else 0.34f),
        topLeft = Offset(x + 1.5f, y + 1.5f),
        size = Size(w - 3f, h * 0.36f),
        cornerRadius = radius
    )

    if (isStone) {
        // Carved stone rune crosshatch
        drawLine(
            color = Color.Black.copy(alpha = 0.32f),
            start = Offset(x + w * 0.25f, y + h * 0.5f),
            end = Offset(x + w * 0.75f, y + h * 0.5f),
            strokeWidth = 2f
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.18f),
            radius = w * 0.1f,
            center = Offset(x + w * 0.5f, y + h * 0.5f)
        )
    }

    // Faceted Jewel Overlay
    if (jewelType != JewelType.NONE) {
        val cx = x + w * 0.5f
        val cy = y + h * 0.5f
        val r = w * 0.34f

        // Outer halo
        drawCircle(
            color = jewelType.glowColor.copy(alpha = 0.42f * jewelPulse),
            radius = w * 0.48f,
            center = Offset(cx, cy)
        )

        val diamond = Path().apply {
            moveTo(cx, cy - r)
            lineTo(cx + r, cy)
            lineTo(cx, cy + r)
            lineTo(cx - r, cy)
            close()
        }
        drawPath(path = diamond, color = jewelType.gemColor)
        drawPath(
            path = diamond,
            color = Color.White.copy(alpha = 0.85f),
            style = Stroke(width = 1.6f)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.92f),
            radius = r * 0.26f,
            center = Offset(cx - r * 0.18f, cy - r * 0.18f)
        )
    }
}

private fun DrawScope.drawBoardMotif(theme: BoardTheme, phase: Float) {
    val accent = theme.frameBorderColor
    when (theme.motif) {
        BoardMotif.SANCTUARY_STARS, BoardMotif.ASTRAL_CONSTELLATION -> {
            val pts = listOf(
                Offset(0.18f, 0.15f), Offset(0.75f, 0.22f),
                Offset(0.32f, 0.44f), Offset(0.82f, 0.58f),
                Offset(0.24f, 0.78f), Offset(0.66f, 0.84f)
            )
            pts.forEachIndexed { idx, rel ->
                val pulse = 0.35f + 0.45f * sin((phase * 2f * PI + idx).toFloat())
                val pos = Offset(rel.x * size.width, rel.y * size.height)
                drawCircle(color = accent.copy(alpha = pulse.coerceIn(0.12f, 0.55f)), radius = 3.2f, center = pos)
            }
            if (theme.motif == BoardMotif.ASTRAL_CONSTELLATION) {
                for (i in 0 until pts.size - 1) {
                    drawLine(
                        color = accent.copy(alpha = 0.11f),
                        start = Offset(pts[i].x * size.width, pts[i].y * size.height),
                        end = Offset(pts[i + 1].x * size.width, pts[i + 1].y * size.height),
                        strokeWidth = 1.2f
                    )
                }
            }
        }
        BoardMotif.SAKURA_PETALS -> {
            for (i in 0 until 7) {
                val y = ((phase + i * 0.14f) % 1f) * size.height
                val x = ((0.12f + i * 0.13f) + 0.04f * sin(phase * 2f * PI + i)).toFloat() * size.width
                drawOval(
                    color = accent.copy(alpha = 0.16f),
                    topLeft = Offset(x, y),
                    size = Size(10f, 6f)
                )
            }
        }
        BoardMotif.JADE_BAMBOO -> {
            for (i in 0 until 5) {
                val y = (0.18f + i * 0.16f + 0.02f * cos(phase * 2f * PI + i)).toFloat() * size.height
                val x = (0.15f + i * 0.17f + 0.03f * sin(phase * 2f * PI + i)).toFloat() * size.width
                drawCircle(
                    color = Color(0x405CE1B8),
                    radius = 5.5f,
                    center = Offset(x, y)
                )
            }
        }
        BoardMotif.CORAL_WAVES -> {
            for (i in 0 until 4) {
                val baseY = size.height * (0.28f + i * 0.2f)
                val path = Path().apply {
                    moveTo(0f, baseY)
                    for (step in 0..8) {
                        val px = (step / 8f) * size.width
                        val py = baseY + 10f * sin((phase * 2f * PI + step * 0.8 + i).toFloat())
                        lineTo(px, py)
                    }
                }
                drawPath(path, color = accent.copy(alpha = 0.10f), style = Stroke(width = 2f))
            }
        }
    }
}
