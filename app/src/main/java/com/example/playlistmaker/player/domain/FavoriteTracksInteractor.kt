package com.example.playlistmaker.player.domain

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {
    suspend fun insert(track: Track)
    suspend fun delete(track: Track)
    suspend fun getAllTracks(): Flow<List<Track>>
}