package com.example.playlistmaker.media.ui

import android.content.Intent
import android.icu.text.SimpleDateFormat
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistInfoBinding
import com.example.playlistmaker.main.ui.TAG
import com.example.playlistmaker.media.data.Playlist
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.SearchFragment.TracksViewHolder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.util.Locale
import kotlin.getValue
import kotlin.math.round

class PlaylistInfoFragment : Fragment() {

    val viewModel by activityViewModel<PlaylistInfoViewModel>()
    private lateinit var binding: FragmentPlaylistInfoBinding

    private val tracks = mutableListOf<Track>()
    private val tracksAdapter = TracksAdapter(tracks)

    private var isClickAllowed = true
    private var clickDebounceJob : Job? = null
    private lateinit var playlist: Playlist
    private var playlistId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        isClickAllowed = true
        viewModel.loadPlaylistInfo(playlistId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistInfoName            = binding.playlistInfoName
        val playlistInfoDescription     = binding.playlistInfoDescription
        val playlistInfoImage           = binding.playlistInfoImage
        val playlistInfoMinutes         = binding.playlistInfoMinutes
        val playlistInfoTracks          = binding.playlistInfoTracks
        val playlistInfoTracksRecView   = binding.playlistInfoTracksRecView
        val playlistInfoEmptyFrame      = binding.playlistInfoEmptyFrame
        val btnBack                     = binding.playlistInfoTbBack
        val playlistShare               = binding.playlistShare
        val playlistMenu                = binding.playlistMenu

        val playerBottomBtnShare            = binding.playerBottomBtnShare
        val playerBottomBtnDeletePlaylist   = binding.playerBottomBtnDeletePlaylist
        val playerBottomBtnEdit             = binding.playerBottomBtnEdit

        val playerBottomSheetMenu           = binding.playerBottomSheetMenu
        val bottomSheetBehavior             = BottomSheetBehavior.from(playerBottomSheetMenu)

        playlistId = requireArguments().getLong(PLAYLIST_ID) ?: 0

        playlistInfoTracksRecView.adapter = tracksAdapter

        btnBack.setOnClickListener {
            findNavController().navigateUp() }

        playlistShare.setOnClickListener {
            showShareDialog()
        }

        playlistMenu.setOnClickListener {
            showBottomMenu()
        }

        playerBottomBtnShare.setOnClickListener {
            showShareDialog()
        }

        playerBottomBtnDeletePlaylist.setOnClickListener {
            showDeletePlaylistDialog()
        }

        playerBottomBtnEdit.setOnClickListener {
            findNavController().navigate(
                R.id.action_playlistInfoFragment_to_editPlaylistFragment,
                EditPlaylistFragment.createArgs(playlistId)
            )
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = GONE
                        binding.playerBottomSheet.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })


