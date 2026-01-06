package com.example.playlistmaker.media.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.main.ui.TAG
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.TracksState
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.util.Date
import kotlin.getValue

class FavoriteTracksFragment : Fragment() {

    val viewModel by activityViewModel<FavoriteTracksViewModel>()
    private lateinit var binding: FragmentFavoriteTracksBinding

    private var isClickAllowed = true
    private var clickDebounceJob : Job? = null

    val tracks = mutableListOf<Track>()
    val tracksAdapter = TracksAdapter(tracks)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
    }

    override fun onResume(){
        super.onResume()
        isClickAllowed = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        //return inflater.inflate(R.layout.fragment_favorite_tracks, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeFavoriteTrackState().observe(viewLifecycleOwner ) {favoriteTracksStateObserver(it)}

        val recView = binding.favoriteRecView
        recView.adapter = tracksAdapter

        viewModel.loadFavoriteTracks()
    }

    class TracksViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val authorView: TextView = itemView.findViewById(R.id.search_rec_track_author)
        private val nameView: TextView = itemView.findViewById(R.id.search_rec_track_name)
        private val timeView: TextView = itemView.findViewById(R.id.search_rec_track_time)
        private val imgView: ImageView = itemView.findViewById(R.id.search_rec_img)
        private val timeFormat = SimpleDateFormat("mm:ss")

        fun bind(track: Track) {
            authorView.text = track.artistName
            nameView.text = track.trackName
            timeView.text = timeFormat.format(Date(track.trackTimeMillis))
            Glide.with(itemView)
                .load(track.artworkUrl100)
                .transform(FitCenter(), RoundedCorners(6))
                .placeholder(R.drawable.track_empty_img)
                .into(imgView)
        }
    }

    inner class TracksAdapter(private val items: List<Track>): RecyclerView.Adapter<TracksViewHolder> () {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.search_recycler_layout, parent, false)
            return TracksViewHolder(view)
        }

        override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
            holder.bind(items[position])
            holder.itemView.setOnClickListener {
                if (clickDebounce()) {
                    openPlayer(items[position])
                }
            }
        }

        private fun clickDebounce() : Boolean {
            val current = isClickAllowed
            if (isClickAllowed) {
                isClickAllowed = false
                clickDebounceJob = viewLifecycleOwner.lifecycleScope.launch  {
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
                R.id.action_mediaFragment_to_playerFragment,
                PlayerFragment.createArgs(trackJson)
            )
        }
    }

    private fun favoriteTracksStateObserver(it: FavoriteTracksState) {
        tracks.clear()
        when {
            it.isEmpty -> shownNothing()
            else -> {
                tracks.addAll(it.tracks)
                tracksAdapter.notifyDataSetChanged()
                showTracks()
            }
        }
    }

    private fun showTracks(){
        binding.favoriteRecView.visibility = VISIBLE
        binding.favoriteEmptyFrame.visibility = GONE
    }

    private fun shownNothing(){
        binding.favoriteRecView.visibility = GONE
        binding.favoriteEmptyFrame.visibility = VISIBLE
    }

    companion object {
        fun newInstance() = FavoriteTracksFragment()
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}


