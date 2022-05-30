package com.github.libretube.data.network.obj

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Streams(
    val title: String? = "",
    val description: String? = "",
    val uploadDate: String? = "",
    val uploader: String? = "",
    val uploaderUrl: String? = "",
    val uploaderAvatar: String? = "",
    val thumbnailUrl: String? = "",
    val hls: String? = "",
    val dash: String? = "",
    val lbryId: String? = "",
    val uploaderVerified: Boolean? = false,
    val duration: Int? = -1,
    val views: Long? = -1L,
    val likes: Long? = -1L,
    val dislikes: Int? = -1,
    val audioStreams: List<PipedStream>? = listOf(),
    val videoStreams: List<PipedStream>? = listOf(),
    val relatedStreams: List<StreamItem>? = listOf(),
    val subtitles: List<Subtitle>? = listOf(),
    val livestream: Boolean? = false,
    val proxyUrl: String? = "",
    val chapters: List<ChapterSegment>? = listOf()
) {
//    constructor() : this(
//        "", "", "", "", "", "", "", "", "", "", null, -1, -1, -1, -1, emptyList(), emptyList(),
//        emptyList(), emptyList(), null, "", emptyList()
//    )
}
