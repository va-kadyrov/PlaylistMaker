package com.example.playlistmaker.main.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

class MainViewModel(private val context: Context): ViewModel() {

    companion object {
        fun getFactory(context: Context): ViewModelProvider.Factory = viewModelFactory{
            initializer {
                MainViewModel(context)
            }
        }
    }
}