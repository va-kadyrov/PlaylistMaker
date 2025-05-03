package com.example.playlistmaker

import TrackResponse
import android.content.ContentValues.TAG
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.GregorianCalendar
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
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.sql.Time
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.time.Duration

class SearchActivity : AppCompatActivity() {

    private val retrofit = Retrofit.Builder().baseUrl(I_TUNES_URL).addConverterFactory(GsonConverterFactory.create()).build()

    private val tracksApiService = retrofit.create<TracksApiService>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val inputEditText = findViewById<EditText>(R.id.search_et)
        val btnClear = findViewById<ImageView>(R.id.search_btn_clear)
        val btnBack = findViewById<Toolbar>(R.id.search_tb_back)
        val recView = findViewById<RecyclerView>(R.id.search_recView)

        recView.adapter = tracksAdapter

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
            }
        }

        btnClear.setOnClickListener {
            inputEditText.setText("")
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }

        btnBack.setOnClickListener {
            finish()
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loadTracks()

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
        loadTracks()
    }

    fun loadTracks() {
        tracksApiService.getTracks("song", inputText).enqueue(object : Callback<TrackResponse> {
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
                    Log.e(TAG, "errorJson $errorJson")
                    showNetworkError()
                }
                tracksAdapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                t.printStackTrace()
                showNetworkError()
                Log.e(TAG, "tracks loading error: ${t.message}")
            }
        })
    }

    fun showTracks(){
        findViewById<RecyclerView>(R.id.search_recView).visibility = VISIBLE
        findViewById<LinearLayout>(R.id.search_nothing_found).visibility = GONE
        findViewById<LinearLayout>(R.id.search_network_error).visibility = GONE
    }

    fun showNothingFound(){
        findViewById<RecyclerView>(R.id.search_recView).visibility = GONE
        findViewById<LinearLayout>(R.id.search_nothing_found).visibility = VISIBLE
        findViewById<LinearLayout>(R.id.search_network_error).visibility = GONE
    }

    fun showNetworkError(){
        findViewById<RecyclerView>(R.id.search_recView).visibility = GONE
        findViewById<LinearLayout>(R.id.search_nothing_found).visibility = GONE
        findViewById<LinearLayout>(R.id.search_network_error).visibility = VISIBLE
    }

    var inputText = ""
    val tracks = mutableListOf<Track>()
    val tracksAdapter = TracksAdapter(tracks)

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

    class TracksAdapter(private val tracks: List<Track>): RecyclerView.Adapter<TracksViewHolder> () {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.search_recycler_layout, parent, false)
            return TracksViewHolder(view)
        }
        override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
            holder.bind(tracks[position])
        }
        override fun getItemCount(): Int {
            return tracks.size
        }
    }

    companion object {
        const val INPUT_TEXT = "INPUT_TEXT"
        const val TAG = "myLog"
        const val I_TUNES_URL = "https://itunes.apple.com"}
}