package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.local.PlayerProgressEntity
import com.example.domain.model.CharacterHero
import com.example.domain.model.GameCatalog
import com.example.domain.model.StoryLevel
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ElevatedSlate
import com.example.ui.theme.LuminousJade
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.MutedMist
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.WarmAlabaster

@Composable
fun AdventureScreen(
    progress: PlayerProgressEntity,
    onOpenCharacterPicker: () -> Unit,
    onSelectLevel: (StoryLevel) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeHero = GameCatalog.characterById(progress.selectedCharacterId)
    val completedSet = progress.completedLevelsSet()

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SelectedCharacterCard(
                hero = activeHero,
                onChangeHero = onOpenCharacterPicker
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Capítulos del Reino",
                        style = MaterialTheme.typography.headlineSmall,
                        color = WarmAlabaster
                    )
                    Text(
                        text = "Dificultad progresiva · Historia adaptada a ${activeHero.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedMist
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ElevatedSlate,
                    border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = "${completedSet.size}/${GameCatalog.storyLevels.size}",
                        style = MaterialTheme.typography.labelMedium,
                        color = ChampagneGold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        items(GameCatalog.storyLevels, key = { it.levelNumber }) { level ->
            val unlocked = level.levelNumber <= progress.maxUnlockedLevel
            val completed = level.levelNumber in completedSet
            StoryLevelCard(
                level = level,
                unlocked = unlocked,
                completed = completed,
                hero = activeHero,
                onClick = { onSelectLevel(level) }
            )
        }
    }
}

@Composable
private fun SelectedCharacterCard(
    hero: CharacterHero,
    onChangeHero: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MidnightSurface),
        border = BorderStroke(1.2.dp, hero.accentColor.copy(alpha = 0.45f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            hero.accentColor.copy(alpha = 0.14f),
                            Color.Transparent
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Image(
                        painter = painterResource(id = hero.portraitResId),
                        contentDescription = "Retrato de ${hero.name}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .border(2.dp, hero.accentColor, CircleShape)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "GUARDIÁN ACTIVO",
                            style = MaterialTheme.typography.labelSmall,
                            color = hero.accentColor
                        )
                        Text(
                            text = "${hero.name}, ${hero.title}",
                            style = MaterialTheme.typography.titleMedium,
                            color = WarmAlabaster,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Diamond,
                                contentDescription = null,
                                tint = hero.affinityJewel.gemColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Afinidad: ${hero.affinityJewel.displayName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedMist
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ElevatedSlate.copy(alpha = 0.85f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 9.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Pasiva · ${hero.passiveTitle}: ${hero.passiveDesc}",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarmAlabaster.copy(alpha = 0.92f)
                        )
                        Text(
                            text = "Reliquia · ${hero.activePowerTitle}: ${hero.activePowerDesc}",
                            style = MaterialTheme.typography.bodySmall,
                            color = hero.accentColor
                        )
                    }
                }

                OutlinedButton(
                    onClick = onChangeHero,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, hero.accentColor.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("change_character_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SwapHoriz,
                        contentDescription = null,
                        tint = hero.accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Escoger Personaje",
                        color = WarmAlabaster
                    )
                }
            }
        }
    }
}

