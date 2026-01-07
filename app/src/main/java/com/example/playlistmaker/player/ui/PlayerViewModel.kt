package com.example.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.FavoriteTracksInteractor
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class PlayerViewModel(private val favoriteTracksInteractor: FavoriteTracksInteractor): ViewModel() {

    private val mediaPlayer = MediaPlayer()

    private val playerFragmentState = PlayerFragmentState(STATE_DEFAULT, "00:00", false)
    private val playerFragmentStateLD = MutableLiveData<PlayerFragmentState>(playerFragmentState)
    fun observePlayerFragmentState(): LiveData<PlayerFragmentState> = playerFragmentStateLD

    private val timeFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private var trackTimer : Job? = null

    fun init(track: Track) {
        if (playerFragmentState.playerStatus == STATE_DEFAULT) {
            preparePlayer(track.previewUrl)
        }
        playerFragmentState.isFavorite = track.isFavorite
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
            playerFragmentStateLD.postValue(playerFragmentState)
        }
        mediaPlayer.setOnCompletionListener {
            playerFragmentState.playerStatus = STATE_PREPARED
            playerFragmentStateLD.postValue(playerFragmentState)
        }
    }

    fun playerDestroy() {
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerFragmentState.playerStatus = STATE_PLAYING
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
            viewModelScope.launch { favoriteTracksInteractor.delete(track) }
            playerFragmentState.isFavorite = false
        }
        else {
            track.isFavorite = true
            viewModelScope.launch { favoriteTracksInteractor.insert(track) }
            playerFragmentState.isFavorite = true
        }
        playerFragmentStateLD.postValue(playerFragmentState)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerFragmentState.playerStatus = STATE_PAUSED
        playerFragmentStateLD.postValue(playerFragmentState)
    }

    private fun setPlaingProgress(){
        if ((playerFragmentState.playerStatus == STATE_PLAYING) or (playerFragmentState.playerStatus == STATE_PAUSED)) {
            playerFragmentState.trackTimeProgress = timeFormat.format(mediaPlayer.currentPosition) }
        else {
            playerFragmentState.trackTimeProgress = timeFormat.format(0) }
            playerFragmentStateLD.postValue(playerFragmentState)
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val MP_REQUEST_INTERVAL = 300L
    }
}