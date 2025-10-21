package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TracksConsumer
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun loadTracks(expression: String, consumer: TracksConsumer) {
        executor.execute {
            consumer.consume(repository.loadTracks(expression))
        }
    }
}