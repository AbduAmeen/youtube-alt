package com.github.libretube.channel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.libretube.databinding.ChannelMainPageFragmentBinding
import com.github.libretube.feed.FeedAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChannelMainPageFragment(private val channelViewModel: ChannelViewModel) : Fragment() {
    private var _binding: ChannelMainPageFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ChannelMainPageFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
    //
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.channelViewModel = channelViewModel
        binding.channelRecView.adapter = FeedAdapter(channelViewModel)
        binding.channelRecView.layoutManager = LinearLayoutManager(context)
    }
}
