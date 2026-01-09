package com.example.playlistmaker.player.data.db

import com.example.playlistmaker.player.data.FavoriteTrackDbConverter
import com.example.playlistmaker.player.data.TrackDbConverter
import com.example.playlistmaker.player.domain.db.TracksRepository
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter,
    private val favoriteTrackDbConverter: FavoriteTrackDbConverter
): TracksRepository {

    override suspend fun addTrack(track: Track): Boolean {
        try {
            appDatabase.trackDao().insert(trackDbConverter.map(track))
            return true}
        catch (e: Exception) {
            return false
        }
    }

    override suspend fun addFavoriteTrack(track: Track) {
        appDatabase.favoriteTrackDao().insert(favoriteTrackDbConverter.map(track))
    }

    override suspend fun deleteTrack(track: Track) {
        appDatabase.trackDao().delete(trackDbConverter.map(track))
    }

    override suspend fun deleteFavoriteTrack(track: Track) {
        appDatabase.favoriteTrackDao().delete(favoriteTrackDbConverter.map(track))
    }

    override suspend fun getAllFavoriteTracks(): Flow<List<Track>> = flow {
        val tracks = convert(appDatabase.favoriteTrackDao().getAll())
        tracks.forEach { it.isFavorite = true }
        emit(tracks)
    }

    private fun convert(tracks: List<FavoriteTrackEntity>): List<Track> {
        return tracks.map {track -> favoriteTrackDbConverter.map(track)}
    }
}