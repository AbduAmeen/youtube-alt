package com.github.libretube.channel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.github.libretube.data.network.ApiClient
import com.github.libretube.data.network.obj.Channel
import com.github.libretube.feed.FeedViewModel
import com.github.libretube.feed.Video
import com.github.libretube.util.formatShort
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelViewModel @Inject constructor(
    private val apiClient: ApiClient,
    savedStateHandle: SavedStateHandle
) : FeedViewModel() {

    private val id: String = savedStateHandle["id"]!!
    private val _videos = MutableLiveData<List<Video>?>()
    val videos: LiveData<List<Video>?> = _videos

    private val _nextPageUrl = MutableLiveData("")
    val nextPageUrl: LiveData<String> = _nextPageUrl

    private val _channelImg = MutableLiveData("")
    val channelImg: LiveData<String> = _channelImg

    private val _channelBanner = MutableLiveData("")
    val channelBanner: LiveData<String> = _channelBanner

    private val _channelDescription = MutableLiveData("")
    val channelDescription: LiveData<String> = _channelDescription

    private val _subscribers = MutableLiveData("-1")
    val subscribers: LiveData<String> = _subscribers

    private val _channelName = MutableLiveData("")
    val channelName: LiveData<String> = _channelName

    init {
        viewModelScope.launch {
            apiClient.fetchChannel(id)?.let {
                populateFields(it)
            }
        }
    }

    private fun populateFields(channel: Channel) {
        with(channel) {
            _channelImg.value = avatarUrl
            _channelBanner.value = bannerUrl
            _channelDescription.value = description
            _subscribers.value = subscriberCount.formatShort()
            _videos.value = relatedStreams?.map {
                val video = Video(it)
                video.description = "${it.views?.formatShort()} views • ${it.uploadedDate}"
                video.compact = true
                video
            }
            _nextPageUrl.value = nextpage
            _channelName.value = name
        }
    }
}
