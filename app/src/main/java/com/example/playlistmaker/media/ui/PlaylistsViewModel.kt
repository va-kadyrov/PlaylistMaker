package com.example.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.PlaylistInteractor
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val playlistsState = MutableLiveData(PlaylistsState(true, emptyList()))
    fun observePlaylistsState(): LiveData<PlaylistsState> = playlistsState

    fun loadPlaylists(){
        viewModelScope.launch {
            playlistInteractor.loadAll().collect {
                playlists ->
                playlistsState.postValue(PlaylistsState(
                    playlists.isEmpty(), (playlists?:emptyList()).toMutableList()))
            }
    }}

}