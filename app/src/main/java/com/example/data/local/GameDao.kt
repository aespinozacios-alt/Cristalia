package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM leaderboard_scores ORDER BY score DESC, timestamp DESC")
    fun observeAllScores(): Flow<List<LeaderboardEntity>>

    @Query("SELECT * FROM leaderboard_scores WHERE levelNumber = :levelNumber ORDER BY score DESC, timestamp DESC")
    fun observeScoresByLevel(levelNumber: Int): Flow<List<LeaderboardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(entry: LeaderboardEntity)

    @Query("DELETE FROM leaderboard_scores")
    suspend fun clearLeaderboard()

    @Query("SELECT * FROM player_progress WHERE id = 1")
    fun observeProgress(): Flow<PlayerProgressEntity?>

    @Query("SELECT * FROM player_progress WHERE id = 1")
    suspend fun getProgressOnce(): PlayerProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: PlayerProgressEntity)
}
