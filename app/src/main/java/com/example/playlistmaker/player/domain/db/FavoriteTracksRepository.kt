package com.example.playlistmaker.player.domain.db

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {
    suspend fun addTrack(track: Track)
    suspend fun delTrack(trackId: Track)
    suspend fun getAllTracks(): Flow<List<Track>>
}