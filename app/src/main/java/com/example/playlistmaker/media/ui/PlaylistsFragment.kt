package com.example.playlistmaker.media.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.data.Playlist
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.SearchFragment.TracksViewHolder
import com.example.playlistmaker.search.ui.TracksState
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.util.Date
import kotlin.getValue

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PlaylistsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlaylistsFragment : Fragment() {
    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null

    val viewModel by activityViewModel<PlaylistsViewModel>()
    private lateinit var binding: FragmentPlaylistsBinding

    val playlists = mutableListOf<Playlist>()
    val playlistsAdapter = PlaylistsAdapter(playlists)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
    }

    override fun onResume(){
        super.onResume()
        viewModel.loadPlaylists()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistsRecView = binding.playlistsRecView
        val btnNewPlaylist = binding.btnNewPlaylist

        playlistsRecView.layoutManager = GridLayoutManager(requireActivity(), 2)
        playlistsRecView.adapter = playlistsAdapter

        btnNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_newPlaylistFragment)
        }

        viewModel.observePlaylistsState().observe(viewLifecycleOwner) {playlistsStateObserver(it) }

    }

    class PlaylistsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.playlist_item_img)
        private val nameView: TextView = itemView.findViewById(R.id.playlist_item_name)
        private val tracksView: TextView = itemView.findViewById(R.id.playlist_item_tracks)

        fun bind(playlist: Playlist) {
            nameView.text = playlist.name
            tracksView.text = trackCountsToString(playlist.trackCounts)
            Glide.with(itemView)
                .load(playlist.filePath)
                .transform(CenterCrop(), RoundedCorners(32))
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
                .inflate(R.layout.playlist_recycler_layout, parent, false)
            return PlaylistsViewHolder(view)
        }

        override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
            holder.bind(items[position])
            holder.itemView.setOnClickListener {
                findNavController().navigate(
                    R.id.action_mediaFragment_to_playlistInfoFragment,
                    PlaylistInfoFragment.createArgs(items[position].id))
            }
        }

        override fun getItemCount(): Int {
            return items.size
        }
    }

    private fun playlistsStateObserver(it: PlaylistsState) {
        playlists.clear()
        when {
            it.isEmpty -> showNothing()
            else -> {
                playlists.addAll(it.playlists)
                playlistsAdapter.notifyDataSetChanged()
                showPlaylists()
            }
        }
    }

    fun showPlaylists(){
        binding.playlistsRecView.visibility = VISIBLE
        binding.playlistsEmptyFrame.visibility = GONE
    }

    fun showNothing(){
        binding.playlistsRecView.visibility = GONE
        binding.playlistsEmptyFrame.visibility = VISIBLE
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}