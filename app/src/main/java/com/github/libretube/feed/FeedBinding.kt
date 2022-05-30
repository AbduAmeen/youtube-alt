package com.github.libretube.feed

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

@BindingAdapter("app:imageUrl")
fun setImageUrl(view: ImageView, url: String?) = view.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
    if (!url.isNullOrEmpty()) {
        Picasso.get().load(url).into(view)
    }
}

@BindingAdapter("app:feed")
fun setFeed(view: RecyclerView, feed: List<FeedItem>?) {
    if (!feed.isNullOrEmpty()) {
        (view.adapter as FeedAdapter).submitList(feed)
    }
}

@BindingAdapter("app:itemlimit")
fun setItemLimit(view: RecyclerView, limit: Int?) {
    val adapter = (view.adapter as FeedAdapter)
    if (limit != null) {
        adapter.itemLimit = limit
        adapter.notifyDataSetChanged()
    } else {
        adapter.itemLimit = Int.MAX_VALUE
    }
}
