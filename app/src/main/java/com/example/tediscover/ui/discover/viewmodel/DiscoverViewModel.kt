package com.example.tediscover.ui.discover.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class DiscoverViewModel: ViewModel() {
    private val _duration = MutableLiveData<String>()
    val duration: LiveData<String> = _duration

    private val _topics = MutableLiveData<MutableList<String>>()
    val topics: LiveData<MutableList<String>> = _topics

    private val _userInput = MutableLiveData<String>()
    val userInput: LiveData<String> = _userInput

    private val _sortBy = MutableLiveData<String>()
    val sortBy: LiveData<String> = _sortBy


    init {
        resetDataItems()
    }


    fun resetDataItems() {
        _duration.value = ""
        _topics.value = mutableListOf()
        _userInput.value = ""
        _sortBy.value = ""
    }


    fun setDuration(durationPicked: String) {
        _duration.value = durationPicked
    }


    fun addTopic(topic: String) {
        val newTopics = _topics.value!!
        newTopics.add(topic)
        _topics.value = newTopics
    }


    fun removeTopic(topic: String) {
        val newTopics = _topics.value!!
        newTopics.remove(topic)
        _topics.value = newTopics
    }


    fun setUserInput(input: String) {
        _userInput.value = input
    }


    fun setSortBy(userSort: String) {
        _sortBy.value = userSort
    }
}