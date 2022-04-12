package com.github.libretube.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.libretube.R
import com.github.libretube.R.id.channel_scrollView
import com.github.libretube.adapters.ChannelAdapter
import com.github.libretube.formatShort
import com.github.libretube.network.PipedApiClient
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso

class ChannelFragment : Fragment() {

    private lateinit var channelId: String
    private var nextPage: String? = null
    private var channelAdapter: ChannelAdapter? = null
    private var isLoading = true
    private var isSubscribed: Boolean = false
    private lateinit var apiClient: PipedApiClient
    private val sharedPref = context?.getSharedPreferences("token", Context.MODE_PRIVATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        channelId = arguments?.getString("channel_id")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_channel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apiClient = PipedApiClient.initialize(requireContext(), ChannelFragment::class.toString())
        val recyclerView = view.findViewById<RecyclerView>(R.id.channel_recView)
        val scrollView = view.findViewById<NestedScrollView>(channel_scrollView)

        channelId = channelId.replace("/channel/", "")
        view.findViewById<TextView>(R.id.channel_name).text = channelId
        recyclerView.layoutManager = LinearLayoutManager(context)
        fetchChannel(view)

        if (sharedPref?.getString("token", "") != "") {
            val subButton = view.findViewById<MaterialButton>(R.id.channel_subscribe)
            isSubscribed(subButton)
        }

        scrollView.viewTreeObserver
            .addOnScrollChangedListener {
                val scrollPercent = scrollView.scrollY / (scrollView.getChildAt(0).bottom.toDouble() - scrollView.height) * 100f
                // scroll view is at bottom
                if (scrollPercent > 70 && nextPage != null && !isLoading
                ) {
                    isLoading = true
                    fetchNextPage()
                }
            }
    }

    private fun isSubscribed(button: MaterialButton) {
        lifecycleScope.launchWhenCreated {
            isSubscribed = apiClient.isSubscribed(channelId) ?: false
            val colorPrimary = TypedValue()
            val colorText = TypedValue()

            with((context as Activity).theme) {
                resolveAttribute(R.attr.colorPrimary, colorPrimary, true)
                resolveAttribute(R.attr.colorOnSurface, colorText, true)
            }

            if (isSubscribed) {
                button.text = getString(R.string.unsubscribe)
                button.setTextColor(colorText.data)
            }

            button.setOnClickListener {
                lifecycleScope.launchWhenCreated {
                    if (isSubscribed) {
                        apiClient.unsubscribe(channelId)
                        button.text = getString(R.string.subscribe)
                        button.setTextColor(colorPrimary.data)
                    } else {
                        apiClient.subscribe(channelId)
                        button.text = getString(R.string.unsubscribe)
                        button.setTextColor(colorText.data)
                    }
                }
            }
        }
    }

    private fun fetchChannel(view: View) {
        lifecycleScope.launchWhenCreated {
            val response = apiClient.fetchChannel(channelId)
            val bannerImage = view.findViewById<ImageView>(R.id.channel_banner)
            val channelImage = view.findViewById<ImageView>(R.id.list_item_video_channel_img)

            nextPage = response?.nextpage
            isLoading = false
            channelAdapter = ChannelAdapter(response?.relatedStreams!!.toMutableList())

            view.findViewById<TextView>(R.id.channel_name).text = response.name
            view.findViewById<TextView>(R.id.channel_subs).text = "${response.subscriberCount.formatShort()} subscribers"
            view.findViewById<TextView>(R.id.video_list_row_description).text = response.description
            view.findViewById<RecyclerView>(R.id.channel_recView).adapter = channelAdapter

            Picasso.get().load(response.bannerUrl).into(bannerImage)
            Picasso.get().load(response.avatarUrl).into(channelImage)
        }
    }

    private fun fetchNextPage() {
        lifecycleScope.launchWhenCreated {
            val response = apiClient.fetchNextPage(channelId, nextPage!!)
            nextPage = response?.nextpage
            channelAdapter?.updateItems(response?.relatedStreams!!)
            isLoading = false
        }
    }

    override fun onDestroyView() {
        val scrollView = view?.findViewById<NestedScrollView>(channel_scrollView)
        scrollView?.viewTreeObserver?.removeOnScrollChangedListener {
        }
        channelAdapter = null
        view?.findViewById<RecyclerView>(R.id.channel_recView)?.adapter = null
        super.onDestroyView()
    }
}
