package com.github.libretube.player

import androidx.lifecycle.SavedStateHandle
import com.github.libretube.ViewModelTest
import com.github.libretube.data.network.PipedApiClient
import com.github.libretube.data.network.obj.Streams
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PlayerViewModelTest : ViewModelTest() {
    @MockK
    private lateinit var apiClient: PipedApiClient
    @MockK
    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewModel: PlayerViewModel

    @Before
    fun setup() {
        coEvery { apiClient.fetchStreams(any()) } returns Streams()
        every { savedStateHandle.get<String>(eq("videoUrl")) } returns "url"
        viewModel = PlayerViewModel(apiClient, savedStateHandle)
    }
    @Test
    fun pause() {
        val expectedValue = !viewModel.paused.value!!

        viewModel.pause()

        assertEquals(expectedValue, viewModel.paused.value)
    }
}
