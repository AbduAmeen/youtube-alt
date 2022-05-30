package com.github.libretube.feed

import com.github.libretube.data.network.obj.SearchItem
import com.github.libretube.util.formatShort

data class Channel(override var url: String) : FeedItem() {
    var channelName: String? = null
    var channelImgUrl: String? = null
    var channelInfo: String? = null

    constructor(item: SearchItem) : this(item.url!!) {
        channelImgUrl = item.thumbnail
        channelName = item.name
        channelInfo = "${item.subscribers?.formatShort()} subscribers • ${item.videos} videos"
    }
}
