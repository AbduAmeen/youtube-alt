package com.github.libretube.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.libretube.databinding.PlaylistFragmentBinding
import com.github.libretube.feed.FeedAdapter
import com.github.libretube.home.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistFragment : Fragment() {
    private val playlistViewModel: PlaylistViewModel by viewModels()
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var _binding: PlaylistFragmentBinding
    val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = PlaylistFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // apiClient = PipedApiClient.initialize(requireContext(), PlaylistFragment::class.toString())

        binding.playlistViewModel = playlistViewModel
        binding.playlistVideos.layoutManager = LinearLayoutManager(context)
        binding.playlistVideos.adapter = FeedAdapter(playlistViewModel)

        initToolbar()
        initNavigation()
    }

    private fun initToolbar() {
        binding.playlistToolbar.setupWithNavController(findNavController(), AppBarConfiguration(findNavController().graph))
        binding.playlistToolbar.title = null
    }

    private fun initNavigation() {
        playlistViewModel.videoClickedEvent.observe(viewLifecycleOwner) {
            if (it != null) {
                activityViewModel.setCurrentPlayingVideo(it.url)
                playlistViewModel.setVideo(null)
            }
        }
    }
}
