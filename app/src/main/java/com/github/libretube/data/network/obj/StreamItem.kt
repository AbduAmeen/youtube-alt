package com.github.libretube.data.network.obj

data class StreamItem(
    var url: String? = "",
    var title: String? = "",
    var thumbnail: String? = "",
    var uploaderName: String? = "",
    var uploaderUrl: String? = "",
    var uploaderAvatar: String? = "",
    var uploadedDate: String? = "",
    var duration: Long? = 0,
    var views: Long? = 0,
    var uploaderVerified: Boolean? = false,
    var uploaded: Long? = 0,
    var shortDescription: String? = ""
)
