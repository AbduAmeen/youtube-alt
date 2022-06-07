package com.github.libretube.playlist

import androidx.lifecycle.SavedStateHandle
import com.github.libretube.ViewModelTest
import com.github.libretube.data.network.PipedApiClient
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PlaylistViewModelTest : ViewModelTest() {
    @MockK
    private lateinit var apiClient: PipedApiClient
    @MockK
    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: PlaylistViewModel

    @Before
    fun setup() {
        coEvery { apiClient.fetchPlaylist(any()) } returns null
        every { savedStateHandle.get<String>("id") } returns "url"
        viewModel = PlaylistViewModel(apiClient, savedStateHandle)
    }

    @Test
    fun shuffle() {
        val expectedValue = !viewModel.shuffleEvent.value!!
        viewModel.shuffle()
        assertEquals(expectedValue, viewModel.shuffleEvent.value)
    }

    @Test
    fun playAll() {
        val expectedValue = !viewModel.playAllEvent.value!!
        viewModel.playAll()
        assertEquals(expectedValue, viewModel.playAllEvent.value)
    }
}
