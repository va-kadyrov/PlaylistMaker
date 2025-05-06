package com.example.playlistmaker

import TrackResponse
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.Date


class SearchActivity : AppCompatActivity() {

    private val retrofit = Retrofit.Builder().baseUrl(I_TUNES_URL).addConverterFactory(GsonConverterFactory.create()).build()

    private val tracksApiService = retrofit.create<TracksApiService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val inputEditText = findViewById<EditText>(R.id.search_et)
        val btnClear = findViewById<ImageView>(R.id.search_btn_clear)
        val btnBack = findViewById<Toolbar>(R.id.search_tb_back)
        val btnReload = findViewById<Button>(R.id.search_btn_reload)
        val recView = findViewById<RecyclerView>(R.id.search_recView)
        val btnClearHistory = findViewById<Button>(R.id.search_btn_clear_history)
        val recHistory = findViewById<RecyclerView>(R.id.search_history_recView)
        val sharedPreference = getSharedPreferences(PM_SHARED_PREFERENCES, MODE_PRIVATE)

        recView.adapter = tracksAdapter

        searchHistory.sharedPreference = sharedPreference
        searchHistory.loadTracks()
        recHistory.adapter = searchHistoryAdapter
        searchHistoryAdapter.notifyDataSetChanged()

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (inputText.isNullOrEmpty() && inputEditText.hasFocus() && searchHistory.tracks.size > 0) showHistory()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) btnClear.visibility = View.INVISIBLE
                else btnClear.visibility = View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    inputText = s.toString()
                    Log.d(TAG, "inputText $inputText")
                } else {
                    inputText = ""
                }
                if (inputText.isNullOrEmpty() && inputEditText.hasFocus() && searchHistory.tracks.size > 0) {
                    showHistory()
                    searchHistoryAdapter.notifyDataSetChanged()
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
            searchHistory.clearTracks()
            searchHistoryAdapter.notifyDataSetChanged()
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

    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_TEXT, inputText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle){
        super.onRestoreInstanceState(savedInstanceState)
        inputText = savedInstanceState.getString(INPUT_TEXT, "")
        loadTracks(inputText)
    }

    fun loadTracks(searchString: String) {
        tracksApiService.getTracks("song", searchString).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(
                call: Call<TrackResponse>,
                response: Response<TrackResponse>
            ) {
                Log.d(TAG, "response.isSuccessful ${response.isSuccessful}")
                if (response.isSuccessful) {
                    tracks.clear()
                    tracks.addAll(response.body()!!.results)
                    if (tracks.size > 0) showTracks()
                    else showNothingFound()
                    Log.d(TAG, "response.body()!!.resultCount ${response.body()!!.resultCount}")
                } else {
                    val errorJson = response.errorBody()?.string()
                    tracks.clear()
                    lastQuery = searchString
                    Log.e(TAG, "errorJson $errorJson")
                    showNetworkError()
                }
                tracksAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                t.printStackTrace()
                tracks.clear()
                lastQuery = searchString
                Log.e(TAG, "tracks loading error: ${t.message}")
                showNetworkError()
            }
        })
    }

    fun showTracks(){
        findViewById<RecyclerView>(R.id.search_recView).visibility = VISIBLE
        findViewById<LinearLayout>(R.id.search_history).visibility = GONE
        findViewById<LinearLayout>(R.id.search_nothing_found).visibility = GONE
        findViewById<LinearLayout>(R.id.search_network_error).visibility = GONE
    }

    fun showHistory(){
        findViewById<RecyclerView>(R.id.search_recView).visibility = GONE
        findViewById<LinearLayout>(R.id.search_history).visibility = VISIBLE
        findViewById<LinearLayout>(R.id.search_nothing_found).visibility = GONE
        findViewById<LinearLayout>(R.id.search_network_error).visibility = GONE
    }

    fun showNothingFound(){
        findViewById<RecyclerView>(R.id.search_recView).visibility = GONE
        findViewById<LinearLayout>(R.id.search_history).visibility = GONE
        findViewById<LinearLayout>(R.id.search_nothing_found).visibility = VISIBLE
        findViewById<LinearLayout>(R.id.search_network_error).visibility = GONE
    }

    fun showNetworkError(){
        findViewById<RecyclerView>(R.id.search_recView).visibility = GONE
        findViewById<LinearLayout>(R.id.search_history).visibility = GONE
        findViewById<LinearLayout>(R.id.search_nothing_found).visibility = GONE
        findViewById<LinearLayout>(R.id.search_network_error).visibility = VISIBLE
    }

    var inputText = ""
    var lastQuery = ""
    val tracks = mutableListOf<Track>()
    val searchHistory = SearchHistory()
    val tracksAdapter = TracksAdapter(tracks)
    val searchHistoryAdapter = TracksAdapter(searchHistory.tracks)

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
            holder.itemView.setOnClickListener{
                searchHistory.addTrack(items[position])
            }

        }
        override fun getItemCount(): Int {
            return items.size
        }
    }

    companion object {
        const val INPUT_TEXT = "INPUT_TEXT"
        const val TAG = "myLog"
        const val PM_SHARED_PREFERENCES = "PM_SHARED_PREFERENCES"
        const val I_TUNES_URL = "https://itunes.apple.com"}
}