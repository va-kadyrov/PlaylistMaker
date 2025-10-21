package com.example.playlistmaker.ui

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
import com.example.playlistmaker.Creator
import com.google.gson.Gson
import java.util.Date
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.Track
import com.example.playlistmaker.domain.api.TracksConsumer
import com.example.playlistmaker.domain.api.TracksHistoryInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.App.Companion.PM_SHARED_PREFERENCES

class SearchActivity : AppCompatActivity() {

    val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable {searchTracks() }
    private lateinit var inputEditText: EditText
    private var isClickAllowed = true
    private lateinit var tracksInteractor: TracksInteractor
    private lateinit var tracksHistoryInteractor: TracksHistoryInteractor

    var inputText = ""
    var lastQuery = ""
    val tracks = mutableListOf<Track>()
    val tracksHistory = mutableListOf<Track>()
    val tracksAdapter = TracksAdapter(tracks, false)
    val searchHistoryAdapter = TracksAdapter(tracksHistory, true)
    lateinit var playerIntent: Intent
//    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        playerIntent = Intent(this, PlayerActivity::class.java)
        val sharedPreferences = getSharedPreferences(PM_SHARED_PREFERENCES, MODE_PRIVATE)

        inputEditText = findViewById<EditText>(R.id.search_et)
        val btnClear = findViewById<ImageView>(R.id.search_btn_clear)
        val btnBack = findViewById<Toolbar>(R.id.search_tb_back)
        val btnReload = findViewById<Button>(R.id.search_btn_reload)
        val recView = findViewById<RecyclerView>(R.id.search_recView)
        val btnClearHistory = findViewById<Button>(R.id.search_btn_clear_history)
        val recHistory = findViewById<RecyclerView>(R.id.search_history_recView)

        tracksInteractor = Creator.provideTracksInteractor()
        tracksHistoryInteractor = Creator.provideTracksHistoryInteractor(sharedPreferences)

        recView.adapter = tracksAdapter
        recHistory.adapter = searchHistoryAdapter

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (inputText.isNullOrEmpty() && inputEditText.hasFocus())
                tracksHistoryInteractor.loadTracks(TracksConsumerHistory())
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) btnClear.visibility = View.INVISIBLE
                else btnClear.visibility = View.VISIBLE
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    inputText = s.toString()
                    Log.d(TAG, "inputText $inputText")
                } else {
                    inputText = ""
                }
                if (inputText.isNullOrEmpty() && inputEditText.hasFocus()) {
                    tracksHistoryInteractor.loadTracks(TracksConsumerHistory())
                } else {
                    showTracks()
                }
            }
        }

        btnClear.setOnClickListener {
            inputEditText.setText("")
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        btnClearHistory.setOnClickListener{
            tracksHistoryInteractor.clearTracks(TracksConsumerHistory())
            showTracks()
        }

        btnBack.setOnClickListener {
            finish()
        }

        btnReload.setOnClickListener {
            loadTracks(lastQuery)
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loadTracks(inputText)
            }
            false
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT, inputText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle){
        super.onRestoreInstanceState(savedInstanceState)
        inputText = savedInstanceState.getString(INPUT_TEXT, "")
        loadTracks(inputText)
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true}, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun loadTracks(searchString : String) {
        handler.removeCallbacks(searchRunnable)
        showProgressBar()
        tracksInteractor.loadTracks(searchString, TracksConsumerMain())
    }

    private fun searchTracks() {
        if (inputEditText.text.isNotEmpty()) {
            loadTracks(inputEditText.text.toString())
        }
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
                        tracksHistoryInteractor.addTrack(items[position],
                            { loadedTracks -> {
                                tracksHistory.clear()
                                tracksHistory.addAll(loadedTracks)
                                searchHistoryAdapter.notifyDataSetChanged()}})
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
            playerIntent.putExtra("track", json);
            startActivity(playerIntent)
        }
    }

    inner class TracksConsumerMain : TracksConsumer {
        override fun consume(loadedTracks: List<Track>) {
            if (loadedTracks.size > 0) handler.post{showTracks()}
            else handler.post{showNothingFound()}
            tracks.clear()
            tracks.addAll(loadedTracks)
            handler.post{tracksAdapter.notifyDataSetChanged()}
            Log.d(TAG, "loadedTracks.size ${loadedTracks.size}")
        }
    }

    inner class TracksConsumerHistory : TracksConsumer {
        override fun consume(loadedTracks: List<Track>) {
            tracksHistory.clear()
            tracksHistory.addAll(loadedTracks)
            searchHistoryAdapter.notifyDataSetChanged()
//            tracksHistoryInteractor.loadTracks(TracksConsumerHistory())
            if (loadedTracks.size > 0) {
                handler.post{showHistory()}
            } else {
                showTracks()
            }
            Log.d(TAG, "loadedTracksHistory.size ${loadedTracks.size}")
        }
    }

    companion object {
        const val INPUT_TEXT = "INPUT_TEXT"
        const val TAG = "myLog"
//        const val PM_SHARED_PREFERENCES = "PM_SHARED_PREFERENCES"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L}
}

