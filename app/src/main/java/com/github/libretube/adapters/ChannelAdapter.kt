package com.github.libretube.adapters

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.libretube.R
import com.github.libretube.formatShort
import com.github.libretube.fragments.PlayerFragment
import com.github.libretube.obj.StreamItem
import com.squareup.picasso.Picasso

class ChannelAdapter(private val videoFeed: MutableList<StreamItem>) : RecyclerView.Adapter<ChannelViewHolder>() {
    override fun getItemCount(): Int {
        return videoFeed.size
    }
    fun updateItems(newItems: List<StreamItem>) {
        val lastIndex = videoFeed.size - 1
        videoFeed.addAll(newItems)
        notifyItemRangeChanged(lastIndex, newItems.size, newItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cell = layoutInflater.inflate(R.layout.playlist_item_video, parent, false)
        return ChannelViewHolder(cell)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        val trending = videoFeed[position]
        val thumbnailImage = holder.v.findViewById<ImageView>(R.id.video_list_row_thumbnail)

        holder.v.findViewById<TextView>(R.id.video_list_row_description).text = trending.title
        holder.v.findViewById<TextView>(R.id.video_list_row_views).text =
            "${trending.views.formatShort()} • ${DateUtils.getRelativeTimeSpanString(trending.uploaded!!)}"
        holder.v.findViewById<TextView>(R.id.video_list_row_duration).text = DateUtils.formatElapsedTime(trending.duration!!)

//        .launchWhenCreated {
//
//        }
        Picasso.get().load(trending.thumbnail).into(thumbnailImage)

        holder.v.setOnClickListener {
            val bundle = Bundle()
            val frag = PlayerFragment()
            val activity = holder.v.context as AppCompatActivity

            bundle.putString("videoId", trending.url!!.replace("/watch?v=", ""))
            frag.arguments = bundle

            activity.supportFragmentManager.beginTransaction()
                .remove(PlayerFragment())
                .commit()
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.container, frag)
                .commitNow()
        }
    }
}
class ChannelViewHolder(val v: View) : RecyclerView.ViewHolder(v) {
    init {
    }
}
