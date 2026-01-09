package com.example.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.TracksInteractor
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(private val tracksInteractor: TracksInteractor) : ViewModel() {

    private val favoriteTracksStateLiveData = MutableLiveData<FavoriteTracksState>(FavoriteTracksState(false, emptyList<Track>().toMutableList()))
    fun observeFavoriteTrackState(): LiveData<FavoriteTracksState> = favoriteTracksStateLiveData

    fun loadFavoriteTracks(){
        viewModelScope.launch {
            tracksInteractor.getAllFavoriteTracks().collect { tracks ->
                favoriteTracksStateLiveData.postValue(FavoriteTracksState(
                    tracks.isEmpty(), (tracks?:emptyList()).toMutableList())
                )
            }
        }
    }

}