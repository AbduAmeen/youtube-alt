package com.github.libretube.views

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.text.getSpans

class BaseTextView : AppCompatTextView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    fun setTextFromHtml(text: String) {
        var formatted = text.replace(Regex("\\n"), "<br>")

        var spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(formatted, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(text)
        }

        var spanBuilder = SpannableStringBuilder(spanned)
        var urls = spanBuilder.getSpans(0, spanned.length, URLSpan::class.java)

        if (urls.isNullOrEmpty()) {
            formatted = formatted.replace(Regex("(http(s)?://.)?(www\\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_+.~#?&//=]*)")) {
                "<a href=\"${it.value}\">${it.value}<a>"
            }
            spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(formatted, Html.FROM_HTML_MODE_COMPACT)
            } else {
                Html.fromHtml(text)
            }
            spanBuilder = SpannableStringBuilder(spanned)
            urls = spanBuilder.getSpans(0, spanned.length, URLSpan::class.java)
        }

        urls.forEach {
            val start: Int = spanBuilder.getSpanStart(it)
            val end: Int = spanBuilder.getSpanEnd(it)
            val flags: Int = spanBuilder.getSpanFlags(it)
            val clickable: ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it.toString())!!)
                    startActivity(context, browserIntent, null)
                }
            }
            spanBuilder.setSpan(clickable, start, end, flags)
            spanBuilder.removeSpan(it.toString())
        }
        setText(spanBuilder)
        movementMethod = LinkMovementMethod.getInstance()
    }
}
