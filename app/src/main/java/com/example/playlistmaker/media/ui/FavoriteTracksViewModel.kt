package com.example.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.FavoriteTracksInteractor
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.TracksState
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(private val favoriteTracksInteractor: FavoriteTracksInteractor) : ViewModel() {

    private val favoriteTracksStateLiveData = MutableLiveData<FavoriteTracksState>(FavoriteTracksState(false, emptyList<Track>().toMutableList()))
    fun observeFavoriteTrackState(): LiveData<FavoriteTracksState> = favoriteTracksStateLiveData

    fun loadFavoriteTracks(){
        viewModelScope.launch {
            favoriteTracksInteractor.getAllTracks().collect { tracks ->
                favoriteTracksStateLiveData.postValue(FavoriteTracksState(
                    tracks.isEmpty(), (tracks?:emptyList()).toMutableList())
                )
            }
        }
    }

}