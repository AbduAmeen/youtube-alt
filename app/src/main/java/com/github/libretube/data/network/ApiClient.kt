package com.github.libretube.data.network

import com.github.libretube.data.network.obj.Channel
import com.github.libretube.data.network.obj.Instances
import com.github.libretube.data.network.obj.Playlist
import com.github.libretube.data.network.obj.SearchResult
import com.github.libretube.data.network.obj.StreamItem
import com.github.libretube.data.network.obj.Streams
import com.github.libretube.data.network.obj.Subscription
import com.github.libretube.data.network.obj.Token

interface ApiClient {
    suspend fun fetchTrending(): List<StreamItem>?

    suspend fun fetchChannel(channelId: String): Channel?

    suspend fun fetchStreams(id: String): Streams?

    suspend fun fetchPlaylist(id: String): Playlist?

    suspend fun fetchSearch(query: String, filter: String = "all"): SearchResult?

    suspend fun fetchSuggestions(query: String): List<String>?

    suspend fun isSubscribed(channelId: String): Boolean?

    suspend fun fetchInstances(): List<Instances>?

    suspend fun subscribe(channelId: String): Boolean

    suspend fun login(username: String, password: String): Result<Token>

    suspend fun register(username: String, password: String): Result<Token>

    suspend fun fetchNextPage(resourceId: String, nextPage: String): Channel?

    suspend fun unsubscribe(channelId: String): Boolean

    suspend fun fetchSubscriptionFeed(token: String): List<StreamItem>?

    suspend fun fetchSubscriptions(token: String): List<Subscription>?

    fun getToken(): String
}
