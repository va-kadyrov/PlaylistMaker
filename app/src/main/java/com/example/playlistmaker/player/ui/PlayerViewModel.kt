package com.example.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Locale

class PlayerViewModel: ViewModel() {

    private val mediaPlayer = MediaPlayer()

    private val playerState = MutableLiveData<Int>(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerState

    private val trackTimerState = MutableLiveData<String>()
    fun observeTrackTimerState(): LiveData<String> = trackTimerState

    private val handler = Handler(Looper.getMainLooper())
    private val timeFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private val trackTimer = object : Runnable{
        override fun run() {
            setPlaingProgress()
            handler.postDelayed(this, MP_REQUEST_INTERVAL) }
    }

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
            setPlaingProgress()
            handler.removeCallbacks { trackTimer }
        }
    }

    fun playerDestroy() {
//        handler.removeCallbacks { trackTimer }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState.postValue( STATE_PLAYING)
        handler.post(trackTimer)
    }

    fun onPause(){
        if (playerState.value == STATE_PLAYING){
            pausePlayer()
        }
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState.postValue( STATE_PAUSED)
        handler.removeCallbacks { trackTimer }
    }

    private fun setPlaingProgress(){
        var currentPosition = 0
        if ((playerState.value == STATE_PLAYING) or (playerState.value == STATE_PAUSED)) {
            currentPosition = mediaPlayer.currentPosition }
        else {
            currentPosition = 0 }
            trackTimerState.postValue(timeFormat.format(currentPosition))
//        Log.d(TAG, "setting plaingProgress.text =  ${plaingProgress.text}")
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val MP_REQUEST_INTERVAL = 333L
    }
}