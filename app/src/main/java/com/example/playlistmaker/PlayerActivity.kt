package com.example.playlistmaker

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintSet.GONE
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.SearchActivity.Companion.TAG
import com.google.gson.Gson
import java.util.Date
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val json = intent.getStringExtra("track")
        val track = Gson().fromJson(json, Track::class.java)
       // val timeFormat = SimpleDateFormat("mm:ss")
        val yearFormat = SimpleDateFormat("YYYY")

        val player_trackName = findViewById<TextView>(R.id.player_trackName)
        val player_artistName = findViewById<TextView>(R.id.player_artistName)
        val player_trackTime = findViewById<TextView>(R.id.player_trackTime)
        val player_collectionName = findViewById<TextView>(R.id.player_collectionName)
        val player_releaseDate = findViewById<TextView>(R.id.player_releaseDate)
        val player_genre = findViewById<TextView>(R.id.player_genre)
        val player_country = findViewById<TextView>(R.id.player_country)
        val player_albumCover = findViewById<ImageView>(R.id.player_albumCover)

        val btnBack = findViewById<Toolbar>(R.id.player_tb_back)
        val btnLike = findViewById<Button>(R.id.player_btn_like)
        btnPlay = findViewById<Button>(R.id.player_btn_play)
        plaingProgress = findViewById<TextView>(R.id.player_playing_progress)
        btnPlay.isEnabled = false

        player_trackName.text       = track.trackName
        player_artistName.text      = track.artistName
        player_trackTime.text       = timeFormat.format(Date(track.trackTimeMillis))
        player_collectionName.text  = track.collectionName
        player_releaseDate.text     = yearFormat.format(track.releaseDate)
        player_genre.text           = track.primaryGenreName
        player_country.text         = track.country

        preparePlayer(track.previewUrl)

        if (player_collectionName.text.isNullOrEmpty()) {
            player_collectionName.visibility = View.GONE
            findViewById<TextView>(R.id.player_collectionName_title).visibility = View.GONE
        }

        Glide.with(applicationContext).load(getCoverArtwork(track.artworkUrl100))
                .transform(FitCenter(), RoundedCorners(12))
                .placeholder(R.drawable.album_cover_empty).into(player_albumCover)

        btnBack.setOnClickListener {
            finish()
        }

        btnPlay.setOnClickListener {
            playerControl()
        }

        btnLike.setOnClickListener {
            //plaingProgress.text = "like"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks { trackTimer }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    private val mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private lateinit var btnPlay : Button
    private lateinit var plaingProgress: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val timeFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private val trackTimer = object : Runnable{
        override fun run() {
            setPlaingProgress()
            handler.postDelayed(this, MP_REQUEST_INTERVAL) }
        }

    fun getCoverArtwork(artworkUrl100: String) = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

    private fun preparePlayer(trackUrl: String) {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            btnPlay.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            btnPlay.setBackgroundResource(R.drawable.btn_play_track)
            playerState = STATE_PREPARED
            setPlaingProgress()
            handler.removeCallbacks { trackTimer }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        btnPlay.setBackgroundResource(R.drawable.btn_pause)
        playerState = STATE_PLAYING
        handler.post(trackTimer)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        btnPlay.setBackgroundResource(R.drawable.btn_play_track)
        playerState = STATE_PAUSED
        handler.removeCallbacks { trackTimer }
    }

    private fun playerControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun setPlaingProgress(){
        var currentPosition = 0
        if ((playerState == STATE_PLAYING) or (playerState == STATE_PAUSED)) {
            currentPosition = mediaPlayer.currentPosition }
        else {
            currentPosition = 0 }
        plaingProgress.text = timeFormat.format(currentPosition)
        Log.d(TAG, "setting plaingProgress.text =  ${plaingProgress.text}")
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val MP_REQUEST_INTERVAL = 333L
    }
}