package com.example.playlistmaker.media.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(true)
    val id: Long,
    val name: String,
    val description: String,
    val filePath: String,
    val tracks: String,
    val timestamp: Long
)