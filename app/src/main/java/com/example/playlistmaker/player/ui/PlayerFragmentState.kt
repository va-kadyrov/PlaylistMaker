package com.example.playlistmaker.player.ui

import com.example.playlistmaker.media.data.Playlist

data class PlayerFragmentState(var playerStatus: Int, var trackTimeProgress: String, var isFavorite: Boolean, val playlists: MutableList<Playlist>)
