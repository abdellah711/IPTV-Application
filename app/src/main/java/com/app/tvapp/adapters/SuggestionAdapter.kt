package com.app.tvapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.tvapp.data.entities.Suggestion
import com.app.tvapp.databinding.SuggestionItemBinding
import com.app.tvapp.viewmodels.MainViewModel

class SuggestionAdapter(
    private val viewModel: MainViewModel,
    private val onSuggestionClick: (String)->Unit
) : RecyclerView.Adapter<SuggestionAdapter.ViewHolder>() {

    class ViewHolder(val binding: SuggestionItemBinding) : RecyclerView.ViewHolder(binding.root)

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Suggestion>() {
        override fun areItemsTheSame(oldItem: Suggestion, newItem: Suggestion) =
            oldItem.suggestion == newItem.suggestion

        override fun areContentsTheSame(oldItem: Suggestion, newItem: Suggestion) =
            oldItem.suggestion == newItem.suggestion
    })

    var suggestions: List<Suggestion>
        set(value) = differ.submitList(value)
        get() = differ.currentList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            SuggestionItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val suggestion = differ.currentList[position]
            txt.text = suggestion.suggestion
            txt.setOnClickListener {
                onSuggestionClick(suggestion.suggestion)
            }
            icon.setOnClickListener {
                viewModel.deleteSuggestion(suggestion)
            }
        }
    }

    override fun getItemCount()= differ.currentList.size
}