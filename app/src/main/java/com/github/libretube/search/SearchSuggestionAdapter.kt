package com.github.libretube.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.libretube.databinding.SearchSuggestionItemBinding

class SearchSuggestionAdapter(private val viewModel: SearchViewModel, lifecycleOwner: LifecycleOwner) :
    ListAdapter<String, SuggestionViewHolder>(SearchDiffCallback()) {

    init {
        viewModel.suggestions.observe(lifecycleOwner) {
            submitList(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        return SuggestionViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        holder.bind(viewModel.suggestions.value!![position])
        holder.itemView.setOnClickListener {
            viewModel.search(holder.binding.suggestionText.text.toString())
        }
    }
}

class SearchDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return areItemsTheSame(oldItem, newItem)
    }
}

class SuggestionViewHolder private constructor(val binding: SearchSuggestionItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(searchItem: String) {
        binding.suggestionText.text = searchItem
    }

    companion object {
        fun from(parent: ViewGroup): SuggestionViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = SearchSuggestionItemBinding.inflate(layoutInflater, parent, false)

            return SuggestionViewHolder(binding)
        }
    }
}
