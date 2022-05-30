package com.github.libretube.channel

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavGraph
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.github.libretube.R
import com.github.libretube.databinding.ChannelFragmentBinding
import com.github.libretube.feed.FeedFragment
import com.github.libretube.home.MainActivity
import com.github.libretube.home.MainActivityViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChannelFragment : Fragment() {

    private val activityViewModel: MainActivityViewModel by activityViewModels()
    val channelViewModel: ChannelViewModel by viewModels()
    private var _binding: ChannelFragmentBinding? = null
    private val binding get() = _binding!!
    private val tabTitles = listOf("Home", "Videos")
    private val mainActivity get() = requireActivity() as MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = ChannelFragmentBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
//
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(
            ChannelFragment::class.java.toString(),
            "NavController: ${findNavController().backQueue
                .map {
                    it.destination
                }
                .filterNot {
                    it is NavGraph
                }
                .joinToString(" > ") {
                    it.displayName.split('/')[1]
                }}"
        )
        binding.channelViewModel = channelViewModel
        binding.pages.adapter = ChannelFragmentAdapter(this)

        TabLayoutMediator(binding.tabs, binding.pages) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        mainActivity.bindPlayerChannelClickedEvent(viewLifecycleOwner, findNavController())

        initToolbar()
        initNavigation()
    }

    private fun initToolbar() {
        val configuration = AppBarConfiguration(findNavController().graph)
        binding.channelToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.searchFragment -> {
                    it.onNavDestinationSelected(findNavController())
                }
            }
            false
        }

        binding.channelToolbar.setNavigationOnClickListener {
            if (findNavController().currentDestination?.id == R.id.playerFragmentContainer) {
                findNavController().clearBackStack(R.id.playerFragmentContainer)
            }
            NavigationUI.navigateUp(findNavController(), configuration)
        }

        binding.channelToolbar.setupWithNavController(findNavController(), configuration)

        binding.channelToolbar.title = channelViewModel.channelName.value
    }

    private fun initNavigation() {
        channelViewModel.videoClickedEvent.observe(viewLifecycleOwner) {
            if (it != null) {
                activityViewModel.setCurrentPlayingVideo(it.url)
                channelViewModel.setVideo(null)
            }
//        mainActivity.setupPlayerChannelClicked(viewLifecycleOwner, findNavController())

//        mainActivity.onBackPressedDispatcher.addCallback {
//            val currentItem = binding.pages.currentItem
//            if (currentItem != 0) {
//                binding.pages.currentItem = currentItem - 1
//            } else {
//                isEnabled = false
//                mainActivity.onBackPressed()
//            }
        }
    }
}

class ChannelFragmentAdapter(private val fragment: ChannelFragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> ChannelMainPageFragment(fragment.channelViewModel)
        else -> FeedFragment(fragment.channelViewModel.videos, fragment.channelViewModel)
    }
}
