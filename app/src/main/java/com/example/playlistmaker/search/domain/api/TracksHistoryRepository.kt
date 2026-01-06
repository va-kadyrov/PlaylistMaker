package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.Track

interface TracksHistoryRepository {
    suspend fun loadTracks(): List<Track>
    fun saveTracks(tracks: List<Track>)
}