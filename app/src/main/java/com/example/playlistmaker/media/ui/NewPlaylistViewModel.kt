package com.example.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.media.domain.PlaylistInteractor

class NewPlaylistViewModel(val playlistInteractor: PlaylistInteractor): ViewModel() {

    private val newPlaylistState = MutableLiveData(NewPlaylistState(false, false))
    fun observeNewPlaylistState(): LiveData<NewPlaylistState> = newPlaylistState

    var playlistName = ""
    var playlistDescription = ""
    var playlistFilepath = ""

    fun playlistName(name: String) {
        playlistName = name
        newPlaylistState.value = NewPlaylistState(playlistName.isNotBlank(), true)
    }

    fun playlistDescription(description: String) {
        playlistDescription = description}

    fun playlistFilepath(filepath: String) {
        playlistFilepath = filepath}

}