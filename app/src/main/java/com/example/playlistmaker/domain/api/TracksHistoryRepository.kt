package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.Track

interface TracksHistoryRepository {
    fun loadTracks(): List<Track>
    fun saveTracks(tracks: List<Track>)
}