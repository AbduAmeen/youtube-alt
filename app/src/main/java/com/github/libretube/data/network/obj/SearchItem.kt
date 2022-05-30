package com.github.libretube.data.network.obj

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class SearchItem(
    var url: String? = "",
    var thumbnail: String? = "",
    var uploaderName: String? = "",
    var uploaded: Long? = 0L,
    var shortDescription: String? = "",
    // Video only attributes
    var title: String? = "",
    var uploaderUrl: String? = "",
    var uploaderAvatar: String? = "",
    var uploadedDate: String? = "",
    var duration: Long? = 0L,
    var views: Long? = 0L,
    var uploaderVerified: Boolean? = null,
    // Channel and Playlist attributes
    var name: String? = null,
    var description: String? = null,
    var subscribers: Long? = -1,
    var videos: Long? = -1,
    var verified: Boolean? = null
)
