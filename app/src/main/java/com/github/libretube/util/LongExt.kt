package com.github.libretube.util

import android.icu.number.Notation
import android.icu.number.NumberFormatter
import android.os.Build
import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.ln
import kotlin.math.pow

fun Long.formatShort(): String {

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        NumberFormatter
            .withLocale(Locale.getDefault())
            .notation(Notation.compactShort())
            .format(this)
            .toString()
    } else {
        if (this < 1000) return "" + this
        val exp = (ln(this.toDouble()) / ln(1000.0)).toInt()
        val format = DecimalFormat("0.#")
        val value: String = format.format(this / 1000.0.pow(exp.toDouble()))
        String.format("%s%c", value, "kMBTPE"[exp - 1])
    }
}
