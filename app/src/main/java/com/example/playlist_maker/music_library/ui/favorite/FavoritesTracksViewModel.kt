package com.example.playlist_maker.music_library.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlist_maker.music_library.domain.FavoriteTracksInteractor
import kotlinx.coroutines.launch

class FavoritesTracksViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {

    private val _favoriteState = MutableLiveData<FavoriteState>(FavoriteState.Load)

    val favoriteState: LiveData<FavoriteState> = _favoriteState

    private fun setState(favoriteState: FavoriteState) {
        _favoriteState.postValue(favoriteState)
    }

    fun getFavoriteList() {
        viewModelScope.launch {
            setState(FavoriteState.Load)
            favoriteTracksInteractor
                .getTracks()
                .collect { tracks ->
                    if (tracks.isEmpty()) {
                        setState(FavoriteState.NoEntry)
                    } else {
                        setState(FavoriteState.Content(tracks))
                    }
                }
        }
    }

}