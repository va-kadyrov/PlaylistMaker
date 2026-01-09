package com.example.playlistmaker.player.ui

import com.example.playlistmaker.media.data.Playlist

data class PlayerFragmentState(
    var action: Int,
    var playerStatus: Int,
    var trackTimeProgress: String,
    var isFavorite: Boolean,
    val playlists: MutableList<Playlist>,
    var trackInPlaylist: Int = 0,
    var playlistName: String = ""
)
