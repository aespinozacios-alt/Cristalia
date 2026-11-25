package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.local.LeaderboardEntity
import com.example.domain.model.GameCatalog
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ElevatedSlate
import com.example.ui.theme.LuminousJade
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.MutedMist
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.WarmAlabaster
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LeaderboardScreen(
    entries: List<LeaderboardEntity>,
    selectedLevelFilter: Int?,
    onSelectLevelFilter: (Int?) -> Unit,
    onClearLeaderboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showClearDialog by remember { mutableStateOf(false) }

    val filtered = remember(entries, selectedLevelFilter) {
        if (selectedLevelFilter == null) entries
        else entries.filter { it.levelNumber == selectedLevelFilter }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tabla de Clasificaciones",
                        style = MaterialTheme.typography.headlineSmall,
                        color = WarmAlabaster
                    )
                    Text(
                        text = "Puntuaciones locales guardadas en este dispositivo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedMist
                    )
                }
                if (entries.isNotEmpty()) {
                    IconButton(
                        onClick = { showClearDialog = true },
                        modifier = Modifier.testTag("clear_leaderboard_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DeleteSweep,
                            contentDescription = "Limpiar clasificación",
                            tint = MutedMist
                        )
                    }
                }
            }
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedLevelFilter == null,
                        onClick = { onSelectLevelFilter(null) },
                        label = { Text("Todos") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ChampagneGold,
                            selectedLabelColor = ObsidianBg
                        )
                    )
                }
                items(GameCatalog.storyLevels, key = { it.levelNumber }) { lvl ->
                    FilterChip(
                        selected = selectedLevelFilter == lvl.levelNumber,
                        onClick = { onSelectLevelFilter(lvl.levelNumber) },
                        label = { Text("Cap. ${lvl.levelNumber}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ChampagneGold,
                            selectedLabelColor = ObsidianBg
                        )
                    )
                }
            }
        }

        if (filtered.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MidnightSurface),
                    border = BorderStroke(1.dp, Color(0xFF2B3350)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.EmojiEvents,
                            contentDescription = null,
                            tint = ChampagneGold.copy(alpha = 0.7f),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Sin marcas registradas aún",
                            style = MaterialTheme.typography.titleMedium,
                            color = WarmAlabaster
                        )
                        Text(
                            text = "Completa una partida en cualquier capítulo para inscribir tu puntuación y personaje en la clasificación local.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedMist,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            itemsIndexed(filtered, key = { _, item -> item.id }) { index, entry ->
                LeaderboardRowCard(rank = index + 1, entry = entry)
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            containerColor = MidnightSurface,
            title = { Text("¿Reiniciar clasificación?", color = WarmAlabaster) },
            text = {
                Text(
                    "Se eliminarán todas las puntuaciones locales guardadas. Tus niveles desbloqueados y cristales se conservarán.",
                    color = MutedMist
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearLeaderboard()
                        showClearDialog = false
                    }
                ) {
                    Text("Reiniciar", color = Color(0xFFFF7588))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancelar", color = ChampagneGold)
                }
            }
        )
    }
}

@Composable
private fun LeaderboardRowCard(
    rank: Int,
    entry: LeaderboardEntity
) {
    val hero = GameCatalog.characterById(entry.characterId)
    val medalColor = when (rank) {
        1 -> Color(0xFFFFD66B)
        2 -> Color(0xFFD5DCE8)
        3 -> Color(0xFFDF9E72)
        else -> MutedMist
    }
    val dateFormat = remember { SimpleDateFormat("dd MMM", Locale("es", "ES")) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (rank <= 3) ElevatedSlate else MidnightSurface
        ),
        border = BorderStroke(
            width = if (rank == 1) 1.5.dp else 1.dp,
            color = if (rank <= 3) medalColor.copy(alpha = 0.5f) else Color(0xFF28304C)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.width(34.dp)
            ) {
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = medalColor
                )
            }

            Image(
                painter = painterResource(id = hero.portraitResId),
                contentDescription = hero.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, hero.accentColor, CircleShape)
            )

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = entry.playerName,
                        style = MaterialTheme.typography.titleSmall,
                        color = WarmAlabaster,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (entry.isVictory) {
                        Icon(
                            imageVector = Icons.Rounded.Verified,
                            contentDescription = "Capítulo superado",
                            tint = LuminousJade,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Text(
                    text = "${hero.name} · Cap. ${entry.levelNumber} · ${entry.linesCleared} líneas · ${dateFormat.format(Date(entry.timestamp))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedMist,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${entry.score}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ChampagneGold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Diamond,
                        contentDescription = null,
                        tint = hero.affinityJewel.gemColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "${entry.jewelsTriggered}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedMist
                    )
                }
            }
        }
    }
}
