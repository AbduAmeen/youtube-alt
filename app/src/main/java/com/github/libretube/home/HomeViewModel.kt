package com.github.libretube.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.github.libretube.data.network.ApiClient
import com.github.libretube.feed.FeedViewModel
import com.github.libretube.feed.Video
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val apiClient: ApiClient
) : FeedViewModel() {
    private val _videoFeed = MutableLiveData<List<Video>?>()
    val videoFeed: LiveData<List<Video>?> = _videoFeed

    init {
        updateFeed(true)
    }

    fun updateFeed(forced: Boolean) {
        viewModelScope.launch {
            if (forced) {
                _videoFeed.value = null
            }

            val response = apiClient.fetchTrending()

            _videoFeed.value = response?.map { Video(it) }
        }
    }
}
