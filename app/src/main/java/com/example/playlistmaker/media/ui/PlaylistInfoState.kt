package com.example.playlistmaker.media.ui

import com.example.playlistmaker.media.data.Playlist
import com.example.playlistmaker.search.domain.Track

data class PlaylistInfoState(val action: Int, val playlist: Playlist?, val tracks: List<Track>, val message: String)
