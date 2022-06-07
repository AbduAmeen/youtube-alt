package com.github.libretube.search

import com.github.libretube.ViewModelTest
import com.github.libretube.data.network.PipedApiClient
import com.github.libretube.data.network.obj.SearchResult
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class SearchViewModelTest : ViewModelTest() {
    @MockK
    private lateinit var apiClient: PipedApiClient

    lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        coEvery { apiClient.fetchSearch(any(), any()) } returns SearchResult(listOf())
        coEvery { apiClient.fetchSuggestions(any()) } returns listOf("test")
        viewModel = SearchViewModel(apiClient)
    }
    @Test
    fun searchTermChanged() {
        viewModel.searchTermChanged("test")

        assertEquals("test", viewModel.searchTerm.value)
        assertFalse(viewModel.suggestions.value.isNullOrEmpty())
    }

    @Test
    fun search_defaultSearchFilter_returnSearchResult() {
        viewModel.search("test")

        assertNotNull(viewModel.results.value)
    }
}
