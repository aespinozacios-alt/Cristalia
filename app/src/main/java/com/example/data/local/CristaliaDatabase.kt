package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [LeaderboardEntity::class, PlayerProgressEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CristaliaDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao

    companion object {
        @Volatile
        private var INSTANCE: CristaliaDatabase? = null

        fun getInstance(context: Context): CristaliaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CristaliaDatabase::class.java,
                    "cristalia_local.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
