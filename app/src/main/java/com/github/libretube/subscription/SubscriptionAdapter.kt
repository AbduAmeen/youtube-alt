package com.github.libretube.subscriptionchannel

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.github.libretube.R
import com.github.libretube.data.network.obj.StreamItem
import com.github.libretube.home.MainActivity
import com.github.libretube.player.PlayerFragment
import com.github.libretube.util.formatShort
import com.squareup.picasso.Picasso

class SubscriptionAdapter(private val videoFeed: List<StreamItem>) : RecyclerView.Adapter<SubscriptionViewHolder>() {
    // private var limitedVideoFeed: MutableList<String> = [""].toMutableList()
    var i = 0
    override fun getItemCount(): Int {
        return i
    }

    fun updateItems() {
        // limitedVideoFeed.add("")
        i += 10
        if (i > videoFeed.size)
            i = videoFeed.size
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cell = layoutInflater.inflate(R.layout.video_feeditem, parent, false)
        return SubscriptionViewHolder(cell)
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        val trending = videoFeed[position]
        holder.v.findViewById<TextView>(R.id.list_item_video_title).text = trending.title
        holder.v.findViewById<TextView>(R.id.list_item_video_upload_info).text = trending.uploaderName + " • " + trending.views?.formatShort() + " • " + DateUtils.getRelativeTimeSpanString(trending.uploaded!!)
        val thumbnailImage = holder.v.findViewById<ImageView>(R.id.list_item_video_thumbnail)
        holder.v.findViewById<TextView>(R.id.list_item_video_duration).text = DateUtils.formatElapsedTime(trending.duration!!)
        val channelImage = holder.v.findViewById<ImageView>(R.id.channel_image)
        channelImage.setOnClickListener {
            val activity = holder.v.context as MainActivity
            val bundle = bundleOf("channel_id" to trending.uploaderUrl)
            // activity.navController.navigate(R.id.channel, bundle)
            try {
                val mainMotionLayout = activity.findViewById<MotionLayout>(R.id.mainMotionLayout)
                if (mainMotionLayout.progress == 0.toFloat()) {
                    mainMotionLayout.transitionToEnd()
                    activity.findViewById<MotionLayout>(R.id.playerMotionLayout).transitionToEnd()
                }
            } catch (e: Exception) {
            }
        }
        Picasso.get().load(trending.thumbnail).into(thumbnailImage)
        Picasso.get().load(trending.uploaderAvatar).into(channelImage)
        holder.v.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("videoId", trending.url!!.replace("/watch?v=", ""))
            var frag = PlayerFragment()
            frag.arguments = bundle
            val activity = holder.v.context as AppCompatActivity
            activity.supportFragmentManager.beginTransaction()
                .remove(PlayerFragment())
                .commit()
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.playerFragmentContainer, frag)
                .commitNow()
        }
    }
}
class SubscriptionViewHolder(val v: View) : RecyclerView.ViewHolder(v) {
    init {
    }
}
