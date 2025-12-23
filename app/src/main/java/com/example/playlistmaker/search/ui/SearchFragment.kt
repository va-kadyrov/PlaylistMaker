package com.example.playlistmaker.search.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.Track
import com.google.gson.Gson
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Date
import com.example.playlistmaker.main.ui.TAG

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()
    private lateinit var binding: FragmentSearchBinding

    private lateinit var inputEditText: EditText
    private var isClickAllowed = true
    private var clickDebounceJob : Job? = null

    val tracks = mutableListOf<Track>()
    val tracksHistory = mutableListOf<Track>()
    val tracksAdapter = TracksAdapter(tracks, false)
    val searchHistoryAdapter = TracksAdapter(tracksHistory, true)

    override fun onResume(){
        super.onResume()
        isClickAllowed = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.observeTrackState().observe(viewLifecycleOwner ) {tracksStateObserver(it)}
        viewModel.observeTrackHistoryState().observe(viewLifecycleOwner) {trackHistoryStateObserver(it)}

        inputEditText = binding.searchEt
        val btnClear = binding.searchBtnClear
        val btnReload = binding.searchBtnReload
        val recView = binding.searchRecView
        val btnClearHistory = binding.searchBtnClearHistory
        val recHistory = binding.searchHistoryRecView

        recView.adapter = tracksAdapter
        recHistory.adapter = searchHistoryAdapter

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (inputEditText.hasFocus()) viewModel.searchFielsOnFocus()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) btnClear.visibility = View.INVISIBLE
                else btnClear.visibility = View.VISIBLE
                viewModel.searchTextChanged((s?:"").toString())
            }

            override fun afterTextChanged(s: Editable?) {
                //empty
            }
        }

        btnClear.setOnClickListener {
            inputEditText.setText("")
            viewModel.searchTextEntered("")
            val inputMethodManager = getSystemService(requireContext(), InputMethodManager::class.java) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        btnClearHistory.setOnClickListener{
            viewModel.clearTracksHistory()
        }

        btnReload.setOnClickListener {
            viewModel.repeatSearch()
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchTextEntered((inputEditText.text?:"").toString())
            }
            false
        }
    }

    fun showTracks(){
        binding.searchRecView.visibility = VISIBLE
        binding.searchHistory.visibility = GONE
        binding.searchNothingFound.visibility = GONE
        binding.searchNetworkError.visibility = GONE
        binding.progressBar.visibility = GONE
    }

    fun showHistory(){
        binding.searchRecView.visibility = GONE
        binding.searchHistory.visibility = VISIBLE
        binding.searchNothingFound.visibility = GONE
        binding.searchNetworkError.visibility = GONE
        binding.progressBar.visibility = GONE
    }

    fun showNothingFound(){
        binding.searchRecView.visibility = GONE
        binding.searchHistory.visibility = GONE
        binding.searchNothingFound.visibility = VISIBLE
        binding.searchNetworkError.visibility = GONE
        binding.progressBar.visibility = GONE
    }

    fun showNetworkError(){
        binding.searchRecView.visibility = GONE
        binding.searchHistory.visibility = GONE
        binding.searchNothingFound.visibility = GONE
        binding.searchNetworkError.visibility = VISIBLE
        binding.progressBar.visibility = GONE
    }

    fun showProgressBar(){
        binding.searchRecView.visibility = GONE
        binding.searchHistory.visibility = GONE
        binding.searchNothingFound.visibility = GONE
        binding.searchNetworkError.visibility = GONE
        binding.progressBar.visibility = VISIBLE
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

    inner class TracksAdapter(private val items: List<Track>, private val isHistory: Boolean): RecyclerView.Adapter<TracksViewHolder> () {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.search_recycler_layout, parent, false)
            return TracksViewHolder(view)
        }

        override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
            holder.bind(items[position])
            holder.itemView.setOnClickListener {
                if (clickDebounce()) {
                    if (!isHistory) {
                        viewModel.addTrackToHistory(items[position])
                    }
                    openPlayer(items[position])
                }
           }
        }

        override fun getItemCount(): Int {
            return items.size
        }

        fun openPlayer(track: Track) {
            val gson: Gson by inject()
            val trackJson: String = gson.toJson(track)
            Log.i(TAG, "Player are opening")
            findNavController().navigate(
                R.id.action_searchFragment_to_playerFragment,
                PlayerFragment.createArgs(trackJson)
            )
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

    private fun tracksStateObserver(it: TracksState) {
        tracks.clear()
        when {
            it.isLoading -> showProgressBar()
            it.isEmpty -> showNothingFound()
            it.isError -> showNetworkError()
            else -> {
                tracks.addAll(it.content)
                tracksAdapter.notifyDataSetChanged()
                showTracks()
            }
        }
    }

    private fun trackHistoryStateObserver(it: TracksHistoryState) {
        if (it.isVisible) {
            tracksHistory.clear()
            tracksHistory.addAll(it.content)
            searchHistoryAdapter.notifyDataSetChanged()
            showHistory()
        } else {
            showTracks()
        }
   }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}

