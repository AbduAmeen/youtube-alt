package com.github.libretube.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.libretube.databinding.VideoFeeditemBinding

class VideoItemViewHolder(val binding: VideoFeeditemBinding) : FeedItemViewHolder(binding.root) {

    override fun bind(viewModel: FeedViewModel, item: FeedItem) {
        binding.video = item as Video
        binding.viewmodel = viewModel
    }

    companion object {
        fun from(parent: ViewGroup): VideoItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = VideoFeeditemBinding.inflate(layoutInflater, parent, false)

            return VideoItemViewHolder(binding)
        }
    }
}
