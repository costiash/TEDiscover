package com.example.tediscover.ui.feed.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.tediscover.BR
import com.example.tediscover.R
import com.example.tediscover.databinding.EventItemBinding
import com.example.tediscover.ui.feed.fragments.MainFeedDirections
import com.example.tediscover.ui.models.EventItem


class EventAdapter : PagingDataAdapter<EventItem, EventAdapter.ViewHolder>(REPO_COMPARATOR) {

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<EventItem>() {
            override fun areItemsTheSame(oldItem: EventItem, newItem: EventItem): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: EventItem, newItem: EventItem): Boolean =
                oldItem == newItem
        }
    }


    class ViewHolder(private var binding: EventItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EventItem) = with(itemView) {
            //use two way binding
            //BR - is auto-generating class
            binding.setVariable(BR.item, item)
            // apply changes
            binding.executePendingBindings()

            val imgUri = item.thumbImage.toUri().buildUpon().scheme("https").build()

            binding.tedPhoto.load(imgUri) {
                placeholder(R.drawable.loading_animation)
                error(R.drawable.ic_broken_image)
            }

            itemView.setOnClickListener {
                // navigate to other fragment with Safe Args
                val action = MainFeedDirections.actionNavigationFeedToVideoDetailFragment(item)
                findNavController().navigate(action)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = EventItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val eventItem = getItem(position)
        holder.bind(eventItem!!)
    }
}