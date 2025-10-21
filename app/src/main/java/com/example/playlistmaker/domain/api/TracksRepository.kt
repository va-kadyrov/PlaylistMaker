package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.Track

interface TracksRepository {
    fun loadTracks(expression: String): List<Track>
}