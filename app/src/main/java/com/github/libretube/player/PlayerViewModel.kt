package com.github.libretube.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.github.libretube.data.network.ApiClient
import com.github.libretube.feed.FeedViewModel
import com.github.libretube.feed.Video
import com.github.libretube.util.formatShort
import com.google.android.exoplayer2.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val apiClient: ApiClient,
    savedStateHandle: SavedStateHandle
) : FeedViewModel() {

    private val _relatedVideos: MutableLiveData<List<Video>?> = MutableLiveData()
    val relatedVideos: LiveData<List<Video>?> = _relatedVideos

    private var _title = MutableLiveData("")
    val title: LiveData<String> = _title

    private var _description = MutableLiveData("")
    val description: LiveData<String> = _description

    private var _views: MutableLiveData<String> = MutableLiveData("")
    val views: LiveData<String> = _views

    private var _likes: MutableLiveData<String> = MutableLiveData("")
    val likes: LiveData<String> = _likes

    private var _date: MutableLiveData<String> = MutableLiveData("")
    val date: LiveData<String> = _date

    private var _isSubscribed = MutableLiveData(false)
    val isSubscribed: LiveData<Boolean> = _isSubscribed

    private var _channelName: MutableLiveData<String> = MutableLiveData("")
    val channelName: LiveData<String> = _channelName

    private var _channelImg: MutableLiveData<String> = MutableLiveData("")
    val channelImg: LiveData<String> = _channelImg

    private var _channelUrl: MutableLiveData<String> = MutableLiveData()
    val channelUrl: LiveData<String> = _channelUrl

    private val _paused = MutableLiveData(false)
    val paused: LiveData<Boolean> = _paused

    private val _streamUrl = MutableLiveData<String>()
    val streamUrl: LiveData<String> = _streamUrl

    private val _videoUrl: MutableLiveData<String> = MutableLiveData(savedStateHandle["videoUrl"]!!)
    val videoUrl: LiveData<String> = _videoUrl

    var exoPlayer: ExoPlayer? = null

    init {
        viewModelScope.launch {
            val videoId = videoUrl.value!!.replace("/watch?v=", "")
            val streams = getVideoStreamsAsync(videoId).await() ?: return@launch

            _streamUrl.value = streams.dash ?: streams.hls!!

            _title.value = streams.title
            _description.value = streams.description
            _views.value = streams.views?.formatShort()

            _channelUrl.value = streams.uploaderUrl
            _channelImg.value = streams.uploaderAvatar
            _channelName.value = streams.uploader

            _date.value = streams.uploadDate
            _relatedVideos.value = streams.relatedStreams!!.map { Video(it) }
            _likes.value = streams.likes!!.formatShort()
        }
    }

    private fun getVideoStreamsAsync(videoId: String) = viewModelScope.async {
        return@async apiClient.fetchStreams(videoId)
    }

    fun pause() {
        _paused.value = !_paused.value!!
    }
}
