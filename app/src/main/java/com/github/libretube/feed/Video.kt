package com.github.libretube.feed

import android.text.format.DateUtils
import com.github.libretube.data.network.obj.SearchItem
import com.github.libretube.data.network.obj.StreamItem
import com.github.libretube.util.formatShort

class Video() : FeedItem() {
    constructor(item: StreamItem) : this() {
        item.let {
            url = it.url!!
            title = it.title
            duration = it.duration?.let { duration -> DateUtils.formatElapsedTime(duration).replace("^0+(?!$)", "") }
            uploadDate = it.uploadedDate
            thumbnailUrl = it.thumbnail
            views = it.views?.formatShort()
            channelImgUrl = it.uploaderAvatar
            channelName = it.uploaderName
            channelUrl = it.uploaderUrl
        }
    }
    constructor(item: SearchItem) : this() {
        item.let {
            url = it.url!!
            title = it.title
            duration = it.duration?.let { duration -> DateUtils.formatElapsedTime(duration).replace("^0+(?!$)", "") }
            uploadDate = it.uploadedDate
            thumbnailUrl = it.thumbnail
            views = it.views?.formatShort()
            channelImgUrl = it.uploaderAvatar
            channelName = it.uploaderName
            channelUrl = it.uploaderUrl
        }
    }
    override lateinit var url: String

    var title: String? = null
    var description: String? = null
    var views: String? = null
    var duration: String? = null
    var uploadDate: String? = null
    var thumbnailUrl: String? = null
    var channelName: String? = null
    var channelImgUrl: String? = null
    var channelUrl: String? = null
    var compact: Boolean = false
}
