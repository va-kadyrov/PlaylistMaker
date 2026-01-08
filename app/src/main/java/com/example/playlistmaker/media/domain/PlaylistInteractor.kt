package com.example.playlistmaker.media.domain

import com.example.playlistmaker.media.data.Playlist

interface PlaylistInteractor {
    suspend fun add(playlist: Playlist)
}