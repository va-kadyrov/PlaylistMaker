package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface TracksNtInteractor {
    fun loadTracks(expression: String): Flow<List<Track>>
}