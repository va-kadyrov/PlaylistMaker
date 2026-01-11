package com.example.playlistmaker.player.domain

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    suspend fun addTrack(track: Track) : Boolean
    suspend fun addFavoriteTrack(track: Track)
    suspend fun deleteTrack(track: Track)
    suspend fun deleteTrack(idTrack: Long)
    suspend fun deleteFavoriteTrack(track: Track)
    suspend fun getAllFavoriteTracks(): Flow<List<Track>>
    suspend fun getTrackInfo(id: Long): Flow<Track>
}