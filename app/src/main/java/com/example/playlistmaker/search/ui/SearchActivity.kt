package com.example.playlistmaker.search.ui

import android.content.Intent
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.Track
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Date

class SearchActivity : AppCompatActivity() {

    val handler = Handler(Looper.getMainLooper())
    private lateinit var inputEditText: EditText
    private var isClickAllowed = true

    val tracks = mutableListOf<Track>()
    val tracksHistory = mutableListOf<Track>()
    val tracksAdapter = TracksAdapter(tracks, false)
    val searchHistoryAdapter = TracksAdapter(tracksHistory, true)
    lateinit var playerIntent: Intent
    private val viewModel: SearchViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewModel.observeTrackState().observe(this ) {tracksStateObserver(it)}
        viewModel.observeTrackHistoryState().observe(this) {trackHistoryStateObserver(it)}

        playerIntent = Intent(this, PlayerActivity::class.java)

        inputEditText = findViewById<EditText>(R.id.search_et)
        val btnClear = findViewById<ImageView>(R.id.search_btn_clear)
        val btnBack = findViewById<Toolbar>(R.id.search_tb_back)
        val btnReload = findViewById<Button>(R.id.search_btn_reload)
        val recView = findViewById<RecyclerView>(R.id.search_recView)
        val btnClearHistory = findViewById<Button>(R.id.search_btn_clear_history)
        val recHistory = findViewById<RecyclerView>(R.id.search_history_recView)

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
                Log.d(TAG, "onTextChanged s=${s?:""}")
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
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        btnClearHistory.setOnClickListener{
            viewModel.clearTracksHistory()
        }

        btnBack.setOnClickListener {
            finish()
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

    override fun onDestroy() {
        super.onDestroy()
    }

    fun showTracks(){
        findViewById<RecyclerView>(R.id.search_recView).visibility = VISIBLE
        findViewById<LinearLayout>(R.id.search_history).visibility = GONE
        findViewById<LinearLayout>(R.id.search_nothing_found).visibility = GONE
        findViewById<LinearLayout>(R.id.search_network_error).visibility = GONE
        findViewById<ProgressBar>(R.id.progressBar).visibility = GONE
    }

    fun showHistory(){
        findViewById<RecyclerView>(R.id.search_recView).visibility = GONE
        findViewById<LinearLayout>(R.id.search_history).visibility = VISIBLE
        findViewById<LinearLayout>(R.id.search_nothing_found).visibility = GONE
        findViewById<LinearLayout>(R.id.search_network_error).visibility = GONE
        findViewById<ProgressBar>(R.id.progressBar).visibility = GONE
    }

    fun showNothingFound(){
        findViewById<RecyclerView>(R.id.search_recView).visibility = GONE
        findViewById<LinearLayout>(R.id.search_history).visibility = GONE
        findViewById<LinearLayout>(R.id.search_nothing_found).visibility = VISIBLE
        findViewById<LinearLayout>(R.id.search_network_error).visibility = GONE
        findViewById<ProgressBar>(R.id.progressBar).visibility = GONE
    }

    fun showNetworkError(){
        findViewById<RecyclerView>(R.id.search_recView).visibility = GONE
        findViewById<LinearLayout>(R.id.search_history).visibility = GONE
        findViewById<LinearLayout>(R.id.search_nothing_found).visibility = GONE
        findViewById<LinearLayout>(R.id.search_network_error).visibility = VISIBLE
        findViewById<ProgressBar>(R.id.progressBar).visibility = GONE
    }

    fun showProgressBar(){
        findViewById<RecyclerView>(R.id.search_recView).visibility = GONE
        findViewById<LinearLayout>(R.id.search_history).visibility = GONE
        findViewById<LinearLayout>(R.id.search_nothing_found).visibility = GONE
        findViewById<LinearLayout>(R.id.search_network_error).visibility = GONE
        findViewById<ProgressBar>(R.id.progressBar).visibility = VISIBLE
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

        fun openPlayer(track: Track){
            val json = Gson().toJson(track)
            playerIntent.putExtra("track", json)
            startActivity(playerIntent)
        }
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true}, CLICK_DEBOUNCE_DELAY)
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
        const val TAG = "myLog"
        private const val CLICK_DEBOUNCE_DELAY = 1000L}
}