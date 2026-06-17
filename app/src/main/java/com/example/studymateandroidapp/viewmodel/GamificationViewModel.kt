package com.example.studymateandroidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studymateandroidapp.data.repository.GamificationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

/**
 * Shared ViewModel for app-wide gamification features (like XP popups).
 */
class GamificationViewModel(
    private val repository: GamificationRepository
) : ViewModel() {

    val xpEvents = repository.xpEvents

    val levelInfo = repository.getLevelInfoFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
