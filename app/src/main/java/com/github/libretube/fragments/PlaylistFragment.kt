package com.github.libretube.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.libretube.R
import com.github.libretube.adapters.PlaylistAdapter
import com.github.libretube.network.PipedApiClient

class PlaylistFragment : Fragment() {
    private lateinit var playlistId: String
    private var nextPage: String? = null
    private var playlistAdapter: PlaylistAdapter? = null
    private var isLoading = true

    private lateinit var apiClient: PipedApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            playlistId = it.getString("playlist_id")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.playlist_recView)

        apiClient = PipedApiClient.initialize(requireContext(), PlaylistFragment::class.toString())
        playlistId = playlistId.replace("/playlist?list=", "")

        view.findViewById<TextView>(R.id.playlist_name).text = playlistId
        recyclerView.layoutManager = LinearLayoutManager(context)

        fetchPlaylist(view)
    }
    private fun fetchPlaylist(view: View) {
        lifecycleScope.launchWhenCreated {
            val response = apiClient.fetchPlaylist(playlistId)
            val scrollView = view.findViewById<ScrollView>(R.id.playlist_scrollview)

            nextPage = response?.nextpage
            isLoading = false
            playlistAdapter = response?.relatedStreams?.let {
                PlaylistAdapter(it.toMutableList())
            }

            view.findViewById<TextView>(R.id.playlist_name).text = response?.name
            view.findViewById<TextView>(R.id.playlist_uploader).text = response?.uploader
            view.findViewById<TextView>(R.id.playlist_totVideos).text = "${response?.videos} Videos"
            view.findViewById<RecyclerView>(R.id.playlist_recView).adapter = playlistAdapter

            scrollView.viewTreeObserver.addOnScrollChangedListener {
                if (scrollView.getChildAt(0).bottom == (scrollView.height + scrollView.scrollY)) {
                    // scroll view is at bottom
                    if (nextPage != null && !isLoading) {
                        isLoading = true

                        lifecycleScope.launchWhenCreated {
                            val nextPageResponse = apiClient.fetchNextPage(playlistId, nextPage!!)
                            nextPage = nextPageResponse?.nextpage
                            playlistAdapter?.updateItems(nextPageResponse?.relatedStreams!!)
                            isLoading = false
                        }
                    }
                }
            }
        }
    }
}
