package com.app.tvapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.tvapp.R
import com.app.tvapp.data.entities.DBChannel
import com.app.tvapp.databinding.ChannelItemBinding
import com.app.tvapp.ui.PlayerActivity
import com.app.tvapp.viewmodels.MainViewModel
import com.bumptech.glide.Glide

class ChannelsAdapter(
    viewModel: MainViewModel,
) : RecyclerView.Adapter<ChannelsAdapter.ViewHolder>() {

    class ViewHolder(val binding: ChannelItemBinding) : RecyclerView.ViewHolder(binding.root)

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<DBChannel>() {

        override fun areItemsTheSame(oldItem: DBChannel, newItem: DBChannel) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: DBChannel, newItem: DBChannel) =
            oldItem == newItem

    })

    var channels: List<DBChannel>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ChannelItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val channel = channels[position]
        holder.binding.apply {
            root.setOnClickListener { v->
                Intent(v.context,PlayerActivity::class.java).putExtra("channel",channel.url)
                    .also {
                        v.context.startActivity(it)
                    }
            }
            name.text = channel.name
            name.isSelected = true
            categ.text = if(channel.category.isNotEmpty()) channel.category else "Undefined"

            Glide.with(root.context)
                .load(channel.logo)
                .into(img)
        }
    }

    override fun getItemCount() = differ.currentList.size


}