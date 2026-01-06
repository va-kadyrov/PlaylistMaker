package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.Track

interface TracksHistoryInteractor {

    suspend fun loadTracks(consumer: TracksConsumer)

    fun clearTracks(consumer: TracksConsumer)

    suspend fun addTrack(track: Track, consumer: TracksConsumer)

}