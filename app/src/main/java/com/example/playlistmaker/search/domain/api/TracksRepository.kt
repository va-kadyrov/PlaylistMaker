package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun loadTracks(expression: String): Flow<List<Track>>
}