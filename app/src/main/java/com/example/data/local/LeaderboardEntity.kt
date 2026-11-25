package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leaderboard_scores")
data class LeaderboardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val playerName: String,
    val characterId: String,
    val levelNumber: Int,
    val levelTitle: String,
    val score: Int,
    val linesCleared: Int,
    val jewelsTriggered: Int,
    val isVictory: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
