package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.api.TracksConsumer
import com.example.playlistmaker.search.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val MAX_HISTORY_SIZE = 10

class TracksHistoryInteractorImpl(private val repository: TracksHistoryRepository) :
    TracksHistoryInteractor {

    override suspend fun loadTracks(consumer: TracksConsumer) {
        withContext(Dispatchers.IO) {consumer.consume(repository.loadTracks())}
    }

    override fun clearTracks(consumer: TracksConsumer) {
        repository.saveTracks(emptyList())
        consumer.consume(emptyList())
    }

    override suspend fun addTrack(track: Track, consumer: TracksConsumer) {
        val tracks = repository.loadTracks().toMutableList()
        for (eachTrack in tracks) {
            if (eachTrack.trackId == track.trackId) {
                tracks.remove(eachTrack)
                break
            }
        }
        tracks.add(0, track)
        if (tracks.size > MAX_HISTORY_SIZE) tracks.removeAt(tracks.size - 1)
        repository.saveTracks(tracks)
    }
}