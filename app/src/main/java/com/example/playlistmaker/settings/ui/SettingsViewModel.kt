package com.example.playlistmaker.settings.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.R

class SettingsViewModel (private val context: Context): ViewModel() {

    private val darkThemeInteractor = Creator.provideDarkThemeInteractor(context)

    private val darkThemeState = MutableLiveData<Boolean>()
    fun observeDarkTheme(): LiveData<Boolean> = darkThemeState

    private val errorState = MutableLiveData<String>()
    fun observeErrorState(): LiveData<String> = errorState

    fun init(){
        errorState.postValue("")
    }

    fun getDarkTheme() {
        darkThemeState.postValue(darkThemeInteractor.get())
    }

    fun setDarkTheme(state: Boolean) {
        darkThemeInteractor.set(state)
        darkThemeState.postValue(state)
    }

    fun share(){
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.ref_yandex_practicum_android))
        shareIntent.type = "text/plain"
        context.startActivity(Intent.createChooser(shareIntent, null))
    }

    fun support() {
        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.data = Uri.parse("mailto:")
        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.share_email)))
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_title))
        supportIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_message))
        context.startActivity(supportIntent)
        try {
            context.startActivity(supportIntent)
        } catch (e: Exception) {
            errorState.postValue("${context.getString(R.string.err_start_activity)} отправки почты: ${e.message}")
        }
    }

    fun agreement(){
        val agreementIntent = Intent(Intent.ACTION_VIEW)
        agreementIntent.data = Uri.parse(context.getString(R.string.ref_yandex_practicum_offer))
        try{
            context.startActivity(agreementIntent)
        }catch(e: Exception){
            errorState.postValue("${context.getString(R.string.err_start_activity)} открытия веб-страницы: ${e.message}")
        }
    }

    companion object {
        fun getFactory(context: Context): ViewModelProvider.Factory = viewModelFactory{
            initializer {
                SettingsViewModel(context)
            }
        }
    }

}

