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
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.util.Date
import java.util.Locale
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediaBinding
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.search.domain.Track
import org.koin.android.ext.android.inject

class PlayerFragment : Fragment() {

    private val viewModel: PlayerViewModel by inject()
    private lateinit var binding: FragmentPlayerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val timeFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
        val trackJson = requireArguments().getString(TRACK_JSON) ?: ""

        val track = Gson().fromJson(trackJson, Track::class.java)
        val yearFormat = SimpleDateFormat("YYYY")

        val player_trackName = binding.playerTrackName
        val player_artistName = binding.playerArtistName
        val player_trackTime = binding.playerTrackTime
        val player_collectionName = binding.playerCollectionName
        val player_releaseDate = binding.playerReleaseDate
        val player_genre = binding.playerGenre
        val player_country = binding.playerCountry
        val player_albumCover = binding.playerAlbumCover

        val btnBack = binding.playerTbBack
        val btnLike = binding.playerBtnLike
        btnPlay = binding.playerBtnPlay
        plaingProgress = binding.playerPlayingProgress
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
            binding.playerCollectionNameTitle.visibility = View.GONE
        }

        Glide.with(this).load(getCoverArtwork(track.artworkUrl100))
                .transform(FitCenter(), RoundedCorners(12))
                .placeholder(R.drawable.album_cover_empty).into(player_albumCover)

        viewModel.observePlayerState().observe(viewLifecycleOwner){
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
        viewModel.observeTrackTimerState().observe(viewLifecycleOwner) {
            plaingProgress.text = it
        }

        btnBack.setOnClickListener {
            findNavController().navigateUp()
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

        private const val TRACK_JSON = "track_json"

        fun createArgs(trackJson: String) : Bundle = bundleOf(TRACK_JSON to trackJson)

    }
}