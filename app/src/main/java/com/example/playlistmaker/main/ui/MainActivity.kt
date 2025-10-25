package com.example.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.search.ui.SearchActivity
import com.example.playlistmaker.settings.ui.SettingsActivity
import com.example.playlistmaker.media.ui.MediaActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSearch = findViewById<Button>(R.id.main_btn_search)
        val btnMedia = findViewById<Button>(R.id.main_btn_media)
        val btnSettings = findViewById<Button>(R.id.main_btn_settings)

        btnSearch.setOnClickListener {
            val searchIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchIntent)
        }

        btnMedia.setOnClickListener{
            val mediaIntent = Intent(this, MediaActivity::class.java)
            startActivity(mediaIntent)
        }

        btnSettings.setOnClickListener{
            val settingActivity = Intent(this, SettingsActivity::class.java)
            startActivity(settingActivity)
        }
    }
}