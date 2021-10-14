package com.example.tediscover.ui.profile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tediscover.databinding.ProfileItemBinding
import com.example.tediscover.ui.models.EventItem
import com.example.tediscover.ui.models.TalkItem


class PlaylistListAdapter(private val listener: OnClickListener)
    : ListAdapter<TalkItem, PlaylistListAdapter.TalkItemViewHolder>(DiffCallback) {


    // define the listener
    interface OnClickListener {
        fun onItemClick(eventItem: EventItem)
        fun onBtnMoreClick(btn: View, talkItem: TalkItem)
    }


    companion object DiffCallback: DiffUtil.ItemCallback<TalkItem>() {
        override fun areItemsTheSame(oldItem: TalkItem, newItem: TalkItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TalkItem, newItem: TalkItem): Boolean {
            return oldItem.videoUrl == newItem.videoUrl
        }
    }


    inner class TalkItemViewHolder(private var binding: ProfileItemBinding)
        : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        var eventItem: EventItem? = null

        fun bind(TalkItem: TalkItem) = with(itemView) {
            binding.talkItem = TalkItem
            binding.executePendingBindings()

            eventItem = EventItem(TalkItem.id.toInt(), TalkItem.thumbImage, TalkItem.duration,
                TalkItem.speaker, TalkItem.title, TalkItem.videoUrl, TalkItem.date)
        }

        init {
            itemView.setOnClickListener(this)
            binding.moreBtn.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (v is ImageView){
                listener.onBtnMoreClick(binding.moreBtn, binding.talkItem!!)
            } else {
                listener.onItemClick(eventItem!!)
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