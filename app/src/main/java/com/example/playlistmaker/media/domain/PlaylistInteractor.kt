package com.example.playlistmaker.media.domain

import com.example.playlistmaker.media.data.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun add(playlist: Playlist)
    suspend fun loadAll() : Flow<List<Playlist>>
}