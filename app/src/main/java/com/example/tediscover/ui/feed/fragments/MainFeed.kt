package com.example.tediscover.ui.feed.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tediscover.R
import com.example.tediscover.TedActivity
import com.example.tediscover.viewmodels.RemoteViewModel
import com.example.tediscover.ui.feed.adapters.EventAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch


class MainFeed : Fragment(R.layout.fragment_main_feed) {

    private lateinit var rvTedTalkRemote: RecyclerView
    private lateinit var remoteViewModel: RemoteViewModel
    lateinit var adapter: EventAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMembers()
        setUpViews(view)
        fetchDoggoImages()
    }


    private fun fetchDoggoImages() {
        lifecycleScope.launch(IO) {
            remoteViewModel.fetchTalks().distinctUntilChanged().collectLatest {
                adapter.submitData(it)
            }
        }
    }


    private fun initMembers() {
        remoteViewModel = defaultViewModelProviderFactory.create(RemoteViewModel::class.java)
        adapter = EventAdapter()
    }


    private fun setUpViews(view: View) {
        rvTedTalkRemote = view.findViewById(R.id.rvTedTalkRemote)
        rvTedTalkRemote.layoutManager = LinearLayoutManager(this.context)
        rvTedTalkRemote.adapter = adapter
    }


    override fun onResume() {
        super.onResume()
        (activity as TedActivity).findViewById<FloatingActionButton>(R.id.my_fab).visibility = View.INVISIBLE
        (activity as TedActivity).makeScroll()
    }
}