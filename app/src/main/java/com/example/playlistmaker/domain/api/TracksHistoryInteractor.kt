package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.Track

interface TracksHistoryInteractor {

    fun loadTracks(consumer: TracksConsumer)

    fun clearTracks(consumer: TracksConsumer)

    fun addTrack(track: Track, consumer: TracksConsumer)

}