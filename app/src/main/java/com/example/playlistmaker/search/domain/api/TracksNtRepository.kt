package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface TracksNtRepository {
    fun loadTracks(expression: String): Flow<List<Track>>
}