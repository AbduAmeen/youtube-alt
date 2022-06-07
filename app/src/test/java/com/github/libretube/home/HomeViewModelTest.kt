package com.github.libretube.home

import androidx.lifecycle.SavedStateHandle
import com.github.libretube.ViewModelTest
import com.github.libretube.data.network.PipedApiClient
import com.github.libretube.data.network.obj.StreamItem
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

class HomeViewModelTest : ViewModelTest() {
    @MockK
    private lateinit var apiClient: PipedApiClient

    @MockK
    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var homeViewModel: HomeViewModel

    @Before
    fun setup() {
        coEvery { apiClient.fetchTrending() } returns listOf(StreamItem())
        homeViewModel = HomeViewModel(savedStateHandle, apiClient)
    }

    @Test
    fun updateFeed_notForcedRefresh_videoFeedIsNotEmpty() {
        homeViewModel.updateFeed(false)

        assertFalse(homeViewModel.videoFeed.value.isNullOrEmpty())
    }

    @Test
    fun updateFeed_forcedRefresh_videoFeedIsNotEmpty() {
        homeViewModel.updateFeed(true)

        assertFalse(homeViewModel.videoFeed.value.isNullOrEmpty())
    }
}
