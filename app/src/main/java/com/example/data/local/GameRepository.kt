package com.example.data.local

import com.example.domain.model.GameCatalog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameRepository(private val dao: GameDao) {

    val leaderboardScores: Flow<List<LeaderboardEntity>> = dao.observeAllScores()

    val playerProgress: Flow<PlayerProgressEntity> = dao.observeProgress().map { existing ->
        existing ?: PlayerProgressEntity()
    }

    suspend fun ensureInitialized() {
        if (dao.getProgressOnce() == null) {
            dao.upsertProgress(PlayerProgressEntity())
        }
    }

    suspend fun selectCharacter(characterId: String) {
        val current = dao.getProgressOnce() ?: PlayerProgressEntity()
        dao.upsertProgress(current.copy(selectedCharacterId = characterId))
    }

    suspend fun selectTheme(themeId: String) {
        val current = dao.getProgressOnce() ?: PlayerProgressEntity()
        if (current.unlockedThemesSet().contains(themeId)) {
            dao.upsertProgress(current.copy(selectedThemeId = themeId))
        }
    }

    suspend fun unlockThemeWithCrystals(themeId: String): Boolean {
        val current = dao.getProgressOnce() ?: PlayerProgressEntity()
        val theme = GameCatalog.themeById(themeId)
        val unlocked = current.unlockedThemesSet().toMutableSet()
        if (unlocked.contains(themeId)) {
            dao.upsertProgress(current.copy(selectedThemeId = themeId))
            return true
        }
        if (current.crystalsBalance < theme.unlockCostCrystals) {
            return false
        }
        unlocked.add(themeId)
        dao.upsertProgress(
            current.copy(
                crystalsBalance = current.crystalsBalance - theme.unlockCostCrystals,
                unlockedThemesCsv = unlocked.joinToString(","),
                selectedThemeId = themeId
            )
        )
        return true
    }

    suspend fun updatePlayerName(name: String) {
        val sanitized = name.trim().ifEmpty { "Guardián" }.take(20)
        val current = dao.getProgressOnce() ?: PlayerProgressEntity()
        dao.upsertProgress(current.copy(playerName = sanitized))
    }

    suspend fun setAmbientEnabled(enabled: Boolean) {
        val current = dao.getProgressOnce() ?: PlayerProgressEntity()
        dao.upsertProgress(current.copy(ambientEnabled = enabled))
    }

    suspend fun setSfxEnabled(enabled: Boolean) {
        val current = dao.getProgressOnce() ?: PlayerProgressEntity()
        dao.upsertProgress(current.copy(sfxEnabled = enabled))
    }

    suspend fun selectAmbientTrack(trackId: String) {
        val current = dao.getProgressOnce() ?: PlayerProgressEntity()
        dao.upsertProgress(current.copy(selectedAmbientTrackId = trackId))
    }

    suspend fun recordGameResult(
        playerName: String,
        characterId: String,
        levelNumber: Int,
        levelTitle: String,
        score: Int,
        linesCleared: Int,
        jewelsTriggered: Int,
        isVictory: Boolean
    ): Int {
        val sanitizedName = playerName.trim().ifEmpty { "Guardián" }.take(20)
        val current = dao.getProgressOnce() ?: PlayerProgressEntity()

        if (score > 0 || linesCleared > 0) {
            dao.insertScore(
                LeaderboardEntity(
                    playerName = sanitizedName,
                    characterId = characterId,
                    levelNumber = levelNumber,
                    levelTitle = levelTitle,
                    score = score,
                    linesCleared = linesCleared,
                    jewelsTriggered = jewelsTriggered,
                    isVictory = isVictory
                )
            )
        }

        val levelInfo = GameCatalog.levelByNumber(levelNumber)
        val earnedCrystals = ((score / 40) + (linesCleared * 4) + (jewelsTriggered * 12) +
            if (isVictory) levelInfo.rewardCrystals else 0).coerceAtLeast(10)

        val completedSet = current.completedLevelsSet().toMutableSet()
        val unlockedThemes = current.unlockedThemesSet().toMutableSet()
        var nextMaxLevel = current.maxUnlockedLevel

        if (isVictory) {
            completedSet.add(levelNumber)
            if (levelNumber >= nextMaxLevel && levelNumber < GameCatalog.storyLevels.size) {
                nextMaxLevel = levelNumber + 1
            }
            levelInfo.unlocksThemeId?.let { unlockedThemes.add(it) }
        }

        dao.upsertProgress(
            current.copy(
                playerName = sanitizedName,
                maxUnlockedLevel = nextMaxLevel,
                completedLevelsCsv = completedSet.sorted().joinToString(","),
                crystalsBalance = current.crystalsBalance + earnedCrystals,
                unlockedThemesCsv = unlockedThemes.joinToString(","),
                totalScore = current.totalScore + score,
                totalLines = current.totalLines + linesCleared
            )
        )
        return earnedCrystals
    }

    suspend fun clearLeaderboard() {
        dao.clearLeaderboard()
    }
}
