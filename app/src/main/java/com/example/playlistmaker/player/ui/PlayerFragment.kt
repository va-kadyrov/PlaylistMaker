package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import java.util.Date
import java.util.Locale
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.media.data.Playlist
import com.example.playlistmaker.search.domain.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.android.ext.android.inject

class PlayerFragment : Fragment() {

    private val viewModel: PlayerViewModel by inject()
    private lateinit var binding: FragmentPlayerBinding
    private lateinit var btnPlay : Button
    private lateinit var plaingProgress: TextView

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

        val btnBack     = binding.playerTbBack
        val btnLike     = binding.playerBtnLike
        val btnAddTrack = binding.playerBtnAddTrack

        val playerBottomSheet = binding.playerBottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(playerBottomSheet)
        val playerPlaylistsRecView = binding.playerPlaylistsRecView
        val btnNewPlaylist2 = binding.btnNewPlaylist2

        val playlists = mutableListOf<Playlist>()
        val playerPlaylistsAdapter = PlaylistsAdapter(playlists)

        btnPlay = binding.playerBtnPlay
        plaingProgress = binding.playerPlayingProgress
        btnPlay.isEnabled = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        playerPlaylistsRecView.adapter = playerPlaylistsAdapter

        btnAddTrack.setOnClickListener {
            viewModel.loadPlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED }

        player_trackName.text       = track.trackName
        player_artistName.text      = track.artistName
        player_trackTime.text       = timeFormat.format(Date(track.trackTimeMillis))
        player_collectionName.text  = track.collectionName
        player_releaseDate.text     = yearFormat.format(track.releaseDate)
        player_genre.text           = track.primaryGenreName
        player_country.text         = track.country

        viewModel.init(track)

        if (player_collectionName.text.isNullOrEmpty()) {
            player_collectionName.visibility = View.GONE
            binding.playerCollectionNameTitle.visibility = View.GONE
        }

        Glide.with(this).load(getCoverArtwork(track.artworkUrl100))
                .transform(FitCenter(), RoundedCorners(12))
                .placeholder(R.drawable.album_cover_empty).into(player_albumCover)

        viewModel.observePlayerFragmentState().observe(viewLifecycleOwner){
            when(it.action) {
                ACTION_PLAYER_STATUS -> {
                    when(it.playerStatus) {
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
                }}}

                ACTION_TIMER_UPDATE -> {
                    plaingProgress.text = it.trackTimeProgress
                }

                ACTION_PLAYLISTS_UPDATE -> {
                    playlists.clear()
                    playlists.addAll(it.playlists)
                    playerPlaylistsAdapter.notifyDataSetChanged()
                }

                ACTION_IS_FAVORITE -> {
                    if (it.isFavorite) {
                        btnLike.setBackgroundResource(R.drawable.btn_like_track1)
                    } else btnLike.setBackgroundResource(R.drawable.btn_like_track)
                }

                ACTION_TRACK_IN_PLAYLIST -> {
                    when(it.trackInPlaylist) {
                        TRACK_ADDED_IN_PL_SUCCESSFULLY -> {
                            Toast.makeText(requireActivity(), "Добавлено в плейлист ${it.playlistName}", Toast.LENGTH_SHORT).show()
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                        }
                        TRACK_ALREADY_IN_PLAYLIST -> {
                            Toast.makeText(requireActivity(), "Трек уже добавлен в плейлист ${it.playlistName}", Toast.LENGTH_SHORT).show() }
                    }
                }
            }
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        btnPlay.setOnClickListener {
            viewModel.playerControl()
        }

        btnLike.setOnClickListener {
            viewModel.onBtnLikeClicked(track)
        }

        btnNewPlaylist2.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
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

    fun getCoverArtwork(artworkUrl100: String) = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

    class PlaylistsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.playlist_item_img)
        private val nameView: TextView = itemView.findViewById(R.id.playlist_item_name)
        private val tracksView: TextView = itemView.findViewById(R.id.playlist_item_tracks)

        fun bind(playlist: Playlist) {
            nameView.text = playlist.name
            tracksView.text = trackCountsToString(playlist.trackCounts)
            Glide.with(itemView)
                .load(playlist.filePath)
                .transform(FitCenter(), RoundedCorners(2))
                .placeholder(R.drawable.album_cover_empty)
                .into(imageView)
            }
        private fun trackCountsToString(trackCounts: Int): String {
            when {
                trackCounts in 11..20 -> return "$trackCounts треков"
                trackCounts % 10 == 1 -> return "$trackCounts трек"
                trackCounts % 10 in 2..4 -> return "$trackCounts трека"
                else -> return "$trackCounts треков"
            }
        }
    }

    inner class PlaylistsAdapter(private val items: List<Playlist>): RecyclerView.Adapter<PlaylistsViewHolder> () {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.playlist_bottom_recycler_layout, parent, false)
            return PlaylistsViewHolder(view)
        }

        override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
            holder.bind(items[position])
            holder.itemView.setOnClickListener {
                viewModel.addTrackToPlaylist(items[position])
            }
        }

        override fun getItemCount(): Int {
            return items.size
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val TRACK_ADDED_IN_PL_SUCCESSFULLY = 1
        private const val TRACK_ALREADY_IN_PLAYLIST = 2

        private const val ACTION_PLAYER_STATUS      = 1
        private const val ACTION_TIMER_UPDATE       = 2
        private const val ACTION_PLAYLISTS_UPDATE   = 3
        private const val ACTION_IS_FAVORITE        = 4
        private const val ACTION_TRACK_IN_PLAYLIST  = 5

        private const val TRACK_JSON = "track_json"

        fun createArgs(trackJson: String) : Bundle = bundleOf(TRACK_JSON to trackJson)

    }
}