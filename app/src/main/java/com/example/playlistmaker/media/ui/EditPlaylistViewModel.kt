package com.example.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.data.Playlist
import com.example.playlistmaker.media.domain.PlaylistInteractor
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch

class EditPlaylistViewModel(playlistInteractor: PlaylistInteractor) : NewPlaylistViewModel(
    playlistInteractor
) {
    private var playlist: Playlist? = null
    private val playlistState: MutableLiveData<Playlist> = MutableLiveData(null)
    fun observePlaylistsState(): LiveData<Playlist> = playlistState

    fun loadPlaylistInfo(id: Long) {
        viewModelScope.launch {
            playlist = playlistInteractor.loadInfo(id).single()
            playlistState.postValue(playlist!!)
            playlistName = playlist!!.name
            playlistDescription = playlist!!.description
            playlistFilepath = playlist!!.filePath
        }
    }

    override fun savePlaylist(){
        viewModelScope.launch {
            if (playlist!=null)
                {playlistInteractor.add(Playlist(playlist!!.id,
                    playlistName,
                    playlistDescription,
                    playlistFilepath,
                    playlist!!.tracks,
                    playlist!!.trackCounts,
                    playlist!!.totalDuration))
                newPlaylistState.value = NewPlaylistState(playlistName.isNotBlank(), true)}
        }
    }

}
