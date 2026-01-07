package com.example.playlistmaker.player.domain

import com.example.playlistmaker.player.domain.db.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class FavoriteTracksInteractorImpl(private val repository: FavoriteTracksRepository): FavoriteTracksInteractor {
    override suspend fun insert(track: Track) {
        withContext(Dispatchers.IO) { repository.addTrack(track) }
    }

    override suspend fun delete(track: Track) {
        withContext(Dispatchers.IO) { repository.delTrack(track) }
    }

    override suspend fun getAllTracks(): Flow<List<Track>> {
        return withContext(Dispatchers.IO) { repository.getAllTracks() }
    }
}