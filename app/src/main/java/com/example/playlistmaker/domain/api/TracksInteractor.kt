package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.Track

interface TracksInteractor {
    fun loadTracks(expression: String, consumer: TracksConsumer)
}