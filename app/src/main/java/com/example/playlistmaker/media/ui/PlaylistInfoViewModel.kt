package com.example.playlistmaker.media.ui

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.media.data.Playlist
import com.example.playlistmaker.media.domain.PlaylistInteractor
import com.example.playlistmaker.player.domain.TracksInteractor
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale

class PlaylistInfoViewModel(val playlistInteractor: PlaylistInteractor, val trackInreractor: TracksInteractor) : ViewModel() {

    var playlist: Playlist? = null
    private val playlistInfoState = MutableLiveData(PlaylistInfoState(0, null, emptyList(), ""))
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
            playlistInfoState.postValue(PlaylistInfoState(ACTION_INFO_LOADED, playlist, tracks, ""))
        }
    }

    fun deleteTrackFromPlaylist(track: Track){
        playlist?.tracks?.remove(track.trackId)
        viewModelScope.launch {
            playlistInteractor.add(playlist!!)
            if (playlistInteractor.trackIsIdle(track.trackId)) {
                trackInreractor.deleteTrack(track.trackId)
                Log.i("myTag", "track ${track.trackId} was deleted")
            }
        }
        loadPlaylistInfo(playlist!!.id)
    }

    fun deletePlaylist(){
        viewModelScope.launch {
            playlistInteractor.delete(playlist!!.id)
            playlist!!.tracks.forEach { idTrack ->
                if (playlistInteractor.trackIsIdle(idTrack)) {
                    trackInreractor.deleteTrack(idTrack)
                    Log.i("myTag", "track $idTrack was deleted")
                }
            }
            Log.i("myTag", "playlist ${playlist!!.name} was deleted")
        }
    }

    fun sharePlaylist(){

        val timeFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        var shareMessage = " ${playlist!!.name} \n " +
                    " ${playlist!!.description} \n " +
                    " количество треков: ${trackCountsToString(playlist!!.trackCounts)}"
        viewModelScope.launch {
            var num = 0
            playlist!!.tracks.forEach { trackId ->
            val track = trackInreractor.getTrackInfo(trackId).single()

            shareMessage = shareMessage + "\n $num. ${track.artistName} - ${track.trackName} (${timeFormat.format(Date(track.trackTimeMillis))})"
            }
        }

        playlistInfoState.postValue(PlaylistInfoState(ACTION_SHARE, playlist, emptyList(), shareMessage))

    }

    private fun trackCountsToString(trackCounts: Int): String {
        when {
            trackCounts in 11..20 -> return "$trackCounts треков"
            trackCounts % 10 == 1 -> return "$trackCounts трек"
            trackCounts % 10 in 2..4 -> return "$trackCounts трека"
            else -> return "$trackCounts треков"
        }
    }

    companion object {
        private const val ACTION_INFO_LOADED = 1
        private const val ACTION_SHARE = 2
    }

}