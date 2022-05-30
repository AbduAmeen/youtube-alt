package com.github.libretube.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.libretube.databinding.ChannelFeeditemBinding

class ChannelItemViewHolder(val binding: ChannelFeeditemBinding) : FeedItemViewHolder(binding.root) {
    override fun bind(viewModel: FeedViewModel, item: FeedItem) {
        binding.channel = item as Channel
        binding.viewmodel = viewModel
    }

    companion object {
        fun from(parent: ViewGroup): ChannelItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ChannelFeeditemBinding.inflate(layoutInflater, parent, false)

            return ChannelItemViewHolder(binding)
        }
    }
}
