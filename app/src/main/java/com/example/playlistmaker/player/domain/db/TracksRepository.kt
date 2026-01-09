package com.example.playlistmaker.player.domain.db

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    suspend fun addTrack(track: Track): Boolean
    suspend fun addFavoriteTrack(track: Track)
    suspend fun deleteTrack(track: Track)
    suspend fun deleteFavoriteTrack(track: Track)
    suspend fun getAllFavoriteTracks(): Flow<List<Track>>
}
