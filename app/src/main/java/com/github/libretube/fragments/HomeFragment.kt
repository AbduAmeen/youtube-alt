package com.github.libretube.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.libretube.R
import com.github.libretube.adapters.TrendingAdapter
import com.github.libretube.network.PipedApiClient

class HomeFragment : Fragment() {
    private lateinit var apiClient: PipedApiClient
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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        apiClient = PipedApiClient.initialize(requireContext(), HomeFragment::class.toString())
        val recyclerView = view.findViewById<RecyclerView>(R.id.fragment_home_list)

        val progressbar = view.findViewById<ProgressBar>(R.id.progressBar)

        recyclerView.layoutManager = GridLayoutManager(view.context, resources.getInteger(R.integer.grid_items))
        fetchTrending(progressbar, recyclerView)

        refreshLayout = view.findViewById(R.id.home_refresh)
        refreshLayout?.isEnabled = true
        refreshLayout?.setOnRefreshListener {
            fetchTrending(progressbar, recyclerView)
        }
    }

    private fun fetchTrending(progressBar: ProgressBar, recyclerView: RecyclerView) {
        lifecycleScope.launchWhenCreated {
            val response = apiClient.fetchTrending()
            refreshLayout?.isRefreshing = false
            progressBar.visibility = View.GONE

            if (response != null) {
                recyclerView.adapter = TrendingAdapter(response)
            }
        }
    }

    override fun onDestroyView() {
        view?.findViewById<RecyclerView>(R.id.fragment_home_list)?.adapter = null
        refreshLayout = null
        super.onDestroyView()
    }
}
