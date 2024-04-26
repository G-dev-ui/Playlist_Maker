package com.example.playlist_maker.settings.data

import android.content.SharedPreferences
import com.example.playlist_maker.settings.domain.ThemeSettings


const val NIGHT_THEME_KEY = "night_theme"
class SettingsRepositoryImpl(private val sharedPrefs: SharedPreferences): SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(sharedPrefs.getBoolean(NIGHT_THEME_KEY, false))
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        sharedPrefs.edit()
            .putBoolean(NIGHT_THEME_KEY, settings.darkTheme)
            .apply()
    }

}