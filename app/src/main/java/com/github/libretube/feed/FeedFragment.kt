package com.github.libretube.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.libretube.databinding.FeedFragmentBinding

class FeedFragment<T : FeedItem>(private val feedList: LiveData<List<T>?>, private val feedViewModel: FeedViewModel) : Fragment() {
    private var _binding: FeedFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FeedFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycler.adapter = FeedAdapter(feedViewModel)
        binding.recycler.layoutManager = LinearLayoutManager(context)

        feedList.observe(viewLifecycleOwner) {
            binding.list = it
        }
    }
}
