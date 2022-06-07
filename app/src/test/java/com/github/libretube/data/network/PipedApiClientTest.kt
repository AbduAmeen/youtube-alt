package com.github.libretube.data.network

import android.content.Context
import android.content.SharedPreferences
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit

@OptIn(ExperimentalCoroutinesApi::class)
class PipedApiClientTest {

    @get:Rule
    val mockkRule = MockKRule(this)
    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var preferences: SharedPreferences

    @MockK
    private lateinit var retrofit: Retrofit

    @RelaxedMockK
    private lateinit var definition: PipedApiDefinition

    private lateinit var pipedClient: PipedApiClient

    @Before
    fun setup() {
        every { preferences.getString(any(), any()) } returns "FAKE STRING"
        every { retrofit.create(PipedApiDefinition::class.java) } returns definition

        pipedClient = PipedApiClient(context, preferences, retrofit)
    }

    @Test
    fun fetchTrending() = runTest {
        val result = pipedClient.fetchTrending()

        assertNotNull(result)
    }

    @Test
    fun fetchChannel() = runTest {
        val result = pipedClient.fetchChannel("id")

        assertNotNull(result)
    }

    @Test
    fun fetchStreams() = runTest {
        val result = pipedClient.fetchStreams("id")

        assertNotNull(result)
    }

    @Test
    fun fetchPlaylist() = runTest {
        val result = pipedClient.fetchPlaylist("id")

        assertNotNull(result)
    }

    @Test
    fun fetchSearch() = runTest {
        val result = pipedClient.fetchSearch("id")

        assertNotNull(result)
    }

    @Test
    fun fetchSuggestions() = runTest {
        val result = pipedClient.fetchSuggestions("id")

        assertNotNull(result)
    }

    @Test
    fun isSubscribed() = runTest {
        val result = pipedClient.isSubscribed("id")

        assertNotNull(result)
    }

    @Test
    fun fetchInstances() = runTest {
        val result = pipedClient.fetchInstances()

        assertNotNull(result)
    }

    @Test
    fun subscribe() = runTest {
        val result = pipedClient.subscribe("id")

        assertNotNull(result)
    }

    @Test
    fun login() = runTest {
        val result = pipedClient.login("username", "password")

        assertNotNull(result)
    }

    @Test
    fun register() = runTest {
        val result = pipedClient.register("username", "password")

        assertNotNull(result)
    }
    @Test
    fun fetchNextPage() = runTest {
        val result = pipedClient.fetchNextPage("id", "nextPage")

        assertNotNull(result)
    }

    @Test
    fun unsubscribe() = runTest {
        val result = pipedClient.unsubscribe("id")

        assertNotNull(result)
    }

    @Test
    fun fetchSubscriptionFeed() = runTest {
        val result = pipedClient.fetchSubscriptionFeed("token")

        assertNotNull(result)
    }

    @Test
    fun fetchSubscriptions() = runTest {
        val result = pipedClient.fetchSubscriptions("token")

        assertNotNull(result)
    }

    @Test
    fun getToken() = runTest {
        val result = pipedClient.getToken()

        assertNotNull(result)
    }
}
