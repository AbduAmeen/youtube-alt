package com.github.libretube.feed

abstract class FeedItem : Any() {
    abstract var url: String

    override fun equals(other: Any?): Boolean {
        return url == (other as? FeedItem)?.url
    }
}
