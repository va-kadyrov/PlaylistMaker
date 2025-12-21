package com.example.playlistmaker.search.domain.api

import com.bumptech.glide.load.engine.Resource
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    //    fun loadTracks(expression: String): List<Track>
    fun loadTracks(expression: String): Flow<List<Track>>
}