package com.example.playlistmaker.search.data.dto

import java.util.Date

class TrackDTO (
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