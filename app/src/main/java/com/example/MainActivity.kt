package com.example

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Diamond
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.MusicOff
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.domain.model.GameCatalog
import com.example.ui.CristaliaViewModel
import com.example.ui.HubTab
import com.example.ui.screens.AdventureScreen
import com.example.ui.screens.CharacterPickerDialog
import com.example.ui.screens.GamePlayScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.SoundsAndGuideScreen
import com.example.ui.screens.StoryPrologueDialog
import com.example.ui.screens.ThemesScreen
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.CristaliaTheme
import com.example.ui.theme.ElevatedSlate
import com.example.ui.theme.LuminousJade
import com.example.ui.theme.MidnightSurface
import com.example.ui.theme.MutedMist
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.WarmAlabaster

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        )
        setContent {
            CristaliaTheme {
                CristaliaAppRoot()
            }
        }
    }
}

private data class NavTabItem(
    val tab: HubTab,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
)

@Composable
fun CristaliaAppRoot(viewModel: CristaliaViewModel = viewModel()) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.onAppResume()
                Lifecycle.Event.ON_PAUSE -> viewModel.onAppPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val progress by viewModel.progress.collectAsStateWithLifecycle()
    val leaderboard by viewModel.leaderboard.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val leaderboardFilter by viewModel.selectedLeaderboardFilterLevel.collectAsStateWithLifecycle()
    val prologueLevel by viewModel.prologueLevel.collectAsStateWithLifecycle()
    val showCharacterPicker by viewModel.showCharacterPicker.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val gameSession by viewModel.gameState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(toastMessage) {
        val msg = toastMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.dismissToast()
    }

    if (gameSession.isPlayingScreen) {
        GamePlayScreen(
            session = gameSession,
            ambientEnabled = progress.ambientEnabled,
            onToggleAmbient = { viewModel.toggleAmbient(!progress.ambientEnabled) },
            onMoveLeft = viewModel::moveLeft,
            onMoveRight = viewModel::moveRight,
            onRotate = viewModel::rotate,
            onSoftDrop = viewModel::softDrop,
            onHardDrop = viewModel::hardDrop,
            onHold = viewModel::holdPiece,
            onActivateRelic = viewModel::activateCharacterRelic,
            onPause = viewModel::pauseGame,
            onResume = viewModel::resumeGame,
            onRestart = viewModel::restartCurrentLevel,
            onContinueEndless = viewModel::continueEndlessAfterVictory,
            onNextLevel = viewModel::goToNextLevelAfterVictory,
            onExitToHub = viewModel::exitGameToHub
        )
        return
    }

    if (selectedTab != HubTab.AVENTURA) {
        BackHandler {
            viewModel.selectTab(HubTab.AVENTURA)
        }
    }

    val tabs = remember {
        listOf(
            NavTabItem(HubTab.AVENTURA, "Aventura", Icons.Rounded.AutoStories, Icons.Outlined.AutoStories, "tab_aventura"),
            NavTabItem(HubTab.ESTETICAS, "Estéticas", Icons.Rounded.Palette, Icons.Outlined.Palette, "tab_esteticas"),
            NavTabItem(HubTab.CLASIFICACION, "Ranking", Icons.Rounded.EmojiEvents, Icons.Outlined.EmojiEvents, "tab_ranking"),
            NavTabItem(HubTab.SONIDOS, "Sonidos", Icons.Rounded.MusicNote, Icons.Outlined.MusicNote, "tab_sonidos")
        )
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useRail = maxWidth >= 600.dp

        Scaffold(
            containerColor = ObsidianBg,
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                CristaliaTopBar(
                    crystals = progress.crystalsBalance,
                    ambientEnabled = progress.ambientEnabled,
                    onToggleAmbient = { viewModel.toggleAmbient(!progress.ambientEnabled) }
                )
            },
            bottomBar = {
                if (!useRail) {
                    NavigationBar(
                        containerColor = MidnightSurface,
                        tonalElevation = 0.dp
                    ) {
                        tabs.forEach { item ->
                            val selected = selectedTab == item.tab
                            NavigationBarItem(
                                selected = selected,
                                onClick = { viewModel.selectTab(item.tab) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label
                                    )
                                },
                                label = { Text(item.label, maxLines = 1) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = ObsidianBg,
                                    selectedTextColor = ChampagneGold,
                                    indicatorColor = ChampagneGold,
                                    unselectedIconColor = MutedMist,
                                    unselectedTextColor = MutedMist
                                ),
                                modifier = Modifier.testTag(item.tag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(ObsidianBg, ColorTokens.DeepCenter, ObsidianBg)
                        )
                    )
                    .padding(innerPadding)
            ) {
                if (useRail) {
                    NavigationRail(
                        containerColor = MidnightSurface,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        tabs.forEach { item ->
                            val selected = selectedTab == item.tab
                            NavigationRailItem(
                                selected = selected,
                                onClick = { viewModel.selectTab(item.tab) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label
                                    )
                                },
                                label = { Text(item.label) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = ObsidianBg,
                                    selectedTextColor = ChampagneGold,
                                    indicatorColor = ChampagneGold,
                                    unselectedIconColor = MutedMist,
                                    unselectedTextColor = MutedMist
                                ),
                                modifier = Modifier.testTag(item.tag)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = 680.dp)
                    ) {
                        when (selectedTab) {
                            HubTab.AVENTURA -> AdventureScreen(
                                progress = progress,
                                onOpenCharacterPicker = viewModel::openCharacterPicker,
                                onSelectLevel = viewModel::openLevelPrologue
                            )
                            HubTab.ESTETICAS -> ThemesScreen(
                                progress = progress,
                                onSelectOrUnlockTheme = viewModel::selectOrUnlockTheme
                            )
                            HubTab.CLASIFICACION -> LeaderboardScreen(
                                entries = leaderboard,
                                selectedLevelFilter = leaderboardFilter,
                                onSelectLevelFilter = viewModel::setLeaderboardFilter,
                                onClearLeaderboard = viewModel::clearLeaderboard
                            )
                            HubTab.SONIDOS -> SoundsAndGuideScreen(
                                progress = progress,
                                onToggleAmbient = viewModel::toggleAmbient,
                                onToggleSfx = viewModel::toggleSfx,
                                onSelectTrack = viewModel::selectAmbientTrack,
                                onSavePlayerName = viewModel::savePlayerNameAndUpdate
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCharacterPicker) {
        CharacterPickerDialog(
            selectedCharacterId = progress.selectedCharacterId,
            onSelect = viewModel::selectCharacter,
            onDismiss = viewModel::closeCharacterPicker
        )
    }

    prologueLevel?.let { level ->
        val activeHero = GameCatalog.characterById(progress.selectedCharacterId)
        StoryPrologueDialog(
            level = level,
            hero = activeHero,
            onStartLevel = { viewModel.startLevelFromPrologue(level) },
            onDismiss = viewModel::dismissPrologue
        )
    }
}

private object ColorTokens {
    val DeepCenter = androidx.compose.ui.graphics.Color(0xFF12172A)
}

@Composable
private fun CristaliaTopBar(
    crystals: Int,
    ambientEnabled: Boolean,
    onToggleAmbient: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Cristalia",
                style = MaterialTheme.typography.headlineMedium,
                color = WarmAlabaster
            )
            Text(
                text = "Santuario de bloques y joyas",
                style = MaterialTheme.typography.bodySmall,
                color = MutedMist
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = ElevatedSlate,
                border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Diamond,
                        contentDescription = "Cristales",
                        tint = ChampagneGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "$crystals",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = ChampagneGold
                    )
                }
            }

            IconButton(
                onClick = onToggleAmbient,
                modifier = Modifier
                    .size(48.dp)
                    .background(MidnightSurface, RoundedCornerShape(14.dp))
                    .testTag("topbar_audio_toggle")
            ) {
                Icon(
                    imageVector = if (ambientEnabled) Icons.Rounded.MusicNote else Icons.Rounded.MusicOff,
                    contentDescription = if (ambientEnabled) "Pausar sonido ambiental" else "Activar sonido ambiental",
                    tint = if (ambientEnabled) LuminousJade else MutedMist
                )
            }
        }
    }
}
