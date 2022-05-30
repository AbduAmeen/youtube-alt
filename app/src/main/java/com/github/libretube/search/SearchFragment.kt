package com.github.libretube.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavGraph
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.libretube.databinding.SearchFragmentBinding
import com.github.libretube.feed.FeedAdapter
import com.github.libretube.home.MainActivityViewModel
import com.github.libretube.util.hideKeyboard
import com.github.libretube.util.onSubmit
import com.github.libretube.util.showKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private val searchViewModel: SearchViewModel by viewModels()
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var _binding: SearchFragmentBinding
    private val binding get() = _binding
    private var isMinimized = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SearchFragmentBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(
            SearchFragment::class.java.toString(),
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
        val suggestionAdapter = SearchSuggestionAdapter(searchViewModel, this)
        val feedAdapter = FeedAdapter(searchViewModel)

        binding.searchViewModel = searchViewModel

        binding.suggestionsRecycler.adapter = suggestionAdapter
        binding.suggestionsRecycler.layoutManager = LinearLayoutManager(view.context)
        binding.suggestionsRecycler.itemAnimator = null

        binding.fragmentSearchList.adapter = feedAdapter
        binding.fragmentSearchList.layoutManager = GridLayoutManager(view.context, 1)

        if (!isMinimized) {
            showKeyboard()
            binding.searchEditText.requestFocus()
        }

        binding.searchEditText.onSubmit {
            searchViewModel.search(binding.searchEditText.text.toString())
        }

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.suggestionsRecycler.visibility = View.VISIBLE
                isMinimized = false
            } else {
                isMinimized = true
            }
        }

        searchViewModel.searchEvent.observe(viewLifecycleOwner) {
            binding.searchEditText.clearFocus()
            hideKeyboard()
            if (!it.isNullOrEmpty()) {
                binding.suggestionsRecycler.visibility = View.GONE
                isMinimized = true
            }
        }

        searchViewModel.searchLoading.observe(viewLifecycleOwner) {
            if (it) {
                binding.fragmentSearchList.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.fragmentSearchList.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }

        initNavigation()
        initToolbar()

//        searchViewModel.searchTerm.observe(viewLifecycleOwner) {
//            searchViewModel.clearSearchResults()
//        }
    }

    private fun initNavigation() {
        searchViewModel.videoClickedEvent.observe(viewLifecycleOwner) {
            if (it != null) {
                activityViewModel.setCurrentPlayingVideo(it.url)
                searchViewModel.setVideo(null)
            }
        }

        searchViewModel.channelClickedEvent.observe(viewLifecycleOwner) {
            if (!it.isNullOrBlank()) {
                val action = SearchFragmentDirections.navigateToChannel(it)
                findNavController().navigate(action)
                searchViewModel.setChannel(null)
            }
        }

        searchViewModel.playlistClickedEvent.observe(viewLifecycleOwner) {
            if (!it.isNullOrBlank()) {
                val action = SearchFragmentDirections.navigateToPlaylist(it)
                findNavController().navigate(action)
                searchViewModel.setPlaylist(null)
            }
        }
    }

    private fun initToolbar() {
        binding.searchToolbar.setupWithNavController(findNavController(), AppBarConfiguration(findNavController().graph))
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }
}
