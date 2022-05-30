package com.github.libretube.feed

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class FeedItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(viewModel: FeedViewModel, item: FeedItem)
}
