package com.example.playlistmaker.player.domain

import com.example.playlistmaker.player.domain.db.TracksRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.withContext

class TracksInteractorImpl(private val repository: TracksRepository): TracksInteractor {

    override suspend fun addTrack(track: Track): Boolean {
        return withContext(Dispatchers.IO) { repository.addTrack(track) }
    }

    override suspend fun addFavoriteTrack(track: Track) {
        withContext(Dispatchers.IO) { repository.addFavoriteTrack(track) }
    }

    override suspend fun deleteTrack(track: Track) {
        withContext(Dispatchers.IO) { repository.deleteTrack(track) }
    }

    override suspend fun deleteTrack(idTrack: Long) {
        withContext(Dispatchers.IO) { repository.deleteTrack(idTrack) }
    }

    override suspend fun deleteFavoriteTrack(track: Track) {
        withContext(Dispatchers.IO) { repository.deleteFavoriteTrack(track) }
    }

    override suspend fun getAllFavoriteTracks(): Flow<List<Track>> {
        return withContext(Dispatchers.IO) { repository.getAllFavoriteTracks() }
    }

    override suspend fun getTrackInfo(id: Long): Flow<Track> {
        return withContext(Dispatchers.IO) { repository.getTrackInfo(id) }
    }

    override suspend fun totalDuration(iDs: List<Long>): Long {
            var totalDuration = 0L
            iDs.forEach { id ->
                totalDuration = totalDuration + repository.getTrackInfo(id).single().trackTimeMillis
            }
            return totalDuration
    }


}