package com.example.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.data.Playlist
import com.example.playlistmaker.media.domain.PlaylistInteractor
import kotlinx.coroutines.launch

class NewPlaylistViewModel(val playlistInteractor: PlaylistInteractor): ViewModel() {

    private val newPlaylistState = MutableLiveData(NewPlaylistState(false, false, false))
    fun observeNewPlaylistState(): LiveData<NewPlaylistState> = newPlaylistState

    var playlistName = ""
    var playlistDescription = ""
    var playlistFilepath = ""

    fun playlistName(name: String) {
        playlistName = name
        newPlaylistState.value = NewPlaylistState((playlistName.isNotBlank() or playlistDescription.isNotBlank() or playlistFilepath.isNotBlank()), playlistName.isNotBlank(), false)
    }

    fun playlistDescription(description: String) {
        playlistDescription = description}

    fun playlistFilepath(filepath: String) {
        playlistFilepath = filepath}

    fun savePlaylist() {
        viewModelScope.launch {
            playlistInteractor.add(Playlist(0, playlistName, playlistDescription, playlistFilepath, emptyList<Long>().toMutableList(), 0))
            newPlaylistState.value = NewPlaylistState((playlistName.isNotBlank() or playlistDescription.isNotBlank() or playlistFilepath.isNotBlank()), playlistName.isNotBlank(), true)
        }
    }
}