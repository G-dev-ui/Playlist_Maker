package com.example.playlist_maker.settings.data

import com.example.playlist_maker.settings.domain.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)


}