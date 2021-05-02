package com.app.tvapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.tvapp.data.entities.ChannelWithLangs
import com.app.tvapp.data.entities.DBChannel
import com.app.tvapp.databinding.ChannelItemBinding
import com.app.tvapp.ui.PlayerActivity
import com.app.tvapp.viewmodels.MainViewModel
import com.bumptech.glide.Glide

class ChannelsPagingAdapter(
    viewModel: MainViewModel
) : PagingDataAdapter<ChannelWithLangs,ChannelsAdapter.ViewHolder>(differCallback) {

    companion object {
        private val differCallback = object : DiffUtil.ItemCallback<ChannelWithLangs>() {

            override fun areItemsTheSame(oldItem: ChannelWithLangs, newItem: ChannelWithLangs) =
                oldItem.channel.id == newItem.channel.id

            override fun areContentsTheSame(oldItem: ChannelWithLangs, newItem: ChannelWithLangs) =
                oldItem == newItem

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ChannelsAdapter.ViewHolder(
            ChannelItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ChannelsAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
}


}
