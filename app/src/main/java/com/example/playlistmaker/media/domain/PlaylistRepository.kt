package com.example.playlistmaker.media.domain

import com.example.playlistmaker.media.data.Playlist

interface PlaylistRepository {
    suspend fun add(playlist: Playlist)
}