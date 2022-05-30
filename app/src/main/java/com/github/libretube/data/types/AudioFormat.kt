package com.github.libretube.data.types

enum class AudioFormat(format: String) {
    M4A("M4A"),
    WEBMA_OPUS("WEBMA_OPUS");

    var value: String = format

    override fun toString(): String {
        return value
    }

    companion object {
        fun get(value: String): AudioFormat? = AudioFormat.values().find { it.value == value }
    }
}
