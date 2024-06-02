package com.example.playlist_maker.App


import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlist_maker.di.dataModel
import com.example.playlist_maker.di.interactorModule
import com.example.playlist_maker.di.repositoryModule
import com.example.playlist_maker.di.viewModelModule
import com.example.playlist_maker.settings.domain.SettingsInteractor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {


    var darkTheme = false


    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    dataModel,
                    repositoryModule,
                    interactorModule,
                    viewModelModule
                )
            )
        }

       val  settingsInteractor : SettingsInteractor by inject()
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
