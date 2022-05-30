package com.github.libretube.data.types

enum class SearchFilters(private val value: String) {
    ALL("all"),
    VIDEOS("videos"),
    CHANNELS("channels"),
    PLAYLISTS("playlists");

    override fun toString(): String {
        return value
    }

    companion object {
        fun get(quality: String): VideoQuality? = VideoQuality.values().find { it.value == quality }
    }
}
