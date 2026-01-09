package com.example.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.data.Playlist
import com.example.playlistmaker.media.domain.PlaylistInteractor
import com.example.playlistmaker.player.domain.TracksInteractor
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class PlayerViewModel(
    private val tracksInteractor: TracksInteractor,
    private val playlistInteractor: PlaylistInteractor): ViewModel() {

    private val mediaPlayer = MediaPlayer()

    private val playerFragmentState = PlayerFragmentState(ACTION_PLAYER_STATUS, STATE_DEFAULT, "00:00", false, emptyList<Playlist>().toMutableList())
    private val playerFragmentStateLD = MutableLiveData<PlayerFragmentState>(playerFragmentState)
    fun observePlayerFragmentState(): LiveData<PlayerFragmentState> = playerFragmentStateLD

    private val timeFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private var trackTimer : Job? = null
    private lateinit var track: Track

    fun init(track: Track) {
        this.track = track
        if (playerFragmentState.playerStatus == STATE_DEFAULT) {
            preparePlayer(track.previewUrl)
        }
        playerFragmentState.isFavorite = track.isFavorite
        playerFragmentState.action = ACTION_IS_FAVORITE
        playerFragmentStateLD.postValue(playerFragmentState)
    }

    fun playerControl() {
        when(playerFragmentState.playerStatus) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    fun preparePlayer(trackUrl: String) {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerFragmentState.playerStatus = STATE_PREPARED
            playerFragmentState.action = ACTION_PLAYER_STATUS
            playerFragmentStateLD.postValue(playerFragmentState)
        }
        mediaPlayer.setOnCompletionListener {
            playerFragmentState.playerStatus = STATE_PREPARED
            playerFragmentState.action = ACTION_PLAYER_STATUS
            playerFragmentStateLD.postValue(playerFragmentState)
        }
    }

    fun playerDestroy() {
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerFragmentState.playerStatus = STATE_PLAYING
        playerFragmentState.action = ACTION_PLAYER_STATUS
        playerFragmentStateLD.postValue(playerFragmentState)
        if ((trackTimer?.isActive?:false) == false) {
            trackTimer?.cancel()
            trackTimer = viewModelScope.launch {
                delay(MP_REQUEST_INTERVAL)
                while (playerFragmentState.playerStatus == STATE_PLAYING) {
                    setPlaingProgress()
                    delay(MP_REQUEST_INTERVAL)
                }
                setPlaingProgress()
            }
        }
    }

    fun onPause(){
        if (playerFragmentState.playerStatus == STATE_PLAYING){
            pausePlayer()
        }
    }

    fun onBtnLikeClicked(track: Track){
        if (track.isFavorite) {
            track.isFavorite = false
            viewModelScope.launch { tracksInteractor.deleteFavoriteTrack(track) }
            playerFragmentState.isFavorite = false
        }
        else {
            track.isFavorite = true
            viewModelScope.launch { tracksInteractor.addFavoriteTrack(track) }
            playerFragmentState.isFavorite = true
        }
        playerFragmentState.action = ACTION_IS_FAVORITE
        playerFragmentStateLD.postValue(playerFragmentState)
    }

    fun loadPlaylists(){
        viewModelScope.launch {
            playerFragmentState.playlists.clear()
            playlistInteractor.loadAll().collect { playlists -> playerFragmentState.playlists.addAll(playlists) }
            playerFragmentState.action = ACTION_PLAYLISTS_UPDATE
            playerFragmentStateLD.postValue(playerFragmentState)
        }
    }

    fun addTrackToPlaylist(playlist: Playlist){
        if (playlist.tracks.contains(track.trackId)) {
            playerFragmentState.trackInPlaylist = TRACK_ALREADY_IN_PLAYLIST
            playerFragmentState.playlistName = playlist.name
            playerFragmentState.action = ACTION_TRACK_IN_PLAYLIST
            playerFragmentStateLD.postValue(playerFragmentState)
        } else{
            viewModelScope.launch {
                playlist.tracks.add(track.trackId)
                playlistInteractor.add(playlist)
                if (tracksInteractor.addTrack(track)){
                    playerFragmentState.trackInPlaylist = TRACK_ADDED_IN_PL_SUCCESSFULLY
                    playerFragmentState.playlistName = playlist.name
                    playerFragmentState.action = ACTION_TRACK_IN_PLAYLIST
                    playerFragmentStateLD.postValue(playerFragmentState)}
            }
            loadPlaylists()
        }
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerFragmentState.playerStatus = STATE_PAUSED
        playerFragmentState.action = ACTION_PLAYER_STATUS
        playerFragmentStateLD.postValue(playerFragmentState)
    }

    private fun setPlaingProgress(){
        if ((playerFragmentState.playerStatus == STATE_PLAYING) or (playerFragmentState.playerStatus == STATE_PAUSED)) {
            playerFragmentState.trackTimeProgress = timeFormat.format(mediaPlayer.currentPosition) }
        else {
            playerFragmentState.trackTimeProgress = timeFormat.format(0) }
            playerFragmentState.action = ACTION_TIMER_UPDATE
            playerFragmentStateLD.postValue(playerFragmentState)
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val TRACK_ADDED_IN_PL_SUCCESSFULLY = 1
        private const val TRACK_ALREADY_IN_PLAYLIST = 2
        private const val MP_REQUEST_INTERVAL = 300L

        private const val ACTION_PLAYER_STATUS      = 1
        private const val ACTION_TIMER_UPDATE       = 2
        private const val ACTION_PLAYLISTS_UPDATE   = 3
        private const val ACTION_IS_FAVORITE        = 4
        private const val ACTION_TRACK_IN_PLAYLIST  = 5

    }
}