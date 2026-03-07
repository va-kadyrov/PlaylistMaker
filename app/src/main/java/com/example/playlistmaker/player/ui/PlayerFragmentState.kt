package com.example.playlistmaker.player.ui

import com.example.playlistmaker.media.data.Playlist

sealed interface PlayerFragmentState {
    data class PlaylistsUpdate(val playlists: List<Playlist>): PlayerFragmentState
    data class IsFavoriteUpdate(val isFavorite: Boolean): PlayerFragmentState
    data class TrackAlreadyInPlaylist(val playlistName: String): PlayerFragmentState
    data class TrackAddedInPlaylist(val playlistName: String): PlayerFragmentState
    data class PlayerStatusDefault(val trackTimeProgress: String): PlayerFragmentState
    data class PlayerStatusPrepared(val trackTimeProgress: String): PlayerFragmentState
    data class PlayerStatusPlaying(val trackTimeProgress: String): PlayerFragmentState
    data class PlayerStatusPaused(val trackTimeProgress: String): PlayerFragmentState
}