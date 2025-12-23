package com.example.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class PlayerViewModel: ViewModel() {

    private val mediaPlayer = MediaPlayer()

    private val playerState = MutableLiveData<Int>(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerState

    private val trackTimerState = MutableLiveData<String>()
    fun observeTrackTimerState(): LiveData<String> = trackTimerState

    private val timeFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private var trackTimer : Job? = null

    fun init(trackUrl: String) {
        if (playerState.value == STATE_DEFAULT) {
            preparePlayer(trackUrl)
        }
    }

    fun playerControl() {
        when(playerState.value) {
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
            playerState.postValue(STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            playerState.postValue(STATE_PREPARED)
        }
    }

    fun playerDestroy() {
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState.postValue(STATE_PLAYING)
        if ((trackTimer?.isActive?:false) == false) {
            trackTimer?.cancel()
            trackTimer = viewModelScope.launch {
                delay(MP_REQUEST_INTERVAL)
                while (playerState.value == STATE_PLAYING) {
                    setPlaingProgress()
                    delay(MP_REQUEST_INTERVAL)
                }
                setPlaingProgress()
            }
        }
    }

    fun onPause(){
        if (playerState.value == STATE_PLAYING){
            pausePlayer()
        }
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState.postValue( STATE_PAUSED)
    }

    private fun setPlaingProgress(){
        var currentPosition = 0
        if ((playerState.value == STATE_PLAYING) or (playerState.value == STATE_PAUSED)) {
            currentPosition = mediaPlayer.currentPosition }
        else {
            currentPosition = 0 }
            trackTimerState.postValue(timeFormat.format(currentPosition))
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val MP_REQUEST_INTERVAL = 300L
    }
}