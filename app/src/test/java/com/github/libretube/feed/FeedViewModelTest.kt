package com.github.libretube.feed

import com.github.libretube.ViewModelTest
import org.junit.Assert.assertEquals
import org.junit.Test

class FeedViewModelTest : ViewModelTest() {
    val viewModel = FeedViewModel()

    @Test
    fun setVideo() {
        val video = Video()
        video.url = "url"
        viewModel.setVideo(video)

        assertEquals(video, viewModel.videoClickedEvent.value)
    }

    @Test
    fun setChannel() {
        val id = "id"
        viewModel.setChannel(id)

        assertEquals(id, viewModel.channelClickedEvent.value)
    }

    @Test
    fun setPlaylist() {
        val id = "id"
        viewModel.setPlaylist(id)

        assertEquals(id, viewModel.playlistClickedEvent.value)
    }
}
