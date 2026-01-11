package com.example.playlistmaker.media.domain

import com.example.playlistmaker.media.data.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun add(playlist: Playlist)
    suspend fun loadAll() : Flow<List<Playlist>>
    suspend fun loadInfo(id: Long) : Flow<Playlist>
    suspend fun delete(id: Long)
    suspend fun trackIsIdle(idTrack: Long): Boolean
}