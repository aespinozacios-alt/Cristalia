package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AmbientAudioEngine
import com.example.data.local.CristaliaDatabase
import com.example.data.local.GameRepository
import com.example.data.local.LeaderboardEntity
import com.example.data.local.PlayerProgressEntity
import com.example.domain.model.BoardTheme
import com.example.domain.model.CharacterHero
import com.example.domain.model.GameCatalog
import com.example.domain.model.StoryLevel
import com.example.domain.model.VisualEffectEvent
import com.example.ui.game.LockResolution
import com.example.ui.game.TetrisBoardState
import com.example.ui.game.TetrisEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class HubTab {
    AVENTURA,
    ESTETICAS,
    CLASIFICACION,
    SONIDOS
}

data class GameSessionUiState(
    val isPlayingScreen: Boolean = false,
    val level: StoryLevel = GameCatalog.storyLevels.first(),
    val character: CharacterHero = GameCatalog.characters.first(),
    val boardTheme: BoardTheme = GameCatalog.boardThemes.first(),
    val boardState: TetrisBoardState? = null,
    val isPaused: Boolean = false,
    val showVictoryModal: Boolean = false,
    val showGameOverModal: Boolean = false,
    val lastEarnedCrystals: Int = 0,
    val scoreAlreadySaved: Boolean = false,
    val activeEffects: List<VisualEffectEvent> = emptyList()
)

class CristaliaViewModel(application: Application) : AndroidViewModel(application) {

    private val database = CristaliaDatabase.getInstance(application)
    private val repository = GameRepository(database.gameDao())
    private val audioEngine = AmbientAudioEngine()

