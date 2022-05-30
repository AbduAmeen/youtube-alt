package com.github.libretube.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.libretube.databinding.PlaylistFeeditemBinding

class PlaylistItemViewHolder(val binding: PlaylistFeeditemBinding) : FeedItemViewHolder(binding.root) {
    override fun bind(viewModel: FeedViewModel, item: FeedItem) {
        binding.playlist = item as Playlist
        binding.viewmodel = viewModel
    }
    companion object {
        fun from(parent: ViewGroup): PlaylistItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = PlaylistFeeditemBinding.inflate(layoutInflater, parent, false)

            return PlaylistItemViewHolder(binding)
        }
    }
}
