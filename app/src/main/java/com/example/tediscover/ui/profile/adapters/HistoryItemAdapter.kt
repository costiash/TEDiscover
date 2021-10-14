package com.example.tediscover.ui.profile.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tediscover.R
import com.example.tediscover.TedActivity
import com.example.tediscover.databinding.ProfileItemBinding
import com.example.tediscover.ui.models.EventItem
import com.example.tediscover.ui.models.TalkItem
import com.example.tediscover.ui.profile.fragments.ProfileDirections


class HistoryItemAdapter
    : ListAdapter<TalkItem, HistoryItemAdapter.TalkItemViewHolder>(DiffCallback) {


    companion object DiffCallback: DiffUtil.ItemCallback<TalkItem>() {
        override fun areItemsTheSame(oldItem: TalkItem, newItem: TalkItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TalkItem, newItem: TalkItem): Boolean {
            return oldItem.videoUrl == newItem.videoUrl
        }
    }


    class TalkItemViewHolder(private var binding: ProfileItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(TalkItem: TalkItem) = with(itemView) {
            binding.talkItem = TalkItem
            binding.executePendingBindings()

            val eventItem = EventItem(TalkItem.id.toInt(), TalkItem.thumbImage, TalkItem.duration,
                TalkItem.speaker, TalkItem.title, TalkItem.videoUrl, TalkItem.date)

            binding.moreBtn.setOnClickListener {
                (context as TedActivity).showHistoryMenu(binding.moreBtn, R.menu.popup_menu, TalkItem)
            }

            itemView.setOnClickListener {
                val action = ProfileDirections.actionNavigationProfileToVideoDetailFragment(eventItem)
                findNavController().navigate(action)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TalkItemViewHolder {
        return TalkItemViewHolder(ProfileItemBinding.inflate(LayoutInflater.from(parent.context)))
    }


    override fun onBindViewHolder(holder: TalkItemViewHolder, position: Int) {
        val talkItem = getItem(position)
        holder.bind(talkItem)
    }
}