package com.github.libretube.playlist

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
class PlaylistViewModel @Inject constructor(
    private val apiClient: ApiClient,
    savedStateHandle: SavedStateHandle
) : FeedViewModel() {
    private val id: String = savedStateHandle["id"]!!

    private val _videos: MutableLiveData<List<Video>> = MutableLiveData()
    val videos: LiveData<List<Video>> = _videos

    private val _title = MutableLiveData("")
    val title: LiveData<String> = _title

    private val _info = MutableLiveData("")
    val info: LiveData<String> = _info

    private val _description = MutableLiveData("")
    val description: LiveData<String> = _description

    private val _playAllEvent = MutableLiveData(false)
    val playAllEvent: LiveData<Boolean> = _playAllEvent

    private val _shuffleEvent = MutableLiveData(false)
    val shuffleEvent: LiveData<Boolean> = _shuffleEvent

    init {
        viewModelScope.launch {
            val playlist = apiClient.fetchPlaylist(id)

            playlist?.let {
                _title.value = it.name
                _info.value = it.uploader
                _videos.value = it.relatedStreams?.map { streamItem ->
                    val video = Video(streamItem)
                    video.compact = true
                    video
                }
            }
        }
    }

    fun shuffle() {
        _shuffleEvent.value = !shuffleEvent.value!!
    }

    fun playAll() {
        _playAllEvent.value = !playAllEvent.value!!
    }
}
