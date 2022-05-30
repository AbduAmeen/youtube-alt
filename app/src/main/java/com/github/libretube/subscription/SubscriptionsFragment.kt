package com.github.libretube.subscription

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.libretube.R
import com.github.libretube.data.network.PipedApiClient
import com.github.libretube.subscriptionchannel.SubscriptionAdapter
import com.github.libretube.subscriptionchannel.SubscriptionChannelAdapter
import kotlinx.coroutines.launch

class SubscriptionsFragment : Fragment() {
    private lateinit var apiClient: PipedApiClient
    private lateinit var token: String
    private var isLoaded = false
    private var subscriptionAdapter: SubscriptionAdapter? = null
    private var refreshLayout: SwipeRefreshLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subscriptions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = context?.getSharedPreferences("token", Context.MODE_PRIVATE)
        val progressBar = view.findViewById<ProgressBar>(R.id.sub_progress)
        val channelRecView = view.findViewById<RecyclerView>(R.id.sub_channels)
        val feedRecView = view.findViewById<RecyclerView>(R.id.sub_feed)
        val scrollView = view.findViewById<ScrollView>(R.id.scrollview_sub)

        // apiClient = PipedApiClient.initialize(requireContext(), SubscriptionsFragment::class.toString())
        token = sharedPref?.getString("token", "")!!
        refreshLayout = view.findViewById(R.id.sub_refresh)

        if (token.isEmpty()) {
            refreshLayout?.isEnabled = false
            return
        }

        fetchSubscriptionFeed(feedRecView, progressBar)
        fetchSubscribedChannels(channelRecView)

        progressBar.visibility = View.VISIBLE
        view.findViewById<RelativeLayout>(R.id.loginOrRegister).visibility = View.GONE
        refreshLayout?.isEnabled = true

        channelRecView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        feedRecView.layoutManager = GridLayoutManager(view.context, resources.getInteger(R.integer.grid_items))
        refreshLayout?.setOnRefreshListener {
            fetchSubscribedChannels(channelRecView)
            fetchSubscriptionFeed(feedRecView, progressBar)
        }

        scrollView.viewTreeObserver.addOnScrollChangedListener {
            if (scrollView.getChildAt(0).bottom == (scrollView.height + scrollView.scrollY)) {
                // scroll view is at bottom
                if (isLoaded) {
                    refreshLayout?.isRefreshing = true
                    subscriptionAdapter?.updateItems()
                    refreshLayout?.isRefreshing = false
                }
            }
        }
    }

    private fun fetchSubscriptionFeed(feedRecView: RecyclerView, progressBar: ProgressBar) {
        lifecycleScope.launch {
            val response = apiClient.fetchSubscriptionFeed(token)

            refreshLayout?.isRefreshing = false

            if (!response.isNullOrEmpty()) {
                subscriptionAdapter = SubscriptionAdapter(response)
                feedRecView.adapter = subscriptionAdapter
                subscriptionAdapter?.updateItems()
            }
            progressBar.visibility = View.GONE
            isLoaded = true
        }
    }

    private fun fetchSubscribedChannels(channelRecView: RecyclerView) {
        lifecycleScope.launch {
            val response = apiClient.fetchSubscriptions(token)

            refreshLayout?.isRefreshing = false

            if (!response.isNullOrEmpty()) {
                channelRecView.adapter = SubscriptionChannelAdapter(response.toMutableList())
            } else {
                Toast.makeText(context, R.string.subscribeIsEmpty, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStop() {
        subscriptionAdapter = null
        view?.findViewById<RecyclerView>(R.id.sub_feed)?.adapter = null
        super.onStop()
    }
}
