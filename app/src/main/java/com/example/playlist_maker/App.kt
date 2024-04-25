package com.example.playlist_maker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlist_maker.creator.Creator
import com.example.playlist_maker.settings.domain.SettingsInteractor

class App : Application() {

    lateinit var settingsInteractor: SettingsInteractor
    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        settingsInteractor = Creator.providesettingsInteractor(this)
        darkTheme = settingsInteractor.getThemeSettings().darkTheme
           applyTheme(darkTheme)

    }

    private fun applyTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
               AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
