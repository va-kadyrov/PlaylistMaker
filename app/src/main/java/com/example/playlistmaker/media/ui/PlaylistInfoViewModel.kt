package com.example.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.data.Playlist
import com.example.playlistmaker.media.domain.PlaylistInteractor
import com.example.playlistmaker.player.domain.TracksInteractor
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch

class PlaylistInfoViewModel(val playlistInteractor: PlaylistInteractor, val trackInreractor: TracksInteractor) : ViewModel() {

    var playlist: Playlist? = null
    private val playlistInfoState = MutableLiveData(PlaylistInfoState(0, null, emptyList()))
    fun observePlaylistInfoState(): LiveData<PlaylistInfoState> = playlistInfoState

    fun loadPlaylistInfo(id: Long){
        viewModelScope.launch {
            playlist = playlistInteractor.loadInfo(id).single()
            val tracks = mutableListOf<Track>()
            playlist?.totalDuration = 0
            playlist?.tracks?.forEach {
                val track = trackInreractor.getTrackInfo(it).single()
                tracks.add(track)
                playlist?.totalDuration += track.trackTimeMillis
            }
            playlistInfoState.postValue(PlaylistInfoState(ACTION_INFO_LOADED, playlist, tracks))
        }
    }

    fun deleteTrackFromPlaylist(track: Track){
        playlist?.tracks?.remove(track.trackId)
        viewModelScope.launch {
            playlistInteractor.add(playlist!!)}
        loadPlaylistInfo(playlist!!.id)
    }

    companion object {
        private const val ACTION_INFO_LOADED = 1
    }

}