package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.PlayerProgressEntity
import com.example.domain.model.GameCatalog
import com.example.domain.model.JewelType
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ElevatedSlate
import com.example.ui.theme.LuminousJade
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.MutedMist
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.WarmAlabaster

@Composable
fun SoundsAndGuideScreen(
    progress: PlayerProgressEntity,
    onToggleAmbient: (Boolean) -> Unit,
    onToggleSfx: (Boolean) -> Unit,
    onSelectTrack: (String) -> Unit,
    onSavePlayerName: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var aliasInput by remember(progress.playerName) { mutableStateOf(progress.playerName) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Ambiente y Joyas",
                    style = MaterialTheme.typography.headlineSmall,
                    color = WarmAlabaster
                )
                Text(
                    text = "Personaliza los sonidos relajantes, tu nombre de guardián y consulta los poderes.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedMist
                )
            }
        }

        // Player Profile Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightSurface),
                border = BorderStroke(1.dp, Color(0xFF2A3250)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = null,
                            tint = ChampagneGold
                        )
                        Text(
                            text = "Nombre para la Clasificación",
                            style = MaterialTheme.typography.titleMedium,
                            color = WarmAlabaster
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = aliasInput,
                            onValueChange = { aliasInput = it.take(20) },
                            singleLine = true,
                            placeholder = { Text("Guardián") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ChampagneGold,
                                unfocusedBorderColor = Color(0xFF343E63),
                                focusedTextColor = WarmAlabaster,
                                unfocusedTextColor = WarmAlabaster
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("player_name_input")
                        )
                        Button(
                            onClick = { onSavePlayerName(aliasInput) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ChampagneGold,
                                contentColor = ObsidianBg
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(52.dp)
                        ) {
                            Text("Guardar", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Relaxing Audio Settings Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightSurface),
                border = BorderStroke(1.dp, LuminousJade.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.MusicNote,
                            contentDescription = null,
                            tint = LuminousJade
                        )
                        Text(
                            text = "Paisajes Sonoros Relajantes",
                            style = MaterialTheme.typography.titleMedium,
                            color = WarmAlabaster
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Música ambiental continua",
                                style = MaterialTheme.typography.bodyLarge,
                                color = WarmAlabaster
                            )
                            Text(
                                text = "Acordes suaves y campanillas generadas en tiempo real",
                                style = MaterialTheme.typography.bodySmall,
                                color = MutedMist
                            )
                        }
                        Switch(
                            checked = progress.ambientEnabled,
                            onCheckedChange = onToggleAmbient,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ObsidianBg,
                                checkedTrackColor = LuminousJade
                            ),
                            modifier = Modifier.testTag("ambient_switch")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.VolumeUp,
                                contentDescription = null,
                                tint = MutedMist,
                                modifier = Modifier.size(18.dp)
                            )
                            Column {
                                Text(
                                    text = "Efectos cristalinos",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = WarmAlabaster
                                )
                                Text(
                                    text = "Toques de marimba al girar, encajar y activar joyas",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MutedMist
                                )
                            }
                        }
                        Switch(
                            checked = progress.sfxEnabled,
                            onCheckedChange = onToggleSfx,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ObsidianBg,
                                checkedTrackColor = ChampagneGold
                            ),
                            modifier = Modifier.testTag("sfx_switch")
                        )
                    }

                    Text(
                        text = "Elige tu atmósfera",
                        style = MaterialTheme.typography.labelLarge,
                        color = ChampagneGold
                    )

                    GameCatalog.ambientTracks.forEach { track ->
                        val selected = track.id == progress.selectedAmbientTrackId
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selected) ElevatedSlate else ObsidianBg
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (selected) LuminousJade else Color(0xFF28304C)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectTrack(track.id) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (selected) LuminousJade.copy(alpha = 0.2f)
                                            else ElevatedSlate
                                        )
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.GraphicEq,
                                        contentDescription = null,
                                        tint = if (selected) LuminousJade else MutedMist,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = track.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = WarmAlabaster
                                    )
                                    Text(
                                        text = track.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MutedMist
                                    )
                                }
                                if (selected) {
                                    Icon(
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = "Activa",
                                        tint = LuminousJade,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Jewel Superpowers Guide
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightSurface),
                border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Diamond,
                            contentDescription = null,
                            tint = ChampagneGold
                        )
                        Text(
                            text = "Superpoderes de Joyas",
                            style = MaterialTheme.typography.titleMedium,
                            color = WarmAlabaster
                        )
                    }
                    Text(
                        text = "Algunas piezas contienen una joya brillante. Completa la fila donde repose la joya para liberar su superpoder:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedMist
                    )

                    listOf(
                        JewelType.RUBY,
                        JewelType.SAPPHIRE,
                        JewelType.EMERALD,
                        JewelType.AMETHYST
                    ).forEach { jewel ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(ElevatedSlate)
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(jewel.gemColor.copy(alpha = 0.2f))
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Diamond,
                                    contentDescription = jewel.displayName,
                                    tint = jewel.gemColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${jewel.displayName} · ${jewel.powerTitle}",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = jewel.glowColor
                                )
                                Text(
                                    text = jewel.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = WarmAlabaster.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Touch Controls Guide
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MidnightSurface),
                border = BorderStroke(1.dp, Color(0xFF28304C)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.TouchApp,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(28.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Controles en Partida",
                            style = MaterialTheme.typography.titleSmall,
                            color = WarmAlabaster
                        )
                        Text(
                            text = "Toca el tablero para rotar, desliza horizontalmente para mover, hacia abajo para acelerar, o usa los botones táctiles inferiores y el botón de Reliquia.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MutedMist
                        )
                    }
                }
            }
        }
    }
}
