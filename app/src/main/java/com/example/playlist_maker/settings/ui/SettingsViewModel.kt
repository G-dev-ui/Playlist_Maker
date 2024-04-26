package com.example.playlist_maker.settings.ui

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlist_maker.creator.Creator
import com.example.playlist_maker.settings.domain.SettingsInteractor
import com.example.playlist_maker.sharing.domain.SharingInteractor

class SettingsViewModel (private val sharingInteractor: SharingInteractor,
                         private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val application =
                        this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                    SettingsViewModel(
                        Creator.provideSharingInteractor(application),
                        Creator.providesettingsInteractor(application)
                    )
                }
            }
    }
    private var themeLiveData = MutableLiveData<Boolean>()

    init {
        themeLiveData.value = settingsInteractor.getThemeSettings().darkTheme
    }

    fun getThemeLiveData(): LiveData<Boolean> = themeLiveData


    fun ShareApp(){
 sharingInteractor.shareApp()
    }

    fun OpenSupport(){
sharingInteractor.openSupport()
    }

    fun OpenTerms(){
sharingInteractor.openTerms()
    }

    fun clickSwitchTheme(isChecked: Boolean) {
        Log.d("TEST", "clickSwitchTheme")
        themeLiveData.value = isChecked
        settingsInteractor.updateThemeSetting(isChecked)
        applyTheme(isChecked)
    }

    private fun applyTheme(darkThemeEnabled: Boolean) {
        Log.d("TEST", "applyTheme")
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

}
