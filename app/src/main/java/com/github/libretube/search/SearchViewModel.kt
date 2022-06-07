package com.github.libretube.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.libretube.data.network.ApiClient
import com.github.libretube.data.network.obj.SearchItem
import com.github.libretube.data.types.SearchFilters
import com.github.libretube.feed.Channel
import com.github.libretube.feed.FeedItem
import com.github.libretube.feed.FeedViewModel
import com.github.libretube.feed.Playlist
import com.github.libretube.feed.Video
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val apiClient: ApiClient
) : FeedViewModel() {
    private val _searchTerm = MutableLiveData("")
    var searchTerm: LiveData<String> = _searchTerm

    private val _suggestions: MutableLiveData<List<String>> = MutableLiveData(listOf())
    val suggestions: LiveData<List<String>> = _suggestions

    private val _results: MutableLiveData<List<FeedItem>?> = MutableLiveData(listOf())
    val results: LiveData<List<FeedItem>?> = _results

    private val _searchEvent = MutableLiveData("")
    val searchEvent: LiveData<String> = _searchEvent

    private val _searchLoading = MutableLiveData(false)
    val searchLoading: LiveData<Boolean> = _searchLoading

    fun searchTermChanged(string: CharSequence) {
        _searchTerm.value = string.toString()

        viewModelScope.launch {
            _suggestions.value = apiClient.fetchSuggestions(_searchTerm.value!!)
        }
    }

    // TODO: Allow for different search filters
    fun search(query: String) {
        _searchTerm.value = query
        _searchLoading.value = true
        _searchEvent.value = query
        viewModelScope.launch {
            val items = apiClient.fetchSearch(_searchTerm.value!!, SearchFilters.ALL.toString())?.items
            _results.value = convertSearchItemsToFeedItems(items)
            _searchLoading.value = false
        }
    }

    private fun convertSearchItemsToFeedItems(items: List<SearchItem>?): List<FeedItem> {
        if (items.isNullOrEmpty()) {
            return emptyList()
        }

        val returnItems: List<FeedItem> = items.map {
            if (it.url!!.contains("/channel/") || it.url!!.contains("/c/")) {
                Channel(it)
            } else if (it.url!!.contains("/playlist")) {
                Playlist(it)
            } else {
                Video(it)
            }
        }

        return returnItems
    }
}
