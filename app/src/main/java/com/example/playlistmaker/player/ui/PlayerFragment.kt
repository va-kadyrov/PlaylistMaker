package com.example.playlistmaker.player.ui

import android.Manifest
import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import android.icu.text.SimpleDateFormat
import android.os.IBinder
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import java.util.Date
import java.util.Locale
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.media.data.Playlist
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.services.PlayerService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.android.ext.android.inject
import kotlin.getValue

class PlayerFragment : Fragment() {

    private val viewModel: PlayerViewModel by inject()
    private lateinit var binding: FragmentPlayerBinding
    private lateinit var btnPlay : PlaybackButtonView
    private lateinit var plaingProgress: TextView

    private val playerServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as PlayerService.PlayerServiceBinder
            viewModel.setPlayerInteractor(binder.getService())
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            viewModel.removePlayerInteractor()
        }
    }

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

        bindPlayerService(track)

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
            when(it) {
                is PlayerFragmentState.PlayerStatusDefault -> {
                    btnPlay.isEnabled = false
                    plaingProgress.text = it.trackTimeProgress }
                is PlayerFragmentState.PlayerStatusPlaying -> {
                    btnPlay.isEnabled = true
                    plaingProgress.text = it.trackTimeProgress}
                is PlayerFragmentState.PlayerStatusPaused -> {
                    btnPlay.isEnabled = true
                    plaingProgress.text = it.trackTimeProgress}
                is PlayerFragmentState.PlayerStatusPrepared -> {
                    btnPlay.isEnabled = true
                    plaingProgress.text = it.trackTimeProgress
                    btnPlay.switchState(false)}
                is PlayerFragmentState.PlaylistsUpdate -> {
                    playlists.clear()
                    playlists.addAll(it.playlists)
                    playerPlaylistsAdapter.notifyDataSetChanged()
                }
                is PlayerFragmentState.IsFavoriteUpdate -> {
                    if (it.isFavorite) {
                        btnLike.setBackgroundResource(R.drawable.btn_like_track1)
                    } else btnLike.setBackgroundResource(R.drawable.btn_like_track)
                }
                is PlayerFragmentState.TrackAddedInPlaylist -> {
                    Toast.makeText(requireActivity(), "Добавлено в плейлист ${it.playlistName}", Toast.LENGTH_SHORT).show()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
                is PlayerFragmentState.TrackAlreadyInPlaylist -> {
                    Toast.makeText(requireActivity(), "Трек уже добавлен в плейлист ${it.playlistName}", Toast.LENGTH_SHORT).show()
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

    private fun bindPlayerService(track: Track) {
        val intent = Intent(requireContext(), PlayerService::class.java).apply {
            putExtra("previewUrl", track.previewUrl)
            putExtra("artistName", track.artistName)
            putExtra("trackName", track.trackName)
        }
        requireActivity().bindService(intent, playerServiceConnection, BIND_AUTO_CREATE)
    }

    private fun unbindPlayerService() {
        requireActivity().unbindService(playerServiceConnection)
    }

    override fun onPause() {
        super.onPause()
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
            viewModel.showNotification()
        } else {
            Toast.makeText(requireContext(), "Для показа уведомления необходимо разрешение.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
            viewModel.hideNotification()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbindPlayerService()
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
                .transform(CenterCrop(), RoundedCorners(2))
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
        private const val TRACK_JSON = "track_json"
        fun createArgs(trackJson: String) : Bundle = bundleOf(TRACK_JSON to trackJson)
    }
}

