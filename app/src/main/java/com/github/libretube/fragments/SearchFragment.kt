package com.github.libretube.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.libretube.R
import com.github.libretube.adapters.SearchAdapter
import com.github.libretube.network.PipedApiClient
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    private lateinit var apiClient: PipedApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.fragment_search_list)
        val autoTextView = view.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        apiClient = PipedApiClient.initialize(requireContext(), SearchFragment::class.toString())

        autoTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrBlank()) {
                    return
                }
                // search suggestions
                lifecycleScope.launch {
                    val response = apiClient.fetchSuggestions(s.toString())

                    if (response != null) {
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, response)
                        autoTextView.setAdapter(adapter)
                    }
                }

                // search results
                lifecycleScope.launch {
                    val response = apiClient.fetchSearch(s.toString())

                    if (response?.items != null) {
                        recyclerView.adapter = SearchAdapter(response.items)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        autoTextView.requestFocus()
        recyclerView.layoutManager = GridLayoutManager(view.context, 1)
        imm.showSoftInput(autoTextView, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }
}
