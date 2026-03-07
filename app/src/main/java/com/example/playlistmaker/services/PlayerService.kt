package com.example.playlistmaker.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.ui.PlayerFragmentState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class PlayerService(): Service(), PlayerInteractor {

    private val binder = PlayerServiceBinder()

    private var mediaPlayer: MediaPlayer? = null
    private var previewUrl = ""
    private var artistName = ""
    private var trackName = ""

    private val timeFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private var trackTimer : Job? = null

    private val _playerState = MutableStateFlow<PlayerFragmentState>(PlayerFragmentState.PlayerStatusDefault("00:00"))
    val playerState = _playerState.asStateFlow()

    override fun onCreate(){
        super.onCreate()
        createNotificationChannel()
        Log.d("playerService", "onCreate")
    }

    override fun onDestroy() {
        Log.d("playerService", "onDestroy")
        super.onDestroy()
        releasePlayer()
    }

    override fun onBind(intent: Intent?): IBinder? {
        mediaPlayer = MediaPlayer()
        previewUrl = intent?.getStringExtra("previewUrl") ?: ""
        artistName = intent?.getStringExtra("artistName") ?: ""
        trackName = intent?.getStringExtra("trackName") ?: ""
        preparePlayer(previewUrl)
        Log.d("playerService", "onBind")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("playerService", "onUnbind")
        releasePlayer()
        return super.onUnbind(intent)
    }

    private fun getForegroundServiceTypeConstant(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
    }

    private fun createNotification(notificationText: String): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Playlist Maker")
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Player service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Service for playing music"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun getPlayerSrviceState(): StateFlow<PlayerFragmentState> {
        return playerState
    }

    private fun preparePlayer(trackUrl: String) {
        if (mediaPlayer == null) return
        mediaPlayer?.setDataSource(trackUrl)
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener {
            _playerState.value = PlayerFragmentState.PlayerStatusPrepared("00:00")
//            playerFragmentStateLD.postValue(playerStatus)
            Log.d("playerService", "preparePlayer")
        }
        mediaPlayer?.setOnCompletionListener {
            _playerState.value = PlayerFragmentState.PlayerStatusPrepared("00:00")
            hideNotification()
            Log.d("playerService", "playerComplete")
        }
    }

    override fun playerControl() {
        when (_playerState.value){
            is PlayerFragmentState.PlayerStatusPrepared -> startPlayer()
            is PlayerFragmentState.PlayerStatusPaused -> startPlayer()
            is PlayerFragmentState.PlayerStatusPlaying -> pausePlayer()
            else -> {}
        }
    }

    private fun startPlayer() {
        if (mediaPlayer == null) return
        mediaPlayer?.start()
        _playerState.value = PlayerFragmentState.PlayerStatusPlaying(timeFormat.format(mediaPlayer?.currentPosition))
        Log.d("playerService", "startPlayer")
        if ((trackTimer?.isActive?:false) == false) {
            trackTimer?.cancel()
            trackTimer =  CoroutineScope(Dispatchers.Default).launch {
                delay(MP_REQUEST_INTERVAL)
                while (_playerState.value is PlayerFragmentState.PlayerStatusPlaying) {
                    setPlaingProgress()
                    delay(MP_REQUEST_INTERVAL)
                }
                setPlaingProgress()
            }
        }
    }

    private fun pausePlayer() {
        if (mediaPlayer == null) return
        mediaPlayer?.pause()
        _playerState.value = PlayerFragmentState.PlayerStatusPaused(timeFormat.format(mediaPlayer?.currentPosition))
        Log.d("playerService", "pausePlayer")
    }

    private fun releasePlayer() {
        if (mediaPlayer == null) return
        mediaPlayer?.stop()
        mediaPlayer?.setOnPreparedListener(null)
        mediaPlayer?.setOnCompletionListener(null)
        mediaPlayer?.release()
        mediaPlayer = null
        _playerState.value = PlayerFragmentState.PlayerStatusDefault("00:00")
    }

    private fun setPlaingProgress(){
        if (mediaPlayer == null) return
        when (_playerState.value) {
            is PlayerFragmentState.PlayerStatusDefault -> _playerState.value = PlayerFragmentState.PlayerStatusDefault("00:00")
            is PlayerFragmentState.PlayerStatusPrepared -> _playerState.value = PlayerFragmentState.PlayerStatusPrepared("00:00")
            is PlayerFragmentState.PlayerStatusPaused -> _playerState.value = PlayerFragmentState.PlayerStatusPaused(timeFormat.format(mediaPlayer?.currentPosition))
            is PlayerFragmentState.PlayerStatusPlaying -> _playerState.value = PlayerFragmentState.PlayerStatusPlaying(timeFormat.format(mediaPlayer?.currentPosition))
            else -> {}
        }
    }

    override fun showNotification() {
        if(_playerState.value is PlayerFragmentState.PlayerStatusPlaying){
            Log.d("playerService", "showNotification")
            ServiceCompat.startForeground(
                this,
                SERVICE_NOTIFICATION_ID,
                createNotification("$artistName -$trackName"),
                getForegroundServiceTypeConstant()
            )
        }
    }

    override fun hideNotification() {
        Log.d("playerService", "hideNotification")
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    inner class PlayerServiceBinder : Binder() {
        fun getService(): PlayerService = this@PlayerService
    }

    private companion object {
        const val MP_REQUEST_INTERVAL = 200L
        const val NOTIFICATION_CHANNEL_ID = "player_service_channel"
        const val SERVICE_NOTIFICATION_ID = 4
    }
}