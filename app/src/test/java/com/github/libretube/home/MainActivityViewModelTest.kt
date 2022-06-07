package com.github.libretube.home

import android.content.res.Configuration
import com.github.libretube.ViewModelTest
import org.junit.Assert.assertEquals
import org.junit.Test

class MainActivityViewModelTest : ViewModelTest() {

    val viewModel = MainActivityViewModel()

    @Test
    fun setOrientationMode() {
        val expectedValue = Configuration.ORIENTATION_PORTRAIT

        viewModel.setOrientationMode(expectedValue)

        assertEquals(expectedValue, viewModel.orientationMode.value)
    }

    @Test
    fun setCurrentPlayingVideo() {
        val expectedValue = "url"

        viewModel.setCurrentPlayingVideo(expectedValue)

        assertEquals(expectedValue, viewModel.videoUrl.value)
    }

    @Test
    fun setChannel() {
        val expectedValue = "url"

        viewModel.setChannel(expectedValue)

        assertEquals(expectedValue, viewModel.channelClickedEvent.value)
    }
}