@Composable
private fun StoryLevelCard(
    level: StoryLevel,
    unlocked: Boolean,
    completed: Boolean,
    hero: CharacterHero,
    onClick: () -> Unit
) {
    val borderColor = when {
        completed -> LuminousJade.copy(alpha = 0.55f)
        unlocked -> ChampagneGold.copy(alpha = 0.45f)
        else -> Color(0xFF28304C)
    }
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (unlocked) MidnightSurface else Color(0xFF101422)
        ),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("level_card_${level.levelNumber}")
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (unlocked) ChampagneGold.copy(alpha = 0.16f)
                                else ElevatedSlate
                            )
                    ) {
                        if (unlocked) {
                            Text(
                                text = "${level.levelNumber}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ChampagneGold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Lock,
                                contentDescription = "Bloqueado",
                                tint = MutedMist,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = level.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (unlocked) WarmAlabaster else MutedMist
                        )
                        Text(
                            text = "${level.location} · ${level.difficultyLabel}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (unlocked) hero.accentColor else MutedMist.copy(alpha = 0.7f)
                        )
                    }
                }

                if (completed) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = "Completado",
                        tint = LuminousJade,
                        modifier = Modifier.size(22.dp)
                    )
                } else if (unlocked) {
                    Surface(
                        shape = CircleShape,
                        color = ChampagneGold
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = "Jugar",
                            tint = ObsidianBg,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(18.dp)
                        )
                    }
                }
            }

            Text(
                text = level.generalPrologue,
                style = MaterialTheme.typography.bodySmall,
                color = if (unlocked) MutedMist else MutedMist.copy(alpha = 0.55f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoTag(text = "Meta: ${level.targetLines} líneas")
                InfoTag(text = "+${level.rewardCrystals} ◆")
                level.unlocksThemeId?.let { themeId ->
                    val themeName = GameCatalog.themeById(themeId).name
                    InfoTag(text = "Desbloquea: $themeName")
                }
            }
        }
    }
}

@Composable
private fun InfoTag(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = ElevatedSlate
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = WarmAlabaster.copy(alpha = 0.85f),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun CharacterPickerDialog(
    selectedCharacterId: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MidnightSurface,
            border = BorderStroke(1.2.dp, ChampagneGold.copy(alpha = 0.45f)),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 520.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Elige a tu Personaje",
                    style = MaterialTheme.typography.headlineSmall,
                    color = WarmAlabaster
                )
                Text(
                    text = "Cada guardián transforma la historia de cada nivel y aporta una joya afín y reliquia única.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedMist
                )

                LazyColumn(
                    modifier = Modifier.height(390.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(GameCatalog.characters, key = { it.id }) { hero ->
                        val isSelected = hero.id == selectedCharacterId
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) ElevatedSlate else ObsidianBg
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 1.6.dp else 1.dp,
                                color = if (isSelected) hero.accentColor else Color(0xFF2A3250)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("pick_character_${hero.id}")
                                .clickable { onSelect(hero.id) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = hero.portraitResId),
                                    contentDescription = hero.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .border(1.5.dp, hero.accentColor, CircleShape)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "${hero.name} · ${hero.title}",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = WarmAlabaster
                                        )
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Rounded.CheckCircle,
                                                contentDescription = "Seleccionado",
                                                tint = hero.accentColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = hero.lore,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MutedMist,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${hero.activePowerTitle}: ${hero.activePowerDesc}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = hero.accentColor
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cerrar", color = ChampagneGold)
                    }
                }
            }
        }
    }
}

@Composable
fun StoryPrologueDialog(
    level: StoryLevel,
    hero: CharacterHero,
    onStartLevel: () -> Unit,
    onDismiss: () -> Unit
) {
    val voiceLine = level.characterVoices[hero.id] ?: level.generalPrologue
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MidnightSurface,
            border = BorderStroke(1.5.dp, hero.accentColor.copy(alpha = 0.55f)),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Image(
                        painter = painterResource(id = hero.portraitResId),
                        contentDescription = hero.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .border(2.dp, hero.accentColor, CircleShape)
                    )
                    Column {
                        Text(
                            text = "CAPÍTULO ${level.levelNumber} · ${level.location.uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = ChampagneGold
                        )
                        Text(
                            text = level.title,
                            style = MaterialTheme.typography.headlineSmall,
                            color = WarmAlabaster
                        )
                    }
                }

                Text(
                    text = level.generalPrologue,
                    style = MaterialTheme.typography.bodyMedium,
                    color = WarmAlabaster.copy(alpha = 0.9f)
                )

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = ElevatedSlate,
                    border = BorderStroke(1.dp, hero.accentColor.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = hero.accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = voiceLine,
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = WarmAlabaster
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    InfoTag(text = "Meta: ${level.targetLines} líneas")
                    InfoTag(text = "Dificultad: ${level.difficultyLabel}")
                    if (level.initialStoneRows > 0) {
                        InfoTag(text = "${level.initialStoneRows} filas piedra")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Volver", color = MutedMist)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onStartLevel,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ChampagneGold,
                            contentColor = ObsidianBg
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("start_level_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Jugar Capítulo", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
