package com.github.libretube.data.network

import com.github.libretube.ResettableLazyManager
import com.github.libretube.resettableLazy
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class RetrofitClient(
    private val url: String,
    jacksonConverterFactory: JacksonConverterFactory,
    lazyManager: ResettableLazyManager
) {
    val api: PipedApiDefinition by resettableLazy(lazyManager) {
        Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(jacksonConverterFactory)
            .build()
            .create(PipedApiDefinition::class.java)
    }
}
