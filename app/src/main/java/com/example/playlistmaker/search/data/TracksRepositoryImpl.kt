package com.example.playlistmaker.search.data

import com.example.playlistmaker.player.data.db.AppDatabase
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.api.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class TracksRepositoryImpl(private val networkClient: NetworkClient, private val appDatabase: AppDatabase) : TracksRepository {
    override fun loadTracks(expression: String): Flow<List<Track>> = flow {
        val favoriteTracksIds = withContext(Dispatchers.IO) { appDatabase.trackDao().getIds() }
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        if (response.resultCode == 200) {
            emit((response as TracksSearchResponse).results.map {
                Track(
                    it.trackName,
                    it.collectionName,
                    it.artistName,
                    it.trackTimeMillis,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.artworkUrl100,
                    it.trackId,
                    it.previewUrl,
                    favoriteTracksIds.contains(it.trackId)
                )
            })
        } else {
            emit(emptyList())
        }
    }
}