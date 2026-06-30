package com.example.studymateandroidapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tracks the user's gamification progress.
 * Phase 1: XP System.
 */
@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey val id: Long = 1, // Single row entity
    val totalXp: Int = 0,
    val userId: String? = null,
    val serverId: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)
