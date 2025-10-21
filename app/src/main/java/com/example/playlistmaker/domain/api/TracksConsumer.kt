package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.Track

fun interface TracksConsumer {
    fun consume(tracks:List<Track>)
}