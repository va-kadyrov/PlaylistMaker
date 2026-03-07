package com.example.playlistmaker.player.domain

import com.example.playlistmaker.player.ui.PlayerFragmentState
import kotlinx.coroutines.flow.StateFlow

interface PlayerInteractor {
    fun getPlayerSrviceState(): StateFlow<PlayerFragmentState>
    fun playerControl()
    fun showNotification()
    fun hideNotification()
}