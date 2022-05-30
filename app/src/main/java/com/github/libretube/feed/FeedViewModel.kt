package com.github.libretube.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class FeedViewModel : ViewModel() {

    // TODO: Set data as event
    protected val mutableVideoClickedEvent = MutableLiveData<Video>()
    val videoClickedEvent: LiveData<Video> = mutableVideoClickedEvent

    protected val mutableChannelClickedEvent = MutableLiveData<String>()
    val channelClickedEvent: LiveData<String> = mutableChannelClickedEvent

    protected val mutablePlaylistClickedEvent = MutableLiveData<String>()
    val playlistClickedEvent: LiveData<String> = mutablePlaylistClickedEvent

    open fun setVideo(video: Video?) {
        mutableVideoClickedEvent.value = video
    }

    open fun setChannel(id: String?) {
        mutableChannelClickedEvent.value = id?.replace("/channel/", "") ?: ""
    }

    open fun setPlaylist(id: String?) {
        mutablePlaylistClickedEvent.value = id?.replace("/playlist?list=", "") ?: ""
    }
}
