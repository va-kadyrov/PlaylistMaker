package com.example.playlistmaker

import java.util.Date

class Track (
    val trackName: String,
    val collectionName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val releaseDate: Date,
    val primaryGenreName: String,
    val country: String,
    val artworkUrl100: String,
    val trackId: Long,
    val previewUrl: String)