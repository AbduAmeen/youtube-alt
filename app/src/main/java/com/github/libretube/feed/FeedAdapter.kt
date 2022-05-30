package com.github.libretube.feed

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import kotlin.math.min

class FeedAdapter(private val viewModel: FeedViewModel) : ListAdapter<FeedItem, FeedItemViewHolder>(FeedItemDiffCallback()) {
    var itemLimit = Int.MAX_VALUE

    override fun getItemCount(): Int {
        return min(super.getItemCount(), itemLimit)
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = getItem(position)) {
            is Video -> {
                if (item.compact) {
                    FeedItemTypes.VIDEOCOMPACT.toInt()
                } else {
                    FeedItemTypes.VIDEO.toInt()
                }
            }
            is Channel -> {
                FeedItemTypes.CHANNEL.toInt()
            }
            is Playlist -> {
                FeedItemTypes.PLAYLIST.toInt()
            }
            else -> FeedItemTypes.VIDEO.toInt()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedItemViewHolder {
        return when (FeedItemTypes.get(viewType)) {
            FeedItemTypes.VIDEOCOMPACT -> VideoItemCompactViewHolder.from(parent)
            FeedItemTypes.CHANNEL -> ChannelItemViewHolder.from(parent)
            FeedItemTypes.PLAYLIST -> PlaylistItemViewHolder.from(parent)
            else -> VideoItemViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: FeedItemViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(viewModel, item)
    }
}

class FeedItemDiffCallback : DiffUtil.ItemCallback<FeedItem>() {

    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }
}
