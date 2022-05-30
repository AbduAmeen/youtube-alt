package com.github.libretube

import android.content.Context
import android.content.res.Resources
import com.github.libretube.data.network.ApiClient
import com.github.libretube.data.network.PipedApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class MainApplicationModule {
    @Provides
    @Singleton
    fun providePipedApiClient(@ApplicationContext context: Context): ApiClient {
        return PipedApiClient(context)
    }

    @Provides
    @Singleton
    fun provideResources(@ApplicationContext context: Context): Resources {
        return context.resources
    }
}
