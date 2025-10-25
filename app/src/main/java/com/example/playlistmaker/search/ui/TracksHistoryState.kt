package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.Track

data class TracksHistoryState(var isVisible: Boolean, var isEmpty: Boolean, val content:MutableList<Track>)
