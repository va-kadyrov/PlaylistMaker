package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.api.TracksNtInteractor
import com.example.playlistmaker.search.domain.api.TracksNtRepository
import kotlinx.coroutines.flow.Flow

class TracksNtInteractorImpl(private val repository: TracksNtRepository) : TracksNtInteractor {

    override fun loadTracks(expression: String): Flow<List<Track>> {
        return repository.loadTracks(expression)

    }
}