package com.github.libretube.data.network.obj

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class PipedStream(
    var url: String? = "",
    var format: String? = "",
    var quality: String? = "",
    var mimeType: String? = "",
    var codec: String? = "",
    var videoOnly: Boolean? = false,
    var bitrate: Int? = -1,
    var initStart: Int? = -1,
    var initEnd: Int? = -1,
    var indexStart: Int? = -1,
    var indexEnd: Int? = -1,
    var width: Int? = -1,
    var height: Int? = -1,
    var fps: Int? = -1
) {
//    constructor() : this("", "", "", "", "", null, -1, -1, -1, -1, -1, -1, -1, -1)
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as PipedStream
//
//        if (url != other.url) return false
//        if (format != other.format) return false
//        if (quality != other.quality) return false
//        if (mimeType != other.mimeType) return false
//        if (codec != other.codec) return false
//        if (videoOnly != other.videoOnly) return false
//        if (bitrate != other.bitrate) return false
//        if (initStart != other.initStart) return false
//        if (initEnd != other.initEnd) return false
//        if (indexStart != other.indexStart) return false
//        if (indexEnd != other.indexEnd) return false
//        if (width != other.width) return false
//        if (height != other.height) return false
//        if (fps != other.fps) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        var result = format?.hashCode() ?: 0
//        result = 31 * result + (quality?.hashCode() ?: 0)
//        return result
//    }
}
