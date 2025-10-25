package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import android.icu.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.Track
import org.koin.android.ext.android.inject

class PlayerActivity : AppCompatActivity() {

    private val viewModel: PlayerViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        viewModel.observePlayerState().observe(this){
            when(it) {
                STATE_DEFAULT -> {
                    btnPlay.setBackgroundResource(R.drawable.btn_play_track)
                    btnPlay.isEnabled = false
                }
                STATE_PLAYING -> {
                    btnPlay.setBackgroundResource(R.drawable.btn_pause)
                }
                STATE_PREPARED, STATE_PAUSED -> {
                    btnPlay.setBackgroundResource(R.drawable.btn_play_track)
                    btnPlay.isEnabled = true
                }
            }
        }
        viewModel.observeTrackTimerState().observe(this) {
            plaingProgress.text = it
        }

        val timeFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

        val json = intent.getStringExtra("track")
        val track = Gson().fromJson(json, Track::class.java)
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

        viewModel.init(track.previewUrl)

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
            viewModel.playerControl()
        }

        btnLike.setOnClickListener {
            //plaingProgress.text = "like"
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.playerDestroy()
    }

    private lateinit var btnPlay : Button
    private lateinit var plaingProgress: TextView

    fun getCoverArtwork(artworkUrl100: String) = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

    companion object {
        private const val TAG = "myLog"
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}