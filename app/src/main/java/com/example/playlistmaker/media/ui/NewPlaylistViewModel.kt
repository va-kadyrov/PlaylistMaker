package com.example.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.data.Playlist
import com.example.playlistmaker.media.domain.PlaylistInteractor
import kotlinx.coroutines.launch

open class NewPlaylistViewModel(val playlistInteractor: PlaylistInteractor): ViewModel() {

    val newPlaylistState = MutableLiveData(NewPlaylistState(false, false))
    fun observeNewPlaylistState(): LiveData<NewPlaylistState> = newPlaylistState

    var playlistName = ""
    var playlistDescription = ""
    var playlistFilepath = ""

    fun playlistName(name: String) {
        playlistName = name
        newPlaylistState.value = NewPlaylistState(playlistName.isNotBlank(), false)
    }

    fun playlistDescription(description: String) {
        playlistDescription = description}

    fun playlistFilepath(filepath: String) {
        playlistFilepath = filepath}

    open fun savePlaylist() {
        viewModelScope.launch {
            playlistInteractor.add(Playlist(0, playlistName, playlistDescription, playlistFilepath, emptyList<Long>().toMutableList(), 0, 0))
            newPlaylistState.value = NewPlaylistState(playlistName.isNotBlank(), true)
        }
    }

    open fun tryBack(){
        if (playlistName.isNotBlank() or playlistDescription.isNotBlank() or playlistFilepath.isNotBlank()) {
            newPlaylistState.value = NewPlaylistState(playlistName.isNotBlank(), false, true, false)
        } else {
            newPlaylistState.value = NewPlaylistState(playlistName.isNotBlank(), false, false, true)
        }
    }
}