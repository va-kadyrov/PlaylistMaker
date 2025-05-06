package com.example.playlistmaker

import android.icu.text.SimpleDateFormat
import android.os.Bundle
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
import com.google.gson.Gson
import java.util.Date

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val json = intent.getStringExtra("track")
        val track = Gson().fromJson(json, Track::class.java)
        val timeFormat = SimpleDateFormat("mm:ss")
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

        player_trackName.text       = track.trackName
        player_artistName.text      = track.artistName
        player_trackTime.text       = timeFormat.format(Date(track.trackTimeMillis))
        player_collectionName.text  = track.collectionName
        player_releaseDate.text     = yearFormat.format(track.releaseDate)
        player_genre.text           = track.primaryGenreName
        player_country.text         = track.country

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

    }

    fun getCoverArtwork(artworkUrl100: String) = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
}