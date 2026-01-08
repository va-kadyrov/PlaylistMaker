package com.example.playlistmaker.media.data

data class Playlist(
    val id: Long,
    val name: String,
    val description: String,
    val filePath: String,
    var tracks: MutableList<Long>,
    var trackCounts: Int
)