    val progress: StateFlow<PlayerProgressEntity> = repository.playerProgress
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlayerProgressEntity()
        )

    val leaderboard: StateFlow<List<LeaderboardEntity>> = repository.leaderboardScores
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedTab = MutableStateFlow(HubTab.AVENTURA)
    val selectedTab: StateFlow<HubTab> = _selectedTab.asStateFlow()

    private val _selectedLeaderboardFilterLevel = MutableStateFlow<Int?>(null)
    val selectedLeaderboardFilterLevel: StateFlow<Int?> = _selectedLeaderboardFilterLevel.asStateFlow()

    private val _prologueLevel = MutableStateFlow<StoryLevel?>(null)
    val prologueLevel: StateFlow<StoryLevel?> = _prologueLevel.asStateFlow()

    private val _showCharacterPicker = MutableStateFlow(false)
    val showCharacterPicker: StateFlow<Boolean> = _showCharacterPicker.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _gameState = MutableStateFlow(GameSessionUiState())
    val gameState: StateFlow<GameSessionUiState> = _gameState.asStateFlow()

    private var engine: TetrisEngine? = null
    private var gameLoopJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureInitialized()
        }
        viewModelScope.launch {
            progress.collectLatest { prog ->
                audioEngine.configure(
                    ambientOn = prog.ambientEnabled,
                    sfxOn = prog.sfxEnabled,
                    trackId = prog.selectedAmbientTrackId
                )
                if (_gameState.value.isPlayingScreen) {
                    _gameState.update {
                        it.copy(boardTheme = GameCatalog.themeById(prog.selectedThemeId))
                    }
                }
            }
        }
    }

    fun onAppResume() {
        audioEngine.onLifecycleResume()
    }

    fun onAppPause() {
        audioEngine.onLifecyclePause()
        if (_gameState.value.isPlayingScreen &&
            !_gameState.value.showVictoryModal &&
            !_gameState.value.showGameOverModal
        ) {
            pauseGame()
        }
    }

    fun selectTab(tab: HubTab) {
        _selectedTab.value = tab
    }

    fun setLeaderboardFilter(levelNumber: Int?) {
        _selectedLeaderboardFilterLevel.value = levelNumber
    }

    fun openCharacterPicker() {
        _showCharacterPicker.value = true
    }

    fun closeCharacterPicker() {
        _showCharacterPicker.value = false
    }

    fun selectCharacter(characterId: String) {
        viewModelScope.launch {
            repository.selectCharacter(characterId)
            _showCharacterPicker.value = false
            val hero = GameCatalog.characterById(characterId)
            showBanner("${hero.name} elegida para tu viaje")
        }
    }

    fun openLevelPrologue(level: StoryLevel) {
        if (level.levelNumber > progress.value.maxUnlockedLevel) {
            showBanner("Completa el nivel ${level.levelNumber - 1} para desbloquear este capítulo")
            return
        }
        _prologueLevel.value = level
    }

    fun dismissPrologue() {
        _prologueLevel.value = null
    }

    fun startLevelFromPrologue(level: StoryLevel) {
        _prologueLevel.value = null
        startLevel(level)
    }

    fun startLevel(level: StoryLevel) {
        val currentProg = progress.value
        val character = GameCatalog.characterById(currentProg.selectedCharacterId)
        val theme = GameCatalog.themeById(currentProg.selectedThemeId)
        val newEngine = TetrisEngine(level = level, character = character)
        engine = newEngine

        _gameState.value = GameSessionUiState(
            isPlayingScreen = true,
            level = level,
            character = character,
            boardTheme = theme,
            boardState = newEngine.snapshot(),
            isPaused = false,
            showVictoryModal = false,
            showGameOverModal = false,
            lastEarnedCrystals = 0,
            scoreAlreadySaved = false,
            activeEffects = emptyList()
        )
        startGravityLoop()
    }

    private fun startGravityLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            while (isActive) {
                val eng = engine ?: break
                val state = _gameState.value
                if (!state.isPlayingScreen || state.isPaused || state.showVictoryModal || state.showGameOverModal) {
                    delay(120L)
                    continue
                }
                val tickMs = eng.currentTickIntervalMs()
                delay(tickMs)
                if (_gameState.value.isPaused || !_gameState.value.isPlayingScreen) continue
                eng.tickSlowMo(tickMs)
                val resolution = eng.gravityStep()
                syncEngineSnapshot(resolution, playLockSound = resolution != null && resolution.linesCleared == 0)
            }
        }
    }

    fun moveLeft() {
        val eng = engine ?: return
        if (isInputBlocked()) return
        if (eng.moveLeft()) {
            audioEngine.playMove()
            syncEngineSnapshot(null, playLockSound = false)
        }
    }

    fun moveRight() {
        val eng = engine ?: return
        if (isInputBlocked()) return
        if (eng.moveRight()) {
            audioEngine.playMove()
            syncEngineSnapshot(null, playLockSound = false)
        }
    }

    fun rotate() {
        val eng = engine ?: return
        if (isInputBlocked()) return
        if (eng.rotateClockwise()) {
            audioEngine.playRotate()
            syncEngineSnapshot(null, playLockSound = false)
        }
    }

    fun softDrop() {
        val eng = engine ?: return
        if (isInputBlocked()) return
        val (moved, resolution) = eng.softDropStep()
        if (moved) {
            audioEngine.playMove()
        }
        syncEngineSnapshot(resolution, playLockSound = resolution != null && resolution.linesCleared == 0)
    }

    fun hardDrop() {
        val eng = engine ?: return
        if (isInputBlocked()) return
        val resolution = eng.hardDrop()
        syncEngineSnapshot(resolution, playLockSound = resolution != null && resolution.linesCleared == 0)
    }

    fun holdPiece() {
        val eng = engine ?: return
        if (isInputBlocked()) return
        if (eng.holdCurrentPiece()) {
            audioEngine.playRotate()
            syncEngineSnapshot(null, playLockSound = false)
        }
    }

    fun activateCharacterRelic() {
        val eng = engine ?: return
        if (isInputBlocked()) return
        val resolution = eng.activateCharacterRelic() ?: return
        audioEngine.playJewelPower(_gameState.value.character.affinityJewel)
        syncEngineSnapshot(resolution, playLockSound = false)
    }

    private fun isInputBlocked(): Boolean {
        val st = _gameState.value
        return !st.isPlayingScreen || st.isPaused || st.showVictoryModal || st.showGameOverModal
    }

    private fun syncEngineSnapshot(resolution: LockResolution?, playLockSound: Boolean) {
        val eng = engine ?: return
        val snap = eng.snapshot()

        if (resolution != null) {
            if (resolution.triggeredJewels.isNotEmpty()) {
                audioEngine.playJewelPower(resolution.triggeredJewels.first())
            } else if (resolution.linesCleared > 0) {
                audioEngine.playLineClear(resolution.linesCleared)
            } else if (playLockSound) {
                audioEngine.playLock()
            }
        }

        val now = System.currentTimeMillis()
        val combinedEffects = (_gameState.value.activeEffects.filter { now - it.createdAtMs < 1500L } +
            (resolution?.effects ?: emptyList())).takeLast(4)

        val victoryNow = resolution?.reachedLevelTarget == true
        val gameOverNow = snap.isGameOver && !_gameState.value.showGameOverModal

        if (victoryNow) {
            audioEngine.playVictory()
            recordProgressOnFinish(isVictory = true, snap = snap)
        } else if (gameOverNow) {
            recordProgressOnFinish(isVictory = false, snap = snap)
        }

        _gameState.update {
            it.copy(
                boardState = snap,
                activeEffects = combinedEffects,
                showVictoryModal = it.showVictoryModal || victoryNow,
                showGameOverModal = it.showGameOverModal || gameOverNow
            )
        }
    }

    private fun recordProgressOnFinish(isVictory: Boolean, snap: TetrisBoardState) {
        val current = _gameState.value
        if (current.scoreAlreadySaved && !isVictory) return
        viewModelScope.launch {
            val crystals = repository.recordGameResult(
                playerName = progress.value.playerName,
                characterId = current.character.id,
                levelNumber = current.level.levelNumber,
                levelTitle = current.level.title,
                score = snap.score,
                linesCleared = snap.linesCleared,
                jewelsTriggered = snap.jewelsTriggeredCount,
                isVictory = isVictory
            )
            _gameState.update {
                it.copy(
                    lastEarnedCrystals = crystals,
                    scoreAlreadySaved = true
                )
            }
        }
    }

    fun continueEndlessAfterVictory() {
        engine?.continueEndless()
        _gameState.update {
            it.copy(
                showVictoryModal = false,
                boardState = engine?.snapshot(),
                scoreAlreadySaved = false
            )
        }
    }

    fun goToNextLevelAfterVictory() {
        val nextNum = (_gameState.value.level.levelNumber + 1).coerceAtMost(GameCatalog.storyLevels.size)
        val nextLevel = GameCatalog.levelByNumber(nextNum)
        startLevel(nextLevel)
    }

    fun restartCurrentLevel() {
        startLevel(_gameState.value.level)
    }

    fun pauseGame() {
        _gameState.update { it.copy(isPaused = true) }
    }

    fun resumeGame() {
        _gameState.update { it.copy(isPaused = false) }
    }

    fun exitGameToHub() {
        gameLoopJob?.cancel()
        engine = null
        _gameState.value = GameSessionUiState(isPlayingScreen = false)
    }

    fun savePlayerNameAndUpdate(name: String) {
        viewModelScope.launch {
            repository.updatePlayerName(name)
            showBanner("Nombre guardado: ${name.trim().ifEmpty { "Guardián" }}")
        }
    }

    fun selectOrUnlockTheme(theme: BoardTheme) {
        viewModelScope.launch {
            val unlocked = progress.value.unlockedThemesSet().contains(theme.id)
            if (unlocked) {
                repository.selectTheme(theme.id)
                showBanner("Estética «${theme.name}» equipada")
            } else {
                val success = repository.unlockThemeWithCrystals(theme.id)
                if (success) {
                    showBanner("¡Estética «${theme.name}» desbloqueada y equipada!")
                } else {
                    val missing = theme.unlockCostCrystals - progress.value.crystalsBalance
                    showBanner("Te faltan $missing cristales para desbloquear «${theme.name}»")
                }
            }
        }
    }

    fun toggleAmbient(enabled: Boolean) {
        viewModelScope.launch {
            repository.setAmbientEnabled(enabled)
        }
    }

    fun toggleSfx(enabled: Boolean) {
        viewModelScope.launch {
            repository.setSfxEnabled(enabled)
        }
    }

    fun selectAmbientTrack(trackId: String) {
        viewModelScope.launch {
            repository.selectAmbientTrack(trackId)
        }
    }

    fun clearLeaderboard() {
        viewModelScope.launch {
            repository.clearLeaderboard()
            showBanner("Tabla de clasificaciones reiniciada")
        }
    }

    fun dismissToast() {
        _toastMessage.value = null
    }

    private fun showBanner(message: String) {
        _toastMessage.value = message
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
        audioEngine.release()
    }
}
