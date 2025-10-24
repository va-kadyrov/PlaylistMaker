package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.api.TracksRepository

class TracksRepositoryImpl (private val networkClient: NetworkClient) : TracksRepository {
    override fun loadTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        if (response.resultCode == 200) {
            return (response as TracksSearchResponse).results.map {
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
                    it.previewUrl
                )
            }
        } else { return emptyList() }
    }
}