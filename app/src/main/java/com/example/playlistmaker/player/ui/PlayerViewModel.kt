package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.data.Playlist
import com.example.playlistmaker.media.domain.PlaylistInteractor
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.TracksInteractor
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val tracksInteractor: TracksInteractor,
    private val playlistInteractor: PlaylistInteractor): ViewModel() {

    private val playlists = emptyList<Playlist>().toMutableList()
    private var playerInteractor: PlayerInteractor? = null

    private val playerFragmentStateLD = MutableLiveData<PlayerFragmentState>(PlayerFragmentState.PlayerStatusDefault("00:00"))
    fun observePlayerFragmentState(): LiveData<PlayerFragmentState> = playerFragmentStateLD

    private lateinit var track: Track

    fun init(track: Track) {
        this.track = track
        if (track.isFavorite) playerFragmentStateLD.postValue(PlayerFragmentState.IsFavoriteUpdate(track.isFavorite))
    }

    fun setPlayerInteractor(playerInteractor: PlayerInteractor) {
        this.playerInteractor = playerInteractor
        viewModelScope.launch {
            playerInteractor.getPlayerSrviceState().collect {
                playerFragmentStateLD.postValue(it)
            }
        }
    }

    fun removePlayerInteractor(){
        this.playerInteractor = null
    }

    fun playerControl() {
        playerInteractor?.playerControl()
    }

    fun showNotification(){
        playerInteractor?.showNotification()
    }

    fun hideNotification(){
        playerInteractor?.hideNotification()
    }

    fun onBtnLikeClicked(track: Track){
        if (track.isFavorite) {
            track.isFavorite = false
            viewModelScope.launch { tracksInteractor.deleteFavoriteTrack(track) }
            playerFragmentStateLD.postValue(PlayerFragmentState.IsFavoriteUpdate(false))
        }
        else {
            track.isFavorite = true
            viewModelScope.launch { tracksInteractor.addFavoriteTrack(track) }
            playerFragmentStateLD.postValue(PlayerFragmentState.IsFavoriteUpdate(true))
        }
    }

    fun loadPlaylists(){
        viewModelScope.launch {
            playlistInteractor.loadAll().collect {
                loadedPlaylists -> playlists.addAll(loadedPlaylists)
            }
            playerFragmentStateLD.postValue(PlayerFragmentState.PlaylistsUpdate(playlists.toList()))
        }
    }

    fun addTrackToPlaylist(playlist: Playlist){
        if (playlist.tracks.contains(track.trackId)) {
            playerFragmentStateLD.postValue(PlayerFragmentState.TrackAlreadyInPlaylist(playlist.name))
        } else{
            viewModelScope.launch {
                playlist.tracks.add(track.trackId)
                playlistInteractor.add(playlist)
                if (tracksInteractor.addTrack(track)){
                    playerFragmentStateLD.postValue(PlayerFragmentState.TrackAddedInPlaylist(playlist.name))}
            }
            loadPlaylists()
        }
    }
}