package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.api.TracksConsumer
import com.example.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksHistoryRepository

const val MAX_HISTORY_SIZE = 10

class TracksHistoryInteractorImpl(private val repository: TracksHistoryRepository) :
    TracksHistoryInteractor {

    override fun loadTracks(consumer: TracksConsumer) {
        consumer.consume(repository.loadTracks())
    }

    override fun clearTracks(consumer: TracksConsumer) {
        repository.saveTracks(emptyList())
        consumer.consume(emptyList())
    }

    override fun addTrack(track: Track, consumer: TracksConsumer) {
        val tracks = repository.loadTracks().toMutableList()
        for (eachTrack in tracks) if (eachTrack.trackId == track.trackId) tracks.remove(eachTrack)
        tracks.add(0, track)
        if (tracks.size > MAX_HISTORY_SIZE) tracks.removeAt(tracks.size - 1)
        repository.saveTracks(tracks)
    }
}