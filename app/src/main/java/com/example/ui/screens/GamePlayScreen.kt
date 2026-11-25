package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.MusicOff
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.RotateRight
import androidx.compose.material.icons.rounded.VerticalAlignBottom
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.domain.model.GameCatalog
import com.example.domain.model.JewelType
import com.example.ui.GameSessionUiState
import com.example.ui.components.MiniPiecePreview
import com.example.ui.components.TetrisCanvasBoard
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ElevatedSlate
import com.example.ui.theme.LuminousJade
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.MutedMist
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.WarmAlabaster

@Composable
fun GamePlayScreen(
    session: GameSessionUiState,
    ambientEnabled: Boolean,
    onToggleAmbient: () -> Unit,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onRotate: () -> Unit,
    onSoftDrop: () -> Unit,
    onHardDrop: () -> Unit,
    onHold: () -> Unit,
    onActivateRelic: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onContinueEndless: () -> Unit,
    onNextLevel: () -> Unit,
    onExitToHub: () -> Unit
) {
    val board = session.boardState ?: return
    val haptic = LocalHapticFeedback.current

    BackHandler {
        when {
            session.showVictoryModal || session.showGameOverModal -> onExitToHub()
            session.isPaused -> onResume()
            else -> onPause()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ObsidianBg,
                        session.boardTheme.bgBottom.copy(alpha = 0.55f),
                        ObsidianBg
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 560.dp)
                .align(Alignment.Center)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. Top HUD Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onPause,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MidnightSurface)
                        .testTag("pause_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Pause,
                        contentDescription = "Pausar partida",
                        tint = WarmAlabaster
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (board.endlessContinued) {
                                "Cap. ${session.level.levelNumber} · Infinito"
                            } else {
                                "Cap. ${session.level.levelNumber} · ${session.level.title}"
                            },
                            style = MaterialTheme.typography.titleSmall,
                            color = WarmAlabaster,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (board.endlessContinued) {
                                "${board.linesCleared} lín."
                            } else {
                                "${board.linesCleared}/${session.level.targetLines}"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = LuminousJade
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    val progressFraction = if (board.endlessContinued) {
                        1f
                    } else {
                        (board.linesCleared.toFloat() / session.level.targetLines.coerceAtLeast(1).toFloat())
                            .coerceIn(0f, 1f)
                    }
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        color = session.boardTheme.frameBorderColor,
                        trackColor = ElevatedSlate,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MidnightSurface,
                    border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "PUNTOS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedMist
                        )
                        Text(
                            text = "${board.score}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = ChampagneGold
                        )
                    }
                }

                IconButton(
                    onClick = onToggleAmbient,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MidnightSurface)
                        .testTag("game_audio_toggle")
                ) {
                    Icon(
                        imageVector = if (ambientEnabled) Icons.Rounded.MusicNote else Icons.Rounded.MusicOff,
                        contentDescription = if (ambientEnabled) "Silenciar música" else "Activar música",
                        tint = if (ambientEnabled) LuminousJade else MutedMist
                    )
                }
            }

            // 2. Hold, Relic Power, and Next Piece Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hold Piece Slot
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MidnightSurface,
                    border = BorderStroke(1.dp, Color(0xFF2B3352)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable(enabled = board.canHoldThisTurn) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onHold()
                        }
                        .testTag("hold_piece_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "RESERVA",
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedMist
                            )
                            Text(
                                text = if (board.holdPieceType == null) "Vacío" else "Tocar",
                                style = MaterialTheme.typography.labelSmall,
                                color = ChampagneGold
                            )
                        }
                        MiniPiecePreview(
                            pieceType = board.holdPieceType,
                            jewelType = board.holdPieceJewel,
                            theme = session.boardTheme
                        )
                    }
                }

                // Character Relic Active Power Button
                val relicReady = board.relicCharge >= 100
                val relicBorder by animateColorAsState(
                    targetValue = if (relicReady) session.character.accentColor else Color(0xFF2D3659),
                    label = "relic_border"
                )
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (relicReady) session.character.accentColor.copy(alpha = 0.22f) else MidnightSurface,
                    border = BorderStroke(if (relicReady) 1.6.dp else 1.dp, relicBorder),
                    modifier = Modifier
                        .clickable(enabled = relicReady) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onActivateRelic()
                        }
                        .testTag("relic_power_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(40.dp)) {
                            CircularProgressIndicator(
                                progress = { board.relicCharge / 100f },
                                color = session.character.accentColor,
                                trackColor = ElevatedSlate,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(40.dp)
                            )
                            Image(
                                painter = painterResource(id = session.character.portraitResId),
                                contentDescription = session.character.activePowerTitle,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                            )
                        }
                        Column {
                            Text(
                                text = if (relicReady) "¡RELIQUIA!" else "RELIQUIA",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (relicReady) session.character.accentColor else MutedMist
                            )
                            Text(
                                text = if (relicReady) "Activar" else "${board.relicCharge}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = WarmAlabaster
                            )
                        }
                    }
                }

                // Next Piece Slot
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MidnightSurface,
                    border = BorderStroke(1.dp, Color(0xFF2B3352)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "SIGUIENTE",
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedMist
                            )
                            if (board.nextPieceJewel != JewelType.NONE) {
                                Text(
                                    text = board.nextPieceJewel.displayName.substringBefore(" "),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = board.nextPieceJewel.glowColor
                                )
                            } else {
                                Text(
                                    text = session.boardTheme.name.substringBefore(" "),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MutedMist
                                )
                            }
                        }
                        MiniPiecePreview(
                            pieceType = board.nextPieceType,
                            jewelType = board.nextPieceJewel,
                            theme = session.boardTheme
                        )
                    }
                }
            }

            // Slow-Mo status pill
            if (board.slowMoRemainingMs > 0L) {
                Surface(
                    shape = CircleShape,
                    color = JewelType.EMERALD.gemColor.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, JewelType.EMERALD.gemColor.copy(alpha = 0.6f)),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = "Calma Temporal activa · ${(board.slowMoRemainingMs / 1000L) + 1}s",
                        style = MaterialTheme.typography.labelSmall,
                        color = JewelType.EMERALD.glowColor,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            // 3. Main Playable Tetris Canvas Board
            TetrisCanvasBoard(
                boardState = board,
                theme = session.boardTheme,
                effects = session.activeEffects,
                onTapRotate = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onRotate()
                },
                onMoveLeft = onMoveLeft,
                onMoveRight = onMoveRight,
                onSoftDrop = onSoftDrop,
                onHardDrop = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onHardDrop()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            // 4. Tactile Ergonomic Control Deck
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp, bottom = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ControlDeckButton(
                    icon = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                    contentDesc = "Mover izquierda",
                    tag = "ctrl_left",
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onMoveLeft()
                    },
                    modifier = Modifier.weight(1f)
                )
                ControlDeckButton(
                    icon = Icons.Rounded.RotateRight,
                    contentDesc = "Rotar pieza",
                    tag = "ctrl_rotate",
                    accent = ChampagneGold,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onRotate()
                    },
                    modifier = Modifier.weight(1f)
                )
                ControlDeckButton(
                    icon = Icons.Rounded.KeyboardArrowDown,
                    contentDesc = "Bajar suave",
                    tag = "ctrl_down",
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onSoftDrop()
                    },
                    modifier = Modifier.weight(1f)
                )
                ControlDeckButton(
                    icon = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDesc = "Mover derecha",
                    tag = "ctrl_right",
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onMoveRight()
                    },
                    modifier = Modifier.weight(1f)
                )
                ControlDeckButton(
                    icon = Icons.Rounded.VerticalAlignBottom,
                    contentDesc = "Caída rápida",
                    tag = "ctrl_drop",
                    accent = LuminousJade,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onHardDrop()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Modals
        if (session.isPaused) {
            PauseOverlayDialog(
                levelTitle = session.level.title,
                onResume = onResume,
                onRestart = onRestart,
                onExit = onExitToHub
            )
        }

        if (session.showVictoryModal) {
            VictoryEpilogueDialog(
                session = session,
                hasNextLevel = session.level.levelNumber < GameCatalog.storyLevels.size,
                onNextLevel = onNextLevel,
                onContinueEndless = onContinueEndless,
                onExit = onExitToHub
            )
        }

        if (session.showGameOverModal) {
            GameOverDialog(
                session = session,
                onRestart = onRestart,
                onExit = onExitToHub
            )
        }
    }
}

@Composable
private fun ControlDeckButton(
    icon: ImageVector,
    contentDesc: String,
    tag: String,
    accent: Color = WarmAlabaster,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MidnightSurface,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.35f)),
        modifier = modifier
            .height(54.dp)
            .testTag(tag)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDesc,
                tint = accent,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun PauseOverlayDialog(
    levelTitle: String,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    Dialog(onDismissRequest = onResume) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MidnightSurface,
            border = BorderStroke(1.2.dp, ChampagneGold.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 400.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Partida en Pausa",
                    style = MaterialTheme.typography.headlineSmall,
                    color = WarmAlabaster
                )
                Text(
                    text = levelTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedMist
                )
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = onResume,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChampagneGold,
                        contentColor = ObsidianBg
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Rounded.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reanudar", fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onRestart,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Rounded.Refresh, contentDescription = null, tint = WarmAlabaster)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reiniciar Capítulo", color = WarmAlabaster)
                }
                TextButton(
                    onClick = onExit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver al Santuario", color = MutedMist)
                }
            }
        }
    }
}

@Composable
private fun VictoryEpilogueDialog(
    session: GameSessionUiState,
    hasNextLevel: Boolean,
    onNextLevel: () -> Unit,
    onContinueEndless: () -> Unit,
    onExit: () -> Unit
) {
    val board = session.boardState
    val unlockedThemeName = session.level.unlocksThemeId?.let { GameCatalog.themeById(it).name }

    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MidnightSurface,
            border = BorderStroke(1.5.dp, LuminousJade.copy(alpha = 0.7f)),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 440.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(36.dp)
                )
                Text(
                    text = "¡Capítulo Completado!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = WarmAlabaster,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = session.level.epilogue,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedMist,
                    textAlign = TextAlign.Center
                )

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = ElevatedSlate,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StatLine("Puntuación", "${board?.score ?: 0}", ChampagneGold)
                        StatLine("Líneas despejadas", "${board?.linesCleared ?: 0}", WarmAlabaster)
                        StatLine("Joyas activadas", "${board?.jewelsTriggeredCount ?: 0}", session.character.accentColor)
                        StatLine("Cristales ganados", "+${session.lastEarnedCrystals} ◆", LuminousJade)
                        if (unlockedThemeName != null) {
                            StatLine("Nueva estética", unlockedThemeName, ChampagneGold)
                        }
                    }
                }

                if (hasNextLevel) {
                    Button(
                        onClick = onNextLevel,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ChampagneGold,
                            contentColor = ObsidianBg
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Siguiente Capítulo", fontWeight = FontWeight.Bold)
                    }
                }
                OutlinedButton(
                    onClick = onContinueEndless,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Seguir en Modo Infinito", color = LuminousJade)
                }
                TextButton(
                    onClick = onExit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar y Volver", color = MutedMist)
                }
            }
        }
    }
}

