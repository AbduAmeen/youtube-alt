package com.github.libretube.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.github.libretube.R
import com.github.libretube.activities.MainActivity
import com.github.libretube.formatShort
import com.github.libretube.fragments.PlayerFragment
import com.github.libretube.obj.SearchItem
import com.squareup.picasso.Picasso

class SearchAdapter(private val searchItems: List<SearchItem>) : RecyclerView.Adapter<ListViewHolder>() {
    override fun getItemCount(): Int {
        return searchItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val layout = when (viewType) {
            0 -> R.layout.list_item_video
            1 -> R.layout.channel_search_row
            2 -> R.layout.playlist_search_row
            else -> throw IllegalArgumentException("Invalid type")
        }
        val layoutInflater = LayoutInflater.from(parent.context)
        val cell = layoutInflater.inflate(layout, parent, false)
        return ListViewHolder(cell)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(searchItems[position])
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            searchItems[position].url!!.startsWith("/watch", false) -> 0
            searchItems[position].url!!.startsWith("/channel", false) -> 1
            searchItems[position].url!!.startsWith("/playlist", false) -> 2
            else -> 3
        }
    }
}
class ListViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {

    private fun populateVideoItem(item: SearchItem) {
        val thumbnailImage = v.findViewById<ImageView>(R.id.list_item_video_thumbnail)
        val channelImage = v.findViewById<ImageView>(R.id.list_item_video_channel_img)
        val title = v.findViewById<TextView>(R.id.list_item_video_title)
        val uploadInfo = v.findViewById<TextView>(R.id.list_item_video_upload_info)

        Picasso.get().load(item.uploaderAvatar).into(channelImage)
        Picasso.get().load(item.thumbnail).into(thumbnailImage)

        title.text = item.title
        uploadInfo.text = "${item.uploaderName} • ${item.views.formatShort()} views • ${item.uploadedDate}"

        v.setOnClickListener {
            var bundle = Bundle()
            var frag = PlayerFragment()
            val activity = v.context as AppCompatActivity

            bundle.putString("videoId", item.url!!.replace("/watch?v=", ""))
            frag.arguments = bundle
            activity.supportFragmentManager.beginTransaction()
                .remove(PlayerFragment())
                .commit()
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.container, frag)
                .commitNow()
        }
        channelImage.setOnClickListener {
            val activity = v.context as MainActivity
            val bundle = bundleOf("channel_id" to item.uploaderUrl)
            activity.navController.navigate(R.id.channel, bundle)
        }
    }

    private fun populateChannelItem(item: SearchItem) {
        val channelImage = v.findViewById<ImageView>(R.id.search_channel_image)
        Picasso.get().load(item.thumbnail).into(channelImage)
        val channelName = v.findViewById<TextView>(R.id.search_channel_name)
        channelName.text = item.name
        val channelViews = v.findViewById<TextView>(R.id.search_views)
        channelViews.text = item.subscribers.formatShort() + " subscribers • " + item.videos + " videos"
        v.setOnClickListener {
            val activity = v.context as MainActivity
            val bundle = bundleOf("channel_id" to item.url)
            activity.navController.navigate(R.id.channel, bundle)
        }
        // todo sub button
    }

    private fun populatePlaylistItem(item: SearchItem) {
        val playlistImage = v.findViewById<ImageView>(R.id.search_thumbnail)
        Picasso.get().load(item.thumbnail).into(playlistImage)
        val playlistNumber = v.findViewById<TextView>(R.id.search_playlist_number)
        playlistNumber.text = item.videos.toString()
        val playlistName = v.findViewById<TextView>(R.id.search_description)
        playlistName.text = item.name
        val playlistChannelName = v.findViewById<TextView>(R.id.search_name)
        playlistChannelName.text = item.uploaderName
        val playlistVideosNumber = v.findViewById<TextView>(R.id.search_playlist_videos)
        playlistVideosNumber.text = item.videos.toString() + " videos"
        v.setOnClickListener {
            // playlist clicked
            val activity = v.context as MainActivity
            val bundle = bundleOf("playlist_id" to item.url)
            activity.navController.navigate(R.id.playlistFragment, bundle)
        }
    }

    fun bind(searchItem: SearchItem) {
        when {
            searchItem.url!!.startsWith("/watch", false) -> populateVideoItem(searchItem)
            searchItem.url!!.startsWith("/channel", false) -> populateChannelItem(searchItem)
            searchItem.url!!.startsWith("/playlist", false) -> populatePlaylistItem(searchItem)
            else -> {
            }
        }
    }
}
