package com.example.tediscover.utils

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.tediscover.R
import com.example.tediscover.ui.models.PlaylistData
import com.example.tediscover.ui.models.TalkItem
import com.example.tediscover.ui.profile.adapters.HistoryItemAdapter
import com.example.tediscover.ui.profile.adapters.PlaylistAdapter
import com.example.tediscover.ui.profile.adapters.PlaylistListAdapter
import com.example.tediscover.ui.profile.adapters.ProfileItemAdapter


@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        imgView.load(imgUri) {
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
        }
    }
}


@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<TalkItem>?) {
    val adapter = recyclerView.adapter as ProfileItemAdapter
    adapter.submitList(data)
}


@BindingAdapter("historyListData")
fun bindHistoryRecyclerView(recyclerView: RecyclerView, data: List<TalkItem>?) {
    val adapter = recyclerView.adapter as HistoryItemAdapter
    adapter.submitList(data)
}


@BindingAdapter("playlistData")
fun bindPlaylistRecyclerView(recyclerView: RecyclerView, data: List<PlaylistData>?) {
    val adapter = recyclerView.adapter as PlaylistAdapter
    adapter.submitList(data)
}


@BindingAdapter("playlistListData")
fun bindPlaylistListRecyclerView(recyclerView: RecyclerView, data: List<TalkItem>?) {
    val adapter = recyclerView.adapter as PlaylistListAdapter
    adapter.submitList(data)
}