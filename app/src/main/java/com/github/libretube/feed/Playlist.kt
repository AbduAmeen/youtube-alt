package com.github.libretube.feed

import com.github.libretube.data.network.obj.SearchItem

class Playlist() : FeedItem() {

    constructor(item: SearchItem) : this() {
        item.let {
            url = it.url!!
            name = it.name
            thumbnail = it.thumbnail
            videoCount = it.videos!!.toInt()
            channel = Channel(it)
        }
    }

    override lateinit var url: String

    var name: String? = null
    var thumbnail: String? = null
    var videos: List<Video>? = null
    var videoCount: Int = -1
    var channelName: String? = null
    var channel: Channel? = null
}
