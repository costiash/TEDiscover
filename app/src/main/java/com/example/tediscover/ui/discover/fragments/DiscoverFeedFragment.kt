package com.example.tediscover.ui.discover.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tediscover.R
import com.example.tediscover.TedActivity
import com.example.tediscover.viewmodels.RemoteViewModel
import com.example.tediscover.ui.discover.adapters.SearchEventAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch


class DiscoverFeedFragment : Fragment(R.layout.fragment_discover_feed) {

    private lateinit var rvTedTalkRemote: RecyclerView
    private lateinit var remoteViewModel: RemoteViewModel
    private lateinit var userAdapter: SearchEventAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = DiscoverFeedFragmentArgs.fromBundle(requireArguments())
        val searchQuery = args.searchItem

        val sortBy = if (searchQuery!!.sortBy == "Most viewed") {
            "Popular"
        } else {
            searchQuery.sortBy
        }
        val baseUrl = "sort=${sortBy.lowercase()}"

        val finalUrl = with(baseUrl) {
            val temp1 = addUserInput(this, searchQuery.userQuery)
            val temp2 = addTopics(temp1, searchQuery.topics)
            addDuration(temp2, searchQuery.duration)
        }

        initMembers()
        setUpViews(view)
        fetchDoggoImages(finalUrl)
    }


    private fun fetchDoggoImages(urlQueries: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            remoteViewModel.fetchDiscoverTalks(urlQueries).distinctUntilChanged().collectLatest {
                userAdapter.submitData(it)
            }
        }
    }


    private fun initMembers() {
        remoteViewModel = defaultViewModelProviderFactory.create(RemoteViewModel::class.java)
        userAdapter = SearchEventAdapter()
    }


    private fun setUpViews(view: View) {
        rvTedTalkRemote = view.findViewById(R.id.rvTedTalkDiscoverRemote)
        rvTedTalkRemote.layoutManager = LinearLayoutManager(this.context)
        rvTedTalkRemote.adapter = userAdapter
    }


    override fun onResume() {
        super.onResume()
        (activity as TedActivity).findViewById<FloatingActionButton>(R.id.my_fab).visibility =
            View.INVISIBLE
    }


    private fun addUserInput(baseUrl: String, input: String): String {
        var url = baseUrl
        if (input.isNotBlank())
            url += "&q=${formatSpacing(input)}"
        return url
    }


    private fun addTopics(baseUrl: String, topics: MutableList<String>): String {
        var url = baseUrl
        if (topics.isNotEmpty()) {
            for (topicTile in topics) {
                url += "&topics%5B%5D=${formatSpacing(topicTile)}"
            }
        }
        return url
    }


    private fun addDuration(baseUrl: String, duration: String): String {
        var url = baseUrl
        if (duration.isNotBlank()) {
            url += if (duration == "18+ minutes")
                "&duration=18%2B"
            else
                "&duration=${duration.split(" ")[0]}"
        }
        return url
    }


    private fun formatSpacing(str: String): String {
        var tempStr = str
        val words = str.split(" ")
        if (words.size > 1) {
            tempStr = words.joinToString(separator = "+")
        }
        return tempStr
    }
}