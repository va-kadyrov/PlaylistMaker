package com.example.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.main.ui.TAG
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import java.util.Locale

class PlayerViewModel: ViewModel() {

    private val mediaPlayer = MediaPlayer()

    private val playerState = MutableLiveData<Int>(STATE_DEFAULT)
    fun observePlayerState(): LiveData<Int> = playerState

    private val trackTimerState = MutableLiveData<String>()
    fun observeTrackTimerState(): LiveData<String> = trackTimerState

//    private val handler = Handler(Looper.getMainLooper())
    private val timeFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private var trackTimer : Job? = null
//    private val trackTimer = object : Runnable{
//        override fun run() {
//            setPlaingProgress()
//            handler.postDelayed(this, MP_REQUEST_INTERVAL) }
//    }

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
//            setPlaingProgress()
//            handler.removeCallbacks { trackTimer }
        }
    }

    fun playerDestroy() {
//        handler.removeCallbacks { trackTimer }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState.postValue(STATE_PLAYING)
        Log.i(TAG, "trackTimer=$trackTimer")
        if ((trackTimer?.isActive?:false) == false) {
            trackTimer = viewModelScope.launch {
                Log.i(TAG, "trackTimer launch")
                delay(MP_REQUEST_INTERVAL)
                Log.i(TAG, "playerState.value=${playerState.value}")
                while (playerState.value == STATE_PLAYING) {
                    setPlaingProgress()
                    delay(MP_REQUEST_INTERVAL)
                    Log.i(TAG, "trackTimer working...")
                }
                setPlaingProgress()
                Log.i(TAG, "trackTimer completed")
                Log.i(TAG, "playerState.value=${playerState.value}")
            }
        }
//        handler.post(trackTimer)
    }

    fun onPause(){
        if (playerState.value == STATE_PLAYING){
            pausePlayer()
        }
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState.postValue( STATE_PAUSED)
//        handler.removeCallbacks { trackTimer }
//        if (trackTimer?.isActive?:false == true) {
//            trackTimer?.cancel()
//        }
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