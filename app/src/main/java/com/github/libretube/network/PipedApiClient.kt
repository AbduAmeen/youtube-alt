package com.github.libretube.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import com.github.libretube.obj.Channel
import com.github.libretube.obj.Instances
import com.github.libretube.obj.Login
import com.github.libretube.obj.Playlist
import com.github.libretube.obj.SearchResult
import com.github.libretube.obj.StreamItem
import com.github.libretube.obj.Streams
import com.github.libretube.obj.Subscribe
import com.github.libretube.obj.Subscription
import com.github.libretube.obj.Token
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

class PipedApiClient private constructor(
    context: Context,
    private var tag: String = "NetworkClient"
) {
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val api: PipedApi = RetrofitInstance.api

    suspend fun fetchTrending(): List<StreamItem>? {
        return try {
            api.getTrending(preferences.getString("region", "US")!!)
        } catch (e: Exception) {
            logError(e)
            null
        }
    }

    suspend fun fetchChannel(channelId: String): Channel? {
        return try {
            api.getChannel(channelId)
        } catch (e: Exception) {
            logError(e)
            null
        }
    }
    suspend fun fetchStreams(id: String): Streams? {
        return try {
            api.getStreams(id)
        } catch (e: Exception) {
            logError(e)
            null
        }
    }

    suspend fun fetchPlaylist(id: String): Playlist? {
        return try {
            RetrofitInstance.api.getPlaylist(id)
        } catch (e: Exception) {
            logError(e)
            null
        }
    }

    suspend fun fetchSearch(query: String, filter: String = "all"): SearchResult? {
        return try {
            RetrofitInstance.api.getSearchResults(query, filter)
        } catch (e: Exception) {
            logError(e)
            null
        }
    }

    suspend fun fetchSuggestions(query: String): List<String>? {
        return try {
            api.getSuggestions(query)
        } catch (e: Exception) {
            logError(e)
            null
        }
    }
    suspend fun isSubscribed(channelId: String): Boolean? {
        return try {
            RetrofitInstance.api.isSubscribed(channelId, getToken()).subscribed
        } catch (e: Exception) {
            logError(e)
            null
        }
    }

    suspend fun fetchInstances(): List<Instances>? {
        return try {
            api.getInstances("https://instances.tokhmi.xyz/")
        } catch (e: Exception) {
            logError(e)
            null
        }
    }

    suspend fun subscribe(channelId: String): Boolean {
        return try {
            api.subscribe(getToken(), Subscribe(channelId))
            true
        } catch (e: Exception) {
            logError(e)
            false
        }
    }

    suspend fun login(username: String, password: String): Result<Token> {
        val token: Token
        try {
            token = api.login(Login(username, password))
        } catch (e: Exception) {
            logError(e)
            return Result.failure(e)
        }
        return Result.success(token)
    }

    suspend fun register(username: String, password: String): Result<Token> {
        val token: Token
        try {
            token = api.register(Login(username, password))
        } catch (e: Exception) {
            logError(e)
            return Result.failure(e)
        }

        return Result.success(token)
    }

    suspend fun fetchNextPage(resourceId: String, nextPage: String): Channel? {
        return try {
            api.getChannelNextPage(resourceId, nextPage)
        } catch (e: Exception) {
            logError(e)
            null
        }
    }

    suspend fun unsubscribe(channelId: String): Boolean {
        return try {
            api.unsubscribe(getToken(), Subscribe(channelId))
            true
        } catch (e: Exception) {
            logError(e)
            false
        }
    }

    suspend fun fetchSubscriptionFeed(token: String): List<StreamItem>? {
        return try {
            RetrofitInstance.api.getFeed(token)
        } catch (e: Exception) {
            logError(e)
            null
        }
    }

    suspend fun fetchSubscriptions(token: String): List<Subscription>? {
        return try {
            RetrofitInstance.api.subscriptions(token)
        } catch (e: Exception) {
            logError(e)
            null
        }
    }

    private fun logError(e: Exception) {
        when (e) {
            is IOException -> Log.e(tag, "IOException, you might not have internet connection", e)
            is HttpException -> Log.e(tag, "HttpException, unexpected response", e)
            else -> Log.e(tag, "Exception", e)
        }
    }

    private fun getToken(): String {
        return preferences.getString("token", "")!!
    }

    companion object Factory {
        fun initialize(context: Context, tag: String = "NetworkClient"): PipedApiClient = PipedApiClient(context, tag)
    }
}
