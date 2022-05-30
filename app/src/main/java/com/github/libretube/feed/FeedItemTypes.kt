package com.github.libretube.feed

enum class FeedItemTypes(private val value: Int) {
    VIDEO(1),
    VIDEOCOMPACT(2),
    CHANNEL(3),
    PLAYLIST(4);

    fun toInt(): Int {
        return value
    }

    companion object {
        fun get(value: Int): FeedItemTypes? = values().find { it.value == value }
    }
}
