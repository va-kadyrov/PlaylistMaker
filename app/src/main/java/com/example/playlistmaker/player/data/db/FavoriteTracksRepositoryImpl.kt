package com.example.playlistmaker.player.data.db

import com.example.playlistmaker.player.data.TrackDbConverter
import com.example.playlistmaker.player.domain.db.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter
): FavoriteTracksRepository {

    override suspend fun addTrack(track: Track) {
        appDatabase.trackDao().insert(trackDbConverter.map(track))
    }

    override suspend fun delTrack(track: Track) {
        appDatabase.trackDao().delete(trackDbConverter.map(track))
    }

    override suspend fun getAllTracks(): Flow<List<Track>> = flow {
        val tracks = convert(appDatabase.trackDao().getAll())
        tracks.forEach { it.isFavorite = true }
        emit(tracks)
    }

    private fun convert(tracks: List<TrackEntity>): List<Track> {
        return tracks.map {track -> trackDbConverter.map(track)}
    }
}