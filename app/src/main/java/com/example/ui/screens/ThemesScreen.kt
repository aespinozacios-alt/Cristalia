package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.PlayerProgressEntity
import com.example.domain.model.BoardTheme
import com.example.domain.model.GameCatalog
import com.example.domain.model.TetrominoType
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ElevatedSlate
import com.example.ui.theme.LuminousJade
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.MutedMist
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.WarmAlabaster

@Composable
fun ThemesScreen(
    progress: PlayerProgressEntity,
    onSelectOrUnlockTheme: (BoardTheme) -> Unit,
    modifier: Modifier = Modifier
) {
    val unlockedSet = progress.unlockedThemesSet()
    var previewThemeId by remember(progress.selectedThemeId) {
        mutableStateOf(progress.selectedThemeId)
    }
    val previewTheme = GameCatalog.themeById(previewThemeId)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Estéticas del Tablero",
                    style = MaterialTheme.typography.headlineSmall,
                    color = WarmAlabaster
                )
                Text(
                    text = "Desbloquea nuevos ambientes completando capítulos o intercambiando cristales.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedMist
                )
            }
        }

        item {
            ThemeLivePreviewBanner(
                theme = previewTheme,
                isEquipped = previewTheme.id == progress.selectedThemeId
            )
        }

        items(GameCatalog.boardThemes, key = { it.id }) { theme ->
            val unlocked = unlockedSet.contains(theme.id)
            val equipped = progress.selectedThemeId == theme.id
            val canAfford = progress.crystalsBalance >= theme.unlockCostCrystals

            ThemeOptionCard(
                theme = theme,
                unlocked = unlocked,
                equipped = equipped,
                isPreviewing = previewThemeId == theme.id,
                canAfford = canAfford,
                onPreview = { previewThemeId = theme.id },
                onAction = { onSelectOrUnlockTheme(theme) }
            )
        }
    }
}

@Composable
private fun ThemeLivePreviewBanner(
    theme: BoardTheme,
    isEquipped: Boolean
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.5.dp, theme.frameBorderColor.copy(alpha = 0.65f)),
        colors = CardDefaults.cardColors(containerColor = MidnightSurface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(theme.bgTop, theme.bgBottom)
                    )
                )
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mini board sample
            Canvas(
                modifier = Modifier
                    .size(width = 96.dp, height = 104.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.2.dp, theme.frameBorderColor, RoundedCornerShape(12.dp))
            ) {
                drawRect(brush = Brush.verticalGradient(listOf(theme.bgTop, theme.bgBottom)))
                val cols = 6
                val rows = 6
                val cw = size.width / cols
                val ch = size.height / rows
                for (c in 1 until cols) {
                    drawLine(theme.gridLineColor, Offset(c * cw, 0f), Offset(c * cw, size.height), 1f)
                }
                for (r in 1 until rows) {
                    drawLine(theme.gridLineColor, Offset(0f, r * ch), Offset(size.width, r * ch), 1f)
                }

                val sampleBlocks = listOf(
                    Triple(4, 0, TetrominoType.L),
                    Triple(5, 0, TetrominoType.L),
                    Triple(5, 1, TetrominoType.L),
                    Triple(5, 2, TetrominoType.J),
                    Triple(5, 3, TetrominoType.J),
                    Triple(4, 3, TetrominoType.T),
                    Triple(4, 4, TetrominoType.T),
                    Triple(4, 5, TetrominoType.T),
                    Triple(3, 4, TetrominoType.T),
                    Triple(2, 1, TetrominoType.O),
                    Triple(2, 2, TetrominoType.O),
                    Triple(1, 1, TetrominoType.O),
                    Triple(1, 2, TetrominoType.O)
                )
                sampleBlocks.forEach { (r, c, type) ->
                    val col = theme.pieceColors[type] ?: Color.Cyan
                    drawRoundRect(
                        color = col,
                        topLeft = Offset(c * cw + 2f, r * ch + 2f),
                        size = Size(cw - 4f, ch - 4f),
                        cornerRadius = CornerRadius(4f, 4f)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.Palette,
                        contentDescription = null,
                        tint = theme.frameBorderColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (isEquipped) "ACTIVA EN TABLERO" else "VISTA PREVIA",
                        style = MaterialTheme.typography.labelSmall,
                        color = theme.frameBorderColor
                    )
                }
                Text(
                    text = theme.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = WarmAlabaster
                )
                Text(
                    text = theme.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedMist
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TetrominoType.entries.take(5).forEach { type ->
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(theme.pieceColors[type] ?: Color.White)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeOptionCard(
    theme: BoardTheme,
    unlocked: Boolean,
    equipped: Boolean,
    isPreviewing: Boolean,
    canAfford: Boolean,
    onPreview: () -> Unit,
    onAction: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurface),
        border = BorderStroke(
            width = if (isPreviewing || equipped) 1.5.dp else 1.dp,
            color = when {
                equipped -> LuminousJade
                isPreviewing -> theme.frameBorderColor
                else -> Color(0xFF28304C)
            }
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onPreview)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.verticalGradient(listOf(theme.bgTop, theme.bgBottom)))
                    .border(1.5.dp, theme.frameBorderColor, RoundedCornerShape(12.dp))
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = theme.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = WarmAlabaster
                )
                Text(
                    text = if (unlocked) theme.subtitle else "Cap. ${theme.unlockLevelNumber} o ${theme.unlockCostCrystals} cristales",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedMist
                )
            }

            when {
                equipped -> {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = LuminousJade.copy(alpha = 0.16f),
                        border = BorderStroke(1.dp, LuminousJade.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CheckCircle,
                                contentDescription = "Equipada",
                                tint = LuminousJade,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Equipada",
                                style = MaterialTheme.typography.labelSmall,
                                color = LuminousJade
                            )
                        }
                    }
                }
                unlocked -> {
                    OutlinedButton(
                        onClick = onAction,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, theme.frameBorderColor.copy(alpha = 0.7f)),
                        modifier = Modifier.testTag("equip_theme_${theme.id}")
                    ) {
                        Text("Equipar", color = WarmAlabaster)
                    }
                }
                else -> {
                    Button(
                        onClick = onAction,
                        enabled = canAfford,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ChampagneGold,
                            contentColor = ObsidianBg,
                            disabledContainerColor = ElevatedSlate,
                            disabledContentColor = MutedMist
                        ),
                        modifier = Modifier.testTag("unlock_theme_${theme.id}")
                    ) {
                        Icon(
                            imageVector = if (canAfford) Icons.Rounded.LockOpen else Icons.Rounded.Diamond,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${theme.unlockCostCrystals}",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
