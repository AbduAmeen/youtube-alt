package com.github.libretube.data.network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import com.github.libretube.R
import com.github.libretube.data.network.obj.Channel
import com.github.libretube.data.network.obj.Instances
import com.github.libretube.data.network.obj.Login
import com.github.libretube.data.network.obj.Playlist
import com.github.libretube.data.network.obj.SearchResult
import com.github.libretube.data.network.obj.StreamItem
import com.github.libretube.data.network.obj.Streams
import com.github.libretube.data.network.obj.Subscribe
import com.github.libretube.data.network.obj.Subscription
import com.github.libretube.data.network.obj.Token
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PipedApiClient @Inject constructor(
    context: Context
) : ApiClient {

    private val TAG: String = "PipedApiClient"
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val url = preferences.getString("instance", context.getString(R.string.default_api_instance))!!
    private val apiClient: PipedApiDefinition = with(Retrofit.Builder()) {
        baseUrl(url)

        addConverterFactory(JacksonConverterFactory.create())
        build().create(PipedApiDefinition::class.java)
    }

    override suspend fun fetchTrending(): List<StreamItem>? {
        return try {
            apiClient.getTrending(preferences.getString("region", "US")!!)
        } catch (e: Exception) {
            logError(e)
            null
        }
    }

    override suspend fun fetchChannel(channelId: String): Channel? {
        return try {
            apiClient.getChannel(channelId)
        } catch (e: Exception) {
            logError(e)
            null
        }
    }

    override suspend fun fetchStreams(id: String): Streams? {
        return try {
            apiClient.getStreams(id)
        } catch (e: Exception) {
            logError(e)
            null
        }
    }

    override suspend fun fetchPlaylist(id: String): Playlist? {
        return try {
            apiClient.getPlaylist(id)
        } catch (e: Exception) {
            logError(e)
            null
        }
    }

    override suspend fun fetchSearch(query: String, filter: String): SearchResult? {
        return try {
            apiClient.getSearchResults(query, filter)
        } catch (e: Exception) {
            logError(e)
            null
        }
    }

    override suspend fun fetchSuggestions(query: String): List<String>? {
        return try {
            apiClient.getSuggestions(query)
        } catch (e: Exception) {
            logError(e)
            null
        }
    }
    override suspend fun isSubscribed(channelId: String): Boolean? {
        return try {
            apiClient.isSubscribed(channelId, getToken()).subscribed
        } catch (e: Exception) {
            logError(e)
            null
        }
    }

    override suspend fun fetchInstances(): List<Instances>? {
        return try {
            // TODO: Replace this with a better solution
            apiClient.getInstances("https://instances.tokhmi.xyz/")
        } catch (e: Exception) {
            logError(e)
            null
        }
    }

    override suspend fun subscribe(channelId: String): Boolean {
        return try {
            apiClient.subscribe(getToken(), Subscribe(channelId))
            true
        } catch (e: Exception) {
            logError(e)
            false
        }
    }

    override suspend fun login(username: String, password: String): Result<Token> {
        val token: Token
        try {
            token = apiClient.login(Login(username, password))
        } catch (e: Exception) {
            logError(e)
            return Result.failure(e)
        }
        return Result.success(token)
    }

    override suspend fun register(username: String, password: String): Result<Token> {
        val token: Token
        try {
            token = apiClient.register(Login(username, password))
        } catch (e: Exception) {
            logError(e)
            return Result.failure(e)
        }

        return Result.success(token)
    }

    override suspend fun fetchNextPage(resourceId: String, nextPage: String): Channel? {
        return try {
            apiClient.getChannelNextPage(resourceId, nextPage)
        } catch (e: Exception) {
            logError(e)
            null
        }
    }

    override suspend fun unsubscribe(channelId: String): Boolean {
        return try {
            apiClient.unsubscribe(getToken(), Subscribe(channelId))
            true
        } catch (e: Exception) {
            logError(e)
            false
        }
    }

    override suspend fun fetchSubscriptionFeed(token: String): List<StreamItem>? {
        return try {
            apiClient.getFeed(token)
        } catch (e: Exception) {
            logError(e)
            null
        }
    }

    override suspend fun fetchSubscriptions(token: String): List<Subscription>? {
        return try {
            apiClient.subscriptions(token)
        } catch (e: Exception) {
            logError(e)
            null
        }
    }

    private fun logError(e: Exception) {
        when (e) {
            is IOException -> Log.e(TAG, "IOException, you might not have internet connection", e)
            is HttpException -> Log.e(TAG, "HttpException, unexpected response", e)
            else -> Log.e(TAG, "Exception", e)
        }
    }

    override fun getToken(): String {
        return preferences.getString("token", "")!!
    }
}