        viewModel.observePlaylistInfoState().observe(viewLifecycleOwner) {
            when (it.action) {
                ACTION_INFO_LOADED -> {
                    playlist = it.playlist!!
                    playlistInfoName.setText(playlist.name)
                    playlistInfoDescription.setText(playlist.description)
                    playlistInfoMinutes.setText(trackCountsToString(playlist.trackCounts))
                    playlistInfoTracks.setText(durationToString(playlist.totalDuration))

                    Glide.with(view)
                        .load(playlist.filePath)
                        .transform(CenterCrop())
                        .placeholder(R.drawable.album_cover_empty)
                        .into(playlistInfoImage)
                    tracks.clear()
                    tracks.addAll(it.tracks)
                    tracksAdapter.notifyDataSetChanged()
                    playlistInfoTracksRecView.isVisible = tracks.isNotEmpty()
                    playlistInfoEmptyFrame.isVisible = tracks.isEmpty()
                }

                ACTION_SHARE -> {

                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.putExtra(Intent.EXTRA_TEXT, it.message)
                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    shareIntent.type = "text/plain"
                    try {
                        requireActivity().startActivity(shareIntent, null)
                    } catch (e: Exception) {
                        Toast.makeText(requireActivity(), "Ошибка отправки плейлиста: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        viewModel.loadPlaylistInfo(playlistId)
    }

    inner class TracksAdapter(private val items: List<Track>): RecyclerView.Adapter<TracksViewHolder> () {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.search_recycler_layout, parent, false)
            return TracksViewHolder(view)
        }

        override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
            holder.bind(items[position])
            holder.itemView.setOnClickListener {
                if (clickDebounce()) {
                    openPlayer(items[position])
                }
            }
            holder.itemView.setOnLongClickListener {
                showDeleteTrackDialog(items[position])
            }
        }

        private fun clickDebounce(): Boolean {
            val current = isClickAllowed
            if (isClickAllowed) {
                isClickAllowed = false
                clickDebounceJob = viewLifecycleOwner.lifecycleScope.launch {
                    delay(CLICK_DEBOUNCE_DELAY)
                    isClickAllowed = true
                }
            }
            return current
        }

        override fun getItemCount(): Int {
            return items.size
        }

        fun openPlayer(track: Track) {
            val gson: Gson by inject()
            val trackJson: String = gson.toJson(track)
            Log.i(TAG, "Player are opening")
            findNavController().navigate(
                R.id.action_playlistInfoFragment_to_playerFragment,
                PlayerFragment.createArgs(trackJson)
            )
        }
    }

    private fun showDeleteTrackDialog(track: Track): Boolean {
        MaterialAlertDialogBuilder(requireActivity())
            .setMessage(R.string.want_to_delete_track) // Описание диалога
            .setNegativeButton("Нет") { dialog, which ->
            }
            .setPositiveButton("Да") { dialog, which ->
                viewModel.deleteTrackFromPlaylist(track)
            }
            .show()
            return true
    }

    private fun showDeletePlaylistDialog() {
        val message = getString(R.string.want_to_delete_playlist) + " '${playlist.name}' ?"
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(R.string.btn_delete_playlist)
            .setMessage(message) // Описание диалога
            .setNegativeButton("Нет") { dialog, which -> showBottomSheet()
            }
            .setPositiveButton("Да") { dialog, which ->
                viewModel.deletePlaylist()
                findNavController().navigateUp()
            }
            .show()
    }

    private fun showShareDialog() {
        if (tracks.isEmpty()) {
            Toast.makeText(requireActivity(), R.string.not_tracks_to_share, Toast.LENGTH_LONG).show()
        } else {
            viewModel.sharePlaylist()
        }
    }

    private fun showBottomMenu(){
        val playlist_item_name = requireActivity().findViewById<TextView>(R.id.playlist_item_name)
        val playlist_item_tracks = requireActivity().findViewById<TextView>(R.id.playlist_item_tracks)
        val playlist_item_img = requireActivity().findViewById<ImageView>(R.id.playlist_item_img)

        playlist_item_name.setText(playlist.name)
        playlist_item_tracks.setText(trackCountsToString(playlist.trackCounts))
        Glide.with(requireView())
            .load(playlist.filePath)
            .transform(CenterCrop(), RoundedCorners(2))
            .placeholder(R.drawable.album_cover_empty)
            .into(playlist_item_img)

        binding.playerBottomSheet.isVisible = false
        binding.playerBottomSheetMenu.isVisible = true
        binding.overlay.isVisible = true

        BottomSheetBehavior.from(binding.playerBottomSheetMenu).state=BottomSheetBehavior.STATE_COLLAPSED

    }

    private fun showBottomSheet(){
        binding.playerBottomSheet.isVisible = true
        binding.playerBottomSheetMenu.isVisible = false
        binding.overlay.isVisible = false
    }

    private fun trackCountsToString(trackCounts: Int): String {
        when {
            trackCounts in 11..20 -> return "$trackCounts треков"
            trackCounts % 10 == 1 -> return "$trackCounts трек"
            trackCounts % 10 in 2..4 -> return "$trackCounts трека"
            else -> return "$trackCounts треков"
        }
    }

    private fun durationToString(millis: Long): String {
        val minutes = millis/60000
        when {
            minutes in 11..20 -> return "$minutes минут"
            minutes % 10 == 1L -> return "$minutes минута"
            minutes % 10 in 2..4 -> return "$minutes минуты"
            else -> return "$minutes минут"
        }
    }

    companion object {
        private const val PLAYLIST_ID = "plailist_id"
        fun newInstance() = PlaylistInfoFragment()
        fun createArgs(plailistid: Long) : Bundle = bundleOf(PLAYLIST_ID to plailistid)

        private const val ACTION_INFO_LOADED = 1
        private const val ACTION_SHARE = 2

        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}