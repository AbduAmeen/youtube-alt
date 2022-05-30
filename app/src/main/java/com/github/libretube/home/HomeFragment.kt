package com.github.libretube.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavGraph
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.github.libretube.R
import com.github.libretube.databinding.FragmentHomeBinding
import com.github.libretube.feed.FeedAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val homeViewModel: HomeViewModel by viewModels()
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val mainActivity get() = requireActivity() as MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
//        setExitSharedElementCallback(object : SharedElementCallback() {
//            override fun onMapSharedElements(
//                names: List<String?>,
//                sharedElements: MutableMap<String?, View?>
//            ) {
//                // Locate the ViewHolder for the clicked position.
//                val selectedViewHolder: RecyclerView.ViewHolder? = binding.fragmentHomeList
//                    .findViewHolderForAdapterPosition(playerViewModel.currentScrollPositon.value!!)
//
//                // Map the first shared element name to the child ImageView.
//                sharedElements["shared_element_container"] =
//                    selectedViewHolder?.itemView?.findViewById(R.id.list_item_video_thumbnail)
//            }
//        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        Log.d(
            HomeFragment::class.java.toString(),
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

        val adapter = FeedAdapter(homeViewModel)

        binding.homeRefresh.isEnabled = true

        binding.fragmentHomeList.adapter = adapter
        binding.fragmentHomeList.layoutManager = GridLayoutManager(view.context, resources.getInteger(R.integer.grid_items))

        binding.progressBar.visibility = View.VISIBLE

        homeViewModel.videoFeed.observe(viewLifecycleOwner) {
            adapter.submitList(it) {
                binding.homeRefresh.isRefreshing = false
                binding.progressBar.visibility = View.GONE
            }
        }

        initNavigation()

        mainActivity.bindPlayerChannelClickedEvent(viewLifecycleOwner, findNavController())

        initToolbar()

        binding.homeRefresh.isEnabled = true

        binding.homeRefresh.setOnRefreshListener {
            homeViewModel.updateFeed(true)
            binding.homeRefresh.isRefreshing = true
        }
    }

    private fun initNavigation() {
        homeViewModel.videoClickedEvent.observe(viewLifecycleOwner) {
            if (it != null) {
                activityViewModel.setCurrentPlayingVideo(it.url)
                homeViewModel.setVideo(null)
            }
        }

        homeViewModel.channelClickedEvent.observe(viewLifecycleOwner) {
//            activityViewModel.setChannel(it)
            if (!it.isNullOrBlank()) {
                val action = HomeFragmentDirections.navigateToChannel(it)
                findNavController().navigate(action)
                // TODO: Change this logic to act as an event
                homeViewModel.setChannel("")
            }
        }

//        playerViewModel.channelClickedEvent.observe(viewLifecycleOwner) {
//            if (!it.isNullOrBlank()) {
//                val action = HomeFragmentDirections.navigateToChannel(it)
//                findNavController().navigate(action)
// //                binding.navHostFragment.getFragment<Fragment>().childFragmentManager.commit {
// //                    replace<ChannelFragment>(R.id.navHostFragment, null, args)
// //                    addToBackStack(null)
// //                }
//                //TODO: Change this logic to act as an event
//                playerViewModel.setChannel("")
//            }
//        }

//        mainActivity.setupPlayerChannelClicked(viewLifecycleOwner, findNavController())

//        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
//            findNavController().popBackStack()
//        }
    }

    private fun initToolbar() {
        val hexColor = String.format("#%06X", 0xFFFFFF and 0xcc322d)

        binding.homeToolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.searchFragment -> {
                    it.onNavDestinationSelected(findNavController())
                }
            }
            false
        }

        binding.homeToolbar.setupWithNavController(findNavController(), AppBarConfiguration(findNavController().graph))

        binding.homeToolbar.title = HtmlCompat.fromHtml(
            "Libre<span  style='color:$hexColor';>Tube</span>",
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )
    }
}
