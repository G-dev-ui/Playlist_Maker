package com.example.playlist_maker

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    var darkTheme = false
    private val PREFS_NAME = "theme_prefs"
    private val THEME_KEY = "dark_theme"

    override fun onCreate() {
        super.onCreate()
        val preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        darkTheme = preferences.getBoolean(THEME_KEY, false)
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled


        val editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(THEME_KEY, darkTheme)
        editor.apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
