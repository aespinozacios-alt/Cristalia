package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_progress")
data class PlayerProgressEntity(
    @PrimaryKey val id: Int = 1,
    val playerName: String = "Guardián",
    val maxUnlockedLevel: Int = 1,
    val completedLevelsCsv: String = "",
    val crystalsBalance: Int = 100,
    val unlockedThemesCsv: String = "santuario",
    val selectedThemeId: String = "santuario",
    val selectedCharacterId: String = "lyra",
    val ambientEnabled: Boolean = true,
    val sfxEnabled: Boolean = true,
    val selectedAmbientTrackId: String = "brisa_bosque",
    val totalScore: Long = 0L,
    val totalLines: Int = 0
) {
    fun unlockedThemesSet(): Set<String> =
        unlockedThemesCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()

    fun completedLevelsSet(): Set<Int> =
        completedLevelsCsv.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()
}
