package com.github.libretube.home

import android.content.SharedPreferences
import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {
    private val _videoUrl = MutableLiveData("")
    val videoUrl: LiveData<String> = _videoUrl

    private val _orientationMode = MutableLiveData<Int>()
    val orientationMode: LiveData<Int> = _orientationMode

    private val mutableChannelClickedEvent = MutableLiveData<String>()
    val channelClickedEvent: LiveData<String> = mutableChannelClickedEvent

    lateinit var preferences: SharedPreferences
    lateinit var resources: Resources

    fun setOrientationMode(mode: Int) {
        _orientationMode.value = mode
    }

    fun setCurrentPlayingVideo(videoUrl: String) {
        _videoUrl.value = videoUrl
    }

    fun setChannel(id: String?) {
        mutableChannelClickedEvent.value = id?.replace("/channel/", "") ?: ""
    }
}
