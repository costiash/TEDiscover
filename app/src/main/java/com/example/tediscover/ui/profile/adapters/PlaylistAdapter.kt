package com.example.tediscover.ui.profile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tediscover.R
import com.example.tediscover.databinding.PlaylistItemBinding
import com.example.tediscover.ui.models.PlaylistData


class PlaylistAdapter(val context: Context, private val listener: OnClickListener) :
    ListAdapter<PlaylistData, PlaylistAdapter.PlaylistViewHolder>(DiffCallback) {

    // define the listener
    interface OnClickListener {
        fun onItemClick(playlistData: PlaylistData)
    }


    companion object DiffCallback: DiffUtil.ItemCallback<PlaylistData>() {
        override fun areItemsTheSame(oldItem: PlaylistData, newItem: PlaylistData): Boolean {
            return oldItem.playlistName == newItem.playlistName
        }

        override fun areContentsTheSame(oldItem: PlaylistData, newItem: PlaylistData): Boolean {
            return oldItem.playlistItems == newItem.playlistItems
        }
    }


    inner class PlaylistViewHolder(private var binding: PlaylistItemBinding)
        : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        fun bind(PlaylistData: PlaylistData) = with(itemView) {
            binding.playlistData = PlaylistData
            binding.playlistNumOfItems.text = context.getString(R.string.num_of_items, PlaylistData.videosNumber)
            binding.executePendingBindings()
        }

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.onItemClick(binding.playlistData!!)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        return PlaylistViewHolder(PlaylistItemBinding.inflate(LayoutInflater.from(parent.context)))
    }


    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val talkItem = getItem(position)
        holder.bind(talkItem)
    }
}