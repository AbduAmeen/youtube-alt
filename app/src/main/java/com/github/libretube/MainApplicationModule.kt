package com.github.libretube

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.preference.PreferenceManager
import com.github.libretube.data.network.ApiClient
import com.github.libretube.data.network.PipedApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class MainApplicationModule {
    @Provides
    @Singleton
    fun providePipedApiClient(@ApplicationContext context: Context, preferences: SharedPreferences, retrofit: Retrofit): ApiClient {
        return PipedApiClient(context, preferences, retrofit)
    }

    @Provides
    @Singleton
    fun provideResources(@ApplicationContext context: Context): Resources {
        return context.resources
    }

    @Provides
    @Singleton
    fun provideRetrofitClient(@ApplicationContext context: Context, preferences: SharedPreferences): Retrofit {
        return with(Retrofit.Builder()) {
            baseUrl(preferences.getString("instance", context.getString(R.string.default_api_instance))!!)
            addConverterFactory(JacksonConverterFactory.create())
            build()
        }
    }

    @Provides
    @Singleton
    fun providePreferences(@ApplicationContext context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
}