@Composable
private fun GameOverDialog(
    session: GameSessionUiState,
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    val board = session.boardState
    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MidnightSurface,
            border = BorderStroke(1.5.dp, ChampagneGold.copy(alpha = 0.55f)),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Diamond,
                    contentDescription = null,
                    tint = session.character.accentColor,
                    modifier = Modifier.size(34.dp)
                )
                Text(
                    text = "Fin de la Partida",
                    style = MaterialTheme.typography.headlineSmall,
                    color = WarmAlabaster
                )
                Text(
                    text = "Tu marca se ha guardado en la clasificación local.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedMist,
                    textAlign = TextAlign.Center
                )
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = ElevatedSlate,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StatLine("Puntuación final", "${board?.score ?: 0}", ChampagneGold)
                        StatLine("Líneas despejadas", "${board?.linesCleared ?: 0}", WarmAlabaster)
                        StatLine("Joyas activadas", "${board?.jewelsTriggeredCount ?: 0}", session.character.accentColor)
                        StatLine("Cristales obtenidos", "+${session.lastEarnedCrystals} ◆", LuminousJade)
                    }
                }
                Button(
                    onClick = onRestart,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChampagneGold,
                        contentColor = ObsidianBg
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reintentar Capítulo", fontWeight = FontWeight.Bold)
                }
                TextButton(
                    onClick = onExit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver al Santuario", color = MutedMist)
                }
            }
        }
    }
}

@Composable
private fun StatLine(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MutedMist)
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}
