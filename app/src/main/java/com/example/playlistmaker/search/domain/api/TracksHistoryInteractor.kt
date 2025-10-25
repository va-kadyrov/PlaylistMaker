package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.Track

interface TracksHistoryInteractor {

    fun loadTracks(consumer: TracksConsumer)

    fun clearTracks(consumer: TracksConsumer)

    fun addTrack(track: Track, consumer: TracksConsumer)

}