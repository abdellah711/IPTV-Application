package com.app.tvapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.tvapp.R
import com.app.tvapp.data.entities.ChannelWithLangs
import com.app.tvapp.data.entities.Language
import com.app.tvapp.databinding.ChannelItemBinding
import com.app.tvapp.ui.PlayerActivity
import com.app.tvapp.viewmodels.MainViewModel
import com.bumptech.glide.Glide
import java.util.*

class ChannelsAdapter(
    viewModel: MainViewModel,
) : RecyclerView.Adapter<ChannelsAdapter.ViewHolder>() {

    class ViewHolder(val binding: ChannelItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(channelWithLangs: ChannelWithLangs?) {
            val channel = channelWithLangs?.channel ?: return

            binding.apply {
                root.setOnClickListener { v ->
                    Intent(v.context, PlayerActivity::class.java).putExtra("channel", channel)
                        .also {
                            v.context.startActivity(it)
                        }
                }
                name.text = channel.name
                name.isSelected = true
                categ.text = if (channel.category.isNotEmpty()) channel.category else "Undefined"

                Glide.with(root.context)
                    .load(channel.logo)
                    .error(R.drawable.ic_no_logo)
                    .into(img)


                lang.text =
                    if (channelWithLangs.langs.isNotEmpty())
                        channelWithLangs.langs.reduce { acc, language ->
                            Language(
                                acc.name.capitalize(
                                    Locale.getDefault()
                                ) + " · " + language.name.capitalize(
                                    Locale.getDefault()
                                ),
                                ""
                            )
                        }.name
                    else
                        "Undefined"
            }
        }
    }

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<ChannelWithLangs>() {

        override fun areItemsTheSame(oldItem: ChannelWithLangs, newItem: ChannelWithLangs) =
            oldItem.channel.id == newItem.channel.id

        override fun areContentsTheSame(oldItem: ChannelWithLangs, newItem: ChannelWithLangs) =
            oldItem == newItem

    })

    var channels: List<ChannelWithLangs>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ChannelItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(channels[position])
    }

    override fun getItemCount() = differ.currentList.size

}
