package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.Track

interface TracksHistoryRepository {
    fun loadTracks(): List<Track>
    fun saveTracks(tracks: List<Track>)
}