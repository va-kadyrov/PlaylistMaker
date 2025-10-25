package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.Track

fun interface TracksConsumer {
    fun consume(tracks:List<Track>)
}