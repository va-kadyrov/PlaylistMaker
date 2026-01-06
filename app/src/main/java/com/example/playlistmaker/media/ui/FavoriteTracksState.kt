package com.example.playlistmaker.media.ui

import com.example.playlistmaker.search.domain.Track

data class FavoriteTracksState(var isEmpty: Boolean, val tracks: MutableList<Track>)

