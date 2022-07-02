package com.github.libretube.data.types

enum class VideoQuality(val value: String) {
    V_144p("144p"),
    V_240p("240p"),
    V_360p("360p"),
    V_480p("480p"),
    V_720p("720p"),
    V_1080p("1080p"),
    V_1440p("1440p"),
    V_2160p("2160p"),
    V_4320p("4320p"),
    AUTO("Auto");

    override fun toString(): String {
        return value
    }

    companion object {
        fun get(quality: String): VideoQuality? = values().find { it.value == quality }
        fun getFromHeight(height: Int): VideoQuality? = when (height) {
            144 -> V_144p
            240 -> V_240p
            360 -> V_360p
            480 -> V_480p
            720 -> V_720p
            1080 -> V_1080p
            1440 -> V_1440p
            2160 -> V_2160p
            4320 -> V_4320p
            else -> null
        }
    }
}
