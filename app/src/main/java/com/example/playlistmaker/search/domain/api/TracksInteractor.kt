package com.example.playlistmaker.search.domain.api

interface TracksInteractor {
    fun loadTracks(expression: String, consumer: TracksConsumer)
}