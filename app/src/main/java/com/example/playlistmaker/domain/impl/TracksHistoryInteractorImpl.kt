package com.example.playlistmaker.domain.impl

import android.util.Log
import com.example.playlistmaker.domain.Track
import com.example.playlistmaker.domain.api.TracksConsumer
import com.example.playlistmaker.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.domain.api.TracksHistoryRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksHistoryInteractorImpl(private val repository: TracksHistoryRepository) : TracksHistoryInteractor {

    //private val executor = Executors.newCachedThreadPool()

    override fun loadTracks(consumer: TracksConsumer) {
        consumer.consume(repository.loadTracks())
//        executor.execute {
//            consumer.consume(repository.loadTracks())
//        }
    }

    override fun clearTracks(consumer: TracksConsumer) {
        repository.saveTracks(emptyList())
        consumer.consume(emptyList())
    }

    override fun addTrack(track:Track, consumer: TracksConsumer) {
        val tracks = repository.loadTracks().toMutableList()
        for (eachTrack in tracks) if (eachTrack.trackId == track.trackId) tracks.remove(eachTrack)
        tracks.add(0, track)
        if (tracks.size > 10) tracks.removeAt(tracks.size-1)
        repository.saveTracks(tracks)
    }
}