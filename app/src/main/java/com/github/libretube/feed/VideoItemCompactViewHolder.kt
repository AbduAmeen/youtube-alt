package com.github.libretube.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.libretube.databinding.VideoFeeditemCompactBinding

class VideoItemCompactViewHolder(val binding: VideoFeeditemCompactBinding) : FeedItemViewHolder(binding.root) {
    override fun bind(viewModel: FeedViewModel, item: FeedItem) {
        binding.video = item as Video
        binding.viewmodel = viewModel
    }

    companion object {
        fun from(parent: ViewGroup): VideoItemCompactViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = VideoFeeditemCompactBinding.inflate(layoutInflater, parent, false)

            return VideoItemCompactViewHolder(binding)
        }
    }
}
