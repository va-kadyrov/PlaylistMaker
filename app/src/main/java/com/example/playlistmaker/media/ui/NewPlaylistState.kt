package com.example.playlistmaker.media.ui

data class NewPlaylistState(
    var canBeSaved: Boolean,
    var playlistSaved: Boolean,
    var showWarningDialog: Boolean = false,
    var goBack: Boolean = false
)
